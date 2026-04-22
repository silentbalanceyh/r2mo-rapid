# AI Agent Fast Start

> Smallest possible entry for AI agents reading `r2mo-rapid`.
> Use this file when token cost matters more than broad orientation.

## 1. One-Sentence Model

`r2mo-rapid` is a dual-container framework repository with shared abstractions, shared contracts, Spring landings, Vert.x landings, and bootstrap assembly.

## 2. First Decision

Choose one branch only:

- shared contract or metadata -> `mcp-route-shared-contracts.md`
- shared capability or SPI -> `mcp-route-shared-capability-modules.md`
- Spring integration or delivery channel -> `mcp-route-spring-integrations.md`
- Spring Security auth flow -> `mcp-route-spring-security.md`
- Vert.x or jOOQ runtime -> `mcp-route-vertx-jooq.md`
- generator or processor -> `mcp-route-code-generator.md`
- unknown but framework-wide -> `core-capability-index.md`

## 3. Hard Stop Rules

- Do not read `README.md` first when the trigger is already specific.
- Do not open Java files before choosing one branch above.
- Do not read more than one route file unless the requirement is clearly mixed.

## 4. Default Reading Path

```text
this file -> one route file -> one module guide/boundary file -> exact source
```
