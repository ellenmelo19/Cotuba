# Arquitetura Cotubify

O Cotubify é uma plataforma ponta a ponta de autopublicação de e-books.
Autores conectam repositórios Git com conteúdo em Markdown, configuram livros e acompanham vendas;
leitores navegam no catálogo, compram e baixam os arquivos gerados (PDF e EPUB).

## Decisões de Arquitetura (ADRs)

As decisões de escalabilidade da plataforma estão registradas como Architecture Decision Records:

- [ADR 001 — Geração assíncrona de e-books via fila de mensagens](../adr/adr-001-geracao-assincrona.md)
- [ADR 002 — Cache em memória para o catálogo “Top 100”](../adr/adr-002-cache-catalogo.md)

## Diagrama de Contexto

Visão de fronteiras do sistema (C4 Nível 1): pessoas, a plataforma e os sistemas externos com os quais ela se integra.

```mermaid
C4Context
    title Diagrama de Contexto - Cotubify

    Person(autor, "Autor", "Escreve livros em Markdown, versiona no Git e gerencia publicações e saques.")
    Person(leitor, "Leitor", "Navega no catálogo, compra e-books, faz download e recebe notificações.")

    System(cotubify, "Plataforma Cotubify", "Permite autopublicação, venda e distribuição de e-books.")

    System_Ext(git, "Provedor Git Externo", "Hospeda repositórios Markdown (GitHub, GitLab etc.).")
    System_Ext(pagamento, "Gateway de Pagamento", "Processa Pix e Cartão de Crédito.")
    System_Ext(email, "Sistema de E-mail Externo", "Envia recibos, avisos e notificações.")

    Rel(autor, cotubify, "Conecta repositório, configura livros, acompanha vendas e solicita saques")
    Rel(autor, git, "Versiona o conteúdo em Markdown")
    Rel(leitor, cotubify, "Acessa a loja, compra e baixa e-books")
    Rel(cotubify, git, "Clona o repositório do autor para gerar o ebook")
    Rel(cotubify, pagamento, "Envia dados de cobrança e processa pagamentos/saques")
    Rel(cotubify, email, "Dispara recibos e notificações")
```

## Diagrama de Container

Zoom para dentro da plataforma (C4 Nível 2): os containers implantáveis, suas responsabilidades e protocolos de comunicação.

A arquitetura de containers reflete as decisões de escalabilidade dos [ADRs](#decisões-de-arquitetura-adrs):

- a geração de e-books é **assíncrona** via **Message Broker** (o Gerador atua como *worker*);
- o catálogo da loja (Top 100) é servido preferencialmente por um **Cache em memória**.

```mermaid
C4Container
    title Diagrama de Container - Cotubify

    Person(autor, "Autor", "Escreve e publica livros.")
    Person(leitor, "Leitor", "Compra e baixa e-books.")

    System_Boundary(cotubify, "Plataforma Cotubify") {
        Container(web, "Aplicação Web Frontend", "SPA / Browser", "Interface visual usada por autores e leitores.")
        Container(api, "API Principal", "Java, Spring Boot", "Contas, catálogo, compras, saldo do autor e orquestração.")
        ContainerQueue(broker, "Message Broker", "RabbitMQ", "Fila de jobs de geração de e-books (eventos assíncronos).")
        Container(gerador, "Serviço Gerador de Ebooks (Worker)", "Java, Spring Boot", "Consome a fila, clona o repositório e gera PDF/EPUB com o motor Cotuba.")
        ContainerDb(db, "Banco de Dados Relacional", "SQL", "Usuários, metadados de livros, transações e ledger.")
        ContainerDb(cache, "Cache em Memória", "Redis", "Catálogo Top 100 e outras leituras frequentes da loja.")
        Container(storage, "Armazenamento de Arquivos", "Object Storage (S3)", "Capas em alta resolução e arquivos PDF/EPUB.")
    }

    System_Ext(git, "Provedor Git Externo", "GitHub / GitLab")
    System_Ext(pagamento, "Gateway de Pagamento", "Pix e Cartão de Crédito")
    System_Ext(email, "Sistema de E-mail Externo", "Recibos e notificações")

    Rel(autor, web, "Usa a plataforma", "HTTPS")
    Rel(leitor, web, "Usa a loja", "HTTPS")
    Rel(web, api, "Consome endpoints de negócio", "HTTPS / JSON")
    Rel(api, db, "Persiste e consulta dados transacionais", "JDBC / SQL")
    Rel(api, cache, "Lê/escreve catálogo Top 100 (cache-aside)", "Redis Protocol")
    Rel(api, broker, "Publica evento de geração de ebook", "AMQP")
    Rel(broker, gerador, "Entrega job de geração ao worker", "AMQP")
    Rel(api, pagamento, "Processa compras e saques", "HTTPS")
    Rel(api, email, "Envia recibos e avisos", "HTTPS / SMTP")
    Rel(api, storage, "Gerencia capas e URLs de download", "HTTPS / S3 API")
    Rel(gerador, git, "Clona o repositório do autor", "Git / HTTPS")
    Rel(gerador, storage, "Publica PDF e EPUB gerados", "HTTPS / S3 API")
    Rel(gerador, api, "Notifica conclusão/falha da geração", "HTTPS / evento")
```
