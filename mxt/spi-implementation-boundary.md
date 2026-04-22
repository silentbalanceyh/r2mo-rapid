# SPI Implementation Boundary

> Single-purpose guide for separating shared SPI contracts from native and Spring-side implementations.

## 1. Core Question

This file answers one question:

```text
Does this extension belong in the shared SPI boundary, a native implementation module,
or a Spring-side container landing?
```

## 2. SPI Ownership Model

In `r2mo-rapid`, SPI usually means:

1. a shared abstraction interface,
2. one or more implementations,
3. runtime selection through `SPI.findOne*` or `SPI.findMany`,
4. optional implementation declaration through `@SPID`.

That means SPI is neither equal to one implementation nor equal to one container.

## 3. Native Implementation vs Spring Implementation

### Shared SPI boundary

Keep the interface and capability contract in shared modules such as:

- `r2mo-dbe`
- `r2mo-io`
- `r2mo-jaas`
- `r2mo-jce`

### Native implementation layer

Put provider or concrete implementation code in native implementation modules when it is still container-neutral, for example:

- `r2mo-io-local`
- `r2mo-xync-email`
- `r2mo-xync-sms`
- `r2mo-xync-weco`

### Spring-side landing

Put Spring wiring, auto-configuration, Bean assembly, or Spring Security participation in:

- `r2mo-spring-*`
- `r2mo-spring-security*`

## 4. Decision Rules

Choose the shared SPI boundary when:

- the capability must remain reusable across containers,
- the API is the real contract,
- multiple implementations are expected,
- runtime selection is part of the design.

Choose the native implementation layer when:

- the work is a concrete provider implementation,
- the code still does not require Spring runtime mechanisms,
- the implementation should stay reusable below Spring.

Choose the Spring-side landing when:

- the behavior depends on Bean lifecycle,
- the work is auto-configuration or filter/provider registration,
- the implementation only makes sense inside Spring.

## 5. Common Mistakes

- Treating one provider implementation as if it owns the abstraction.
- Moving shared interfaces into Spring modules.
- Skipping the native provider layer and wiring vendors directly in business projects.
- Putting Boot default assembly where SPI or provider code should live.

## 6. Reading Rule

Use this order:

```text
spi-implementation-boundary.md -> extension-points.md -> mcp-route-shared-capability-modules.md -> exact module source
```
