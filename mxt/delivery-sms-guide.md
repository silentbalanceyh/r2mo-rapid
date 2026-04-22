# Delivery SMS Guide

> Single-purpose guide for framework-level SMS delivery ownership in `r2mo-rapid`.

## 1. Scope

This guide covers:

- `r2mo-xync-sms` as provider-facing SMS foundation,
- `r2mo-spring-sms` as Spring-side integration landing,
- the boundary between provider selection and Spring runtime integration.

## 2. Ownership Split

### `r2mo-xync-sms`

Read first when the question is about:

- SMS vendor/provider abstraction,
- credentials and provider contracts,
- reusable delivery foundation below Spring.

### `r2mo-spring-sms`

Read first when the question is about:

- Spring-side SMS client integration,
- runtime provider selection in Spring context,
- Spring-facing delivery assembly.

## 3. What Does Not Belong Here

Do not put these in the framework SMS layer:

- project-private notification timing rules,
- one-project approval/login workflow policy,
- tenant-specific SMS copy rules.

## 4. Reading Rule

Use this order:

```text
delivery-sms-guide.md -> mcp-route-spring-integrations.md -> xync-sms or spring-sms -> exact source
```
