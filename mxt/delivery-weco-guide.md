# Delivery WeCo Guide

> Single-purpose guide for framework-level WeCom / WeChat Work delivery ownership in `r2mo-rapid`.

## 1. Scope

This guide covers:

- `r2mo-xync-weco` as provider-facing WeCom foundation,
- `r2mo-spring-weco` as Spring-side integration landing,
- the boundary between provider foundation and Spring runtime integration.

## 2. Ownership Split

### `r2mo-xync-weco`

Read first when the question is about:

- provider-facing WeCom contracts,
- reusable delivery foundation below Spring,
- vendor/integration primitives that should remain container-neutral.

### `r2mo-spring-weco`

Read first when the question is about:

- Spring-side WeCom integration,
- Bean/runtime wiring,
- Spring project integration behavior.

## 3. What Does Not Belong Here

Do not put these in the framework WeCo layer:

- project-private enterprise messaging workflow,
- organization-specific process rules,
- business-domain approval or notification policy.

## 4. Reading Rule

Use this order:

```text
delivery-weco-guide.md -> mcp-route-spring-integrations.md -> xync-weco or spring-weco -> exact source
```

## 5. Pairwise Handling

Preferred pairs:

- `r2mo-rapid` alone for delivery foundation plus Spring landing ownership
- `r2mo-rapid` + `zero-ecotope` when WeCo delivery capability must be compared with Zero WeCo plugin/auth-plugin boundaries

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one delivery symbol is already known
- the unresolved point is whether the issue belongs in provider foundation or Spring integration landing
