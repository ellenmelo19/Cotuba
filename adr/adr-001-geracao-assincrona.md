# ADR 001 — Geração assíncrona de e-books via fila de mensagens

| Campo | Valor |
| --- | --- |
| **Status** | Aceito |
| **Data** | 2026-07-17 |
| **Decisores** | Time de Engenharia Cotubify |

## Contexto

O Cotubify cresceu para milhares de autores e milhões de leitores. Quando um autor publica ou edita um livro, a **API Principal** chama o **Serviço Gerador de E-books** de forma **síncrona via HTTP**.

A geração de PDF/EPUB leva cerca de **15 a 20 segundos**. Em horários de pico, centenas de publicações simultâneas fazem a API Principal reter threads de request enquanto aguarda o Gerador. Isso esgota o pool de threads, provoca **timeouts** e derruba a plataforma — inclusive para leitores que só querem comprar ou baixar livros.

O autor **não precisa** do arquivo na hora: basta um feedback do tipo *"Seu livro está sendo processado"* e uma notificação quando a geração terminar.

## Opções consideradas

### Opção A — Manter HTTP síncrono

- **Prós:** Simples de implementar e de depurar; fluxo request/response linear.
- **Contras:** Acopla latência da geração à latência da API; esgota threads sob carga; falha do Gerador vira falha da API; dificulta *scale out* independente do worker de geração.

### Opção B — HTTP assíncrono com callback / *polling*

- **Prós:** Não exige broker novo; a API responde 202 Accepted e o cliente consulta o status.
- **Contras:** Continua acoplamento ponto a ponto entre API e Gerador; *retries*, *backpressure* e balanceamento de fila ficam a cargo da aplicação; sem fila durável, jobs se perdem se o Gerador cair no meio do processamento.

### Opção C — Mensageria assíncrona (fila / message broker)

- **Prós:** Desacopla API e Gerador; a API publica um evento e libera a thread imediatamente; o Gerador atua como **worker** consumindo a fila e escala horizontalmente; *acks*, *retries* e *dead-letter queues* dão resiliência; melhora o **throughput** sem prender a API à latência de 15–20s.
- **Contras:** Introduz um componente de infraestrutura (broker); exige idempotência no worker e tratamento de mensagens duplicadas; aumenta complexidade operacional (monitoramento da fila, DLQ, etc.).

## Decisão

Adotamos a **Opção C**: comunicação **assíncrona orientada a eventos/filas** entre a API Principal e o Serviço Gerador de E-books.

### Desenho

1. O autor solicita publicação/edição pela Web → API Principal.
2. A API persiste o estado do livro (ex.: `PROCESSANDO`) e **publica um evento** de geração na fila.
3. A API responde de imediato ao autor (*"Seu livro está sendo processado"*).
4. O **Serviço Gerador de E-books** (worker) consome a mensagem, clona o repositório Git, roda o motor Cotuba e grava PDF/EPUB no Object Storage.
5. Ao concluir (sucesso ou falha), o worker notifica a API (evento de conclusão ou atualização de status) e o autor pode ser avisado via e-mail/notificação.

### Tecnologia de referência

**RabbitMQ** (ou equivalente AMQP / broker gerenciado na nuvem) como *message broker*, com fila dedicada a jobs de geração e *dead-letter queue* para falhas irrecuperáveis.

## Consequências

### Positivas

- A API Principal deixa de bloquear threads na geração → maior **throughput** e estabilidade sob pico.
- O Gerador escala **horizontalmente** (mais workers) sem alterar a API.
- Jobs sobrevivem a reinícios do worker se a fila for durável.
- Isolamento de falhas: lentidão ou queda do Gerador não derruba a loja.

### Negativas / trade-offs

- Mais um componente crítico na infraestrutura (broker) a operar e monitorar.
- O fluxo deixa de ser síncrono: o autor vê o arquivo só depois do processamento (UX de status assíncrono).
- Workers precisam ser **idempotentes** (a mesma mensagem pode ser reprocessada).
- Necessidade de observabilidade da fila (profundidade, idade da mensagem, taxa de erro, DLQ).

### Fora de escopo deste ADR

- Escolha exata do provedor gerenciado (Amazon SQS, CloudAMQP, etc.).
- Detalhamento do schema do evento e da API de status de geração.
