# Spring Runtime Guide

> Single-purpose guide for the `r2mo-spring` runtime base layer.

## 1. What This Module Owns

`r2mo-spring` owns the Spring runtime base, not business extensions and not security plugins.

Read it first for:

- Web / MVC integration,
- exception handling conventions,
- interceptor and bean-level cross-cutting behavior,
- shared Spring runtime infrastructure below `r2mo-spring-*` feature modules.

## 2. What Belongs Here

- generic Spring runtime integration,
- container-level shared exception handling,
- base Web / Bean / AOP support,
- infrastructure that other Spring-side modules build on.

## 3. What Does Not Belong Here

Do not place these in `r2mo-spring`:

- JWT / OAuth2 / LDAP / login-mode plugins,
- feature-specific email, sms, doc, excel integrations,
- project-private Web workflows,
- bootstrap-only default assembly.

Those belong in:

- `r2mo-spring-security` or `r2mo-spring-security-*`,
- `r2mo-spring-*` feature modules,
- `r2mo-boot-spring*`,
- business projects.

## 4. Reading Rule

Use this order:

```text
spring-runtime-guide.md -> spring-layer-map.md -> exact r2mo-spring source -> dependent spring feature module if needed
```

## 5. Pairwise Handling

Preferred pairs:

- `r2mo-rapid` alone for Spring runtime base questions
- `r2mo-rapid` + `zero-ecotope` when the unresolved point is a runtime comparison between Spring and Zero execution layers
- `r2mo-rapid` + `r2mo-spec` only when shared contract meaning is still unresolved

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one Spring runtime seam is already known
- the unresolved point is structural spread between base runtime support and dependent Spring feature modules
