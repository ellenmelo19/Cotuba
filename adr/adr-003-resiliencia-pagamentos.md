# ADR 003 — Resiliência de pagamentos (Circuit Breaker, fila e Idempotent Receiver)

| Campo | Valor |
| --- | --- |
| **Status** | Aceito |
| **Data** | 2026-07-19 |
| **Decisores** | Time de Engenharia Cotubify |

## Contexto

No lançamento exclusivo do livro de Paul Rabbit (Black Friday, 50% off), cerca de **100 mil** cliques em “Comprar” chegaram quase ao mesmo tempo. A plataforma Cotubify (já com **filas** e **cache** — ADRs 001 e 002) sustentou o tráfego, mas o **Gateway de Pagamento** externo:

1. passou a responder em ~**40 segundos**;
2. em seguida **saiu do ar**.

A **API Principal** chamava o gateway de forma **síncrona** na thread do request HTTP. Resultado:

- threads da API ficaram **presas** aguardando o parceiro → colapso de disponibilidade;
- usuários acharam que a tela travou e clicaram “Comprar” várias vezes;
- quando o gateway voltou, houve **cobranças duplicadas** (5–6 vezes pelo mesmo livro).

Premissa: a rede e o parceiro **vão falhar de novo**. O Cotubify não pode cair junto nem cobrar em duplicidade.

Referências: padrões de estabilidade (*Circuit Breaker*, *Timeouts*, *Bulkheads*) em *Release It!* (Nygard) e *Idempotent Receiver* em *Patterns of Distributed Systems* (Joshi).

## Opções consideradas

### Opção A — Manter cobrança síncrona e devolver erro (“tente mais tarde”)

- **Prós:** Modelo mental simples; não cria pedido sem confirmação imediata do cartão.
- **Contras:** Em Black Friday, erro em massa destrói conversão; cliques repetidos continuam bombardeando a API e o gateway; se alguém aumentar timeout para “esperar o parceiro”, o *thread pool* volta a travar.

### Opção B — Síncrono com Circuit Breaker + timeout curto (sem fila de pagamento)

- **Prós:** Protege a API do parceiro lento (falha rápida quando o circuito abre); timeouts evitam espera de 40s.
- **Contras:** Com o circuito aberto, o usuário só vê **erro**; picos geram tempestade de retries manuais; **não resolve** sozinho cobrança duplicada (precisa de idempotência à parte); a thread do request ainda toca o gateway no caminho feliz (acoplamento de latência).

### Opção C — Aceitar o pedido de forma assíncrona + fila de pagamento + Circuit Breaker no worker + Idempotent Receiver

- **Prós:** A API responde rápido (`202 Accepted` / “pagamento em processamento”) e **não bloqueia** threads no gateway; o worker isola a integração instável (*bulkhead*); *Circuit Breaker* + timeout no worker evitam martelar o parceiro caído; *Idempotent Receiver* (chave de idempotência) faz cliques repetidos reutilizarem o **mesmo** pedido/cobrança; quando o gateway volta, a fila drena com **uma** tentativa efetiva de cobrança por pedido.
- **Contras:** UX deixa de ser “aprovado na hora” — o leitor vê status pendente e depois notificação; exige pedido persistido, fila, worker e regras claras de retry/DLQ; eventual inconsistência curta entre “pedido aceito” e “pagamento confirmado”.

## Decisão

Adotamos a **Opção C**, combinando decisão de **negócio** e de **engenharia**:

### Decisão de negócio

- **Não** devolvemos apenas “volte mais tarde” no pico: **aceitamos o pedido** e processamos o pagamento **assincronamente**.
- Enquanto o pagamento não confirma, o download do e-book permanece bloqueado; o leitor é notificado (e-mail/push) quando o status virar `PAGO` ou `FALHA_PAGAMENTO`.

### Decisão técnica

1. **Idempotent Receiver (anti-cobrança duplicada)**  
   - O cliente envia `Idempotency-Key` estável por tentativa de checkout (ex.: UUID gerado no Frontend ao abrir o checkout, ou hash `leitorId + livroId + checkoutSessionId`).  
   - A API faz *upsert* do pedido com **unicidade** dessa chave no banco.  
   - Clique repetido com a mesma chave: retorna o **mesmo** pedido (`200`/`202`), **sem** publicar novo comando de cobrança se já existir um em andamento/concluído.

2. **Desacoplamento assíncrono (anti-colapso da API)**  
   - A API **não** chama o gateway na thread HTTP.  
   - Persiste pedido `PENDENTE_PAGAMENTO`, publica comando na **fila de pagamentos** e responde imediatamente.  
   - Um **Payment Worker** (bulkhead) consome a fila e fala com o gateway.

3. **Stability patterns no worker (anti-cascata)**  
   - **Timeout** curto na chamada ao gateway (ex.: 2–3s; nunca dezenas de segundos).  
   - **Circuit Breaker** em torno do cliente HTTP do parceiro: após falhas/timeouts, abre o circuito e falha rápido; com circuito aberto, a mensagem volta para retry com *backoff* (ou fica aguardando) **sem** saturar o parceiro nem o worker.  
   - **Retries** com limite + **Dead Letter Queue** para falhas irrecuperáveis (cartão inválido, rejeição definitiva, etc.).  
   - O worker também é **idempotente**: antes de cobrar, consulta se o pedido já está `PAGO` / se já existe `paymentAttemptId` bem-sucedido.

### Tecnologia de referência

- **RabbitMQ** (já adotado no ADR 001) com fila `pagamentos.cobranca` + DLQ.  
- Biblioteca de resiliência no worker (ex.: **Resilience4j**: Circuit Breaker, Time Limiter, Bulkhead).  
- Chave de idempotência persistida no **Banco Relacional** (constraint unique).

## Consequências

### Positivas

- Queda ou lentidão do gateway **não esgota** o pool de threads da API Principal.  
- Cliques compulsivos em “Comprar” **não** geram N cobranças — convergem para um único pedido/comando idempotente.  
- Quando o parceiro se recupera, a fila processa o backlog de forma controlada (circuito *half-open* → *closed*).  
- Isolamento (*bulkhead*): instabilidade de pagamento não derruba catálogo, login nem geração de e-books.

### Negativas / trade-offs

- Confirmação de compra **não é síncrona**; precisa de UX de status e notificação.  
- Pedidos podem ficar `PENDENTE_PAGAMENTO` por minutos durante outage — política de negócio (cancelar após X horas, reprocessar, etc.) precisa ser definida.  
- Mais peças operacionais: métricas do circuito, profundidade da fila, taxa de DLQ, alertas.  
- Idempotência exige disciplina no Frontend (mesma chave nos retries) e no Worker (não cobrar duas vezes após *ack* perdido).

### Fora de escopo deste ADR

- Escolha do adquirente/gateway específico ou de *fallback* para um segundo provedor.  
- Detalhe de estorno (*refund*) e conciliação financeira diária.  
- UI pixel-perfect do status de pagamento no Frontend.
