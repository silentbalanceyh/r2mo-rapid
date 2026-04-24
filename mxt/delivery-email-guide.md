# Delivery Email Guide

> Single-purpose guide for framework-level email delivery ownership in `r2mo-rapid`.

## 1. Scope

This guide covers:

- `r2mo-xync-email` as provider-facing email foundation,
- `r2mo-spring-email` as Spring-side integration landing,
- the boundary between provider implementation and Spring integration.

## 2. Ownership Split

### `r2mo-xync-email`

Read first when the question is about:

- provider-facing email contracts,
- vendor/account/credential abstractions,
- reusable delivery foundation below Spring.

### `r2mo-spring-email`

Read first when the question is about:

- Spring-side email integration,
- Bean/runtime wiring,
- how email delivery is exposed in Spring projects.

## 3. What Does Not Belong Here

Do not put these in the framework email layer:

- project-private notification content policy,
- tenant/customer-specific template workflows,
- business approval flows that only happen to send email.

## 4. Reading Rule

Use this order:

```text
delivery-email-guide.md -> mcp-route-spring-integrations.md -> xync-email or spring-email -> exact source
```

## 5. Pairwise Handling

Preferred pairs:

- `r2mo-rapid` alone for delivery foundation plus Spring landing ownership
- `r2mo-rapid` + `zero-ecotope` when email delivery capability must be compared with Zero email plugin/auth-plugin boundaries

## 6. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one delivery symbol is already known
- the unresolved point is whether the issue belongs in provider foundation or Spring integration landing
