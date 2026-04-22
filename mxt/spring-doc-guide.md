# Spring Doc Guide

> Single-purpose guide for `r2mo-spring-doc` and Spring-side API documentation ownership.

## 1. What This Module Owns

`r2mo-spring-doc` owns Spring-side API documentation runtime integration.

Read it first for:

- SpringDoc / Swagger runtime configuration,
- API docs exposure behavior,
- Swagger UI and OpenAPI runtime landing on Spring.

## 2. What Does Not Belong Here

Do not place these here:

- shared schemas and OpenAPI contract definitions,
- business-private API descriptions,
- generic Spring runtime base concerns.

Those belong in:

- `r2mo-spec` for shared contracts,
- business repos for app-private docs,
- `r2mo-spring` for generic runtime base behavior.

## 3. Reading Rule

Use this order:

```text
spring-doc-guide.md -> mcp-route-spring-integrations.md -> r2mo-spring-doc source -> spec docs only if contract details are needed
```
