# Spring Excel Guide

> Single-purpose guide for `r2mo-spring-excel` and Spring-side Excel processing ownership.

## 1. What This Module Owns

`r2mo-spring-excel` owns Spring-side Excel processing integration and Excel SPI landing.

Read it first for:

- Excel processor ownership,
- Excel metadata and processing SPI,
- Spring-side import/export runtime integration for Excel.

## 2. What Does Not Belong Here

Do not place these here:

- generic code-generation rules,
- business-private spreadsheet workflows,
- non-Spring storage or transfer behavior.

Those belong in:

- generator docs when generation is the real concern,
- business repos for workflow-specific spreadsheet rules,
- `r2mo-io` if the issue is actually storage/transfer.

## 3. Reading Rule

Use this order:

```text
spring-excel-guide.md -> mcp-route-spring-integrations.md -> r2mo-spring-excel source -> code-generator docs only if generation is actually involved
```

## 4. Pairwise Handling

Preferred pairs:

- `r2mo-rapid` alone for Spring-side Excel integration ownership
- `r2mo-rapid` + `zero-ecotope` when Excel plugin/integration boundaries must be compared across runtime lines

## 5. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one Excel symbol is already known
- the unresolved point is structural spread between Spring Excel landing, SPI, and generation or import/export helpers
