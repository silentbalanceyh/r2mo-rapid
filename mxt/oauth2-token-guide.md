# OAuth2 Token Guide

> Single-purpose guide for OAuth2 and token-flow ownership in `r2mo-rapid`.

## 1. Scope

This guide covers:

- `r2mo-spring-security-oauth2`
- `r2mo-spring-security-oauth2client`
- token-flow and token-refresh semantics inside the Spring Security plugin family

## 2. What To Read First

Read this guide when the question is about:

- OAuth2 token handling,
- token refresh,
- client registration and provider assumptions,
- OAuth2 plugin behavior rather than generic auth wording.

## 3. Ownership Split

### `r2mo-spring-security`

Owns the base security runtime and shared plugin participation model.

### `r2mo-spring-security-oauth2`

Owns OAuth2 server-side plugin behavior and token-flow participation.

### `r2mo-spring-security-oauth2client`

Owns client registration and client-side OAuth2 helper behavior.

## 4. Do Not Do

- Do not route OAuth2 token questions to generic JWT assumptions.
- Do not inspect `oauth2client` without the base `oauth2` and `spring-security` context.
- Do not treat `spring.security.oauth2.*` configuration as self-explaining without runtime source confirmation.

## 5. Reading Rule

Use this order:

```text
oauth2-token-guide.md -> spring-security-mcp-guide.md -> r2mo-spring-security -> oauth2 or oauth2client module -> exact token flow source
```

## 6. Pairwise Handling

Preferred pairs:

- `r2mo-rapid` alone for Spring OAuth2 token ownership
- `r2mo-rapid` + `zero-ecotope` when OAuth2 capability/provider behavior must be compared across Spring and Zero lines

## 7. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one OAuth2 token symbol is already known
- the unresolved point is structural spread between base security runtime, authorization server behavior, and client registration modules
