# ADR 002 — Cache em memória para o catálogo “Top 100”

| Campo | Valor |
| --- | --- |
| **Status** | Aceito |
| **Data** | 2026-07-17 |
| **Decisores** | Time de Engenharia Cotubify |

## Contexto

A página inicial da loja exibe o catálogo dos **“Top 100 E-books Mais Vendidos”**. Cada visita de um dos milhões de leitores dispara, na **API Principal**, uma consulta complexa no **Banco de Dados Relacional** (múltiplos JOINs entre livros, vendas, metadados, etc.).

O banco atinge **100% de CPU** em horários de pico, elevando a **latência** da loja a níveis inaceitáveis. A lista dos Top 100 **não muda a cada segundo** — é um dado de leitura intensiva (*read-heavy*) e atualização relativamente lenta (*write-sparse*), perfil clássico para **cache**.

## Opções consideradas

### Opção A — Manter consultas diretas ao banco relacional

- **Prós:** Sem componente extra; dados sempre “frescos”.
- **Contras:** Cada page view da home vira carga no banco; latência e CPU sobem com o tráfego de leitores; dificulta o *scale out* da API sem pressionar o banco.

### Opção B — Materialized view / tabela denormalizada no próprio SQL

- **Prós:** Reduz custo da query sem novo produto de infraestrutura; ainda usa o ecossistema relacional.
- **Contras:** Continua no mesmo banco que já está saturado; *refresh* da view compete por CPU/I/O; não alivia tanto a latência de rede/app quanto um cache localizado perto da API.

### Opção C — Camada de cache em memória (Key-Value) entre API e banco

- **Prós:** Leituras do Top 100 com latência baixa (microsegundos/ms); alivia drasticamente a CPU do banco; encaixa bem em stores *key-value* (ex.: Redis); a API pode escalar horizontalmente compartilhando o mesmo cache; TTL e invalidação controlam frescor dos dados.
- **Contras:** Risco de dados levemente desatualizados (*eventual consistency* de curto prazo); cache miss stampede e invalidação precisam ser tratados; mais um componente a operar.

## Decisão

Adotamos a **Opção C**: introduzir uma **camada de cache em memória** entre a **API Principal** e o **Banco de Dados Relacional** para servir o catálogo da loja (em especial o Top 100).

### Desenho

1. A API, ao atender a home/catálogo, **lê primeiro o cache** com uma chave estável (ex.: `catalogo:top100`).
2. Em **cache hit**, devolve o payload ao Frontend sem consultar o banco.
3. Em **cache miss**, consulta o banco, grava o resultado no cache com **TTL** (ex.: poucos minutos) e responde ao cliente.
4. Em eventos que alteram ranking de forma relevante (nova venda consolidada, despublicação, etc.), a API pode **invalidar** ou atualizar a chave do cache.

### Tecnologia de referência

**Redis** (ou cache gerenciado equivalente) como *in-memory key-value store*, acessado pela API Principal.

## Consequências

### Positivas

- Redução forte da **latência** da home da loja.
- Menor carga de CPU/I/O no banco relacional → mais capacidade para escritas e consultas transacionais (compras, ledger, contas).
- Melhor **throughput** de leitura sob pico de leitores.
- Alinha-se ao padrão *cache-aside* amplamente usado em system design.

### Negativas / trade-offs

- O Top 100 pode ficar **ligeiramente defasado** em relação ao banco até o TTL ou a invalidação.
- Necessário definir política de TTL, invalidação e proteção contra *thundering herd* em cache miss.
- Redis torna-se dependência de disponibilidade da home (mitigável com fallback ao banco se o cache estiver indisponível).
- Operação: memória, persistência opcional, failover e monitoramento de hit ratio.

### Fora de escopo deste ADR

- Cache de outras páginas (detalhe do livro, busca full-text).
- CDN para assets estáticos (capas) — complementar, mas não substitui o cache do ranking.
- Escolha entre Redis Cluster vs. instância única em cada ambiente.
