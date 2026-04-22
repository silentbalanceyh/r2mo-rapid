# Spring Cache Guide

> Single-purpose guide for `r2mo-spring-cache` in `r2mo-rapid`.

## 1. What This Module Owns

`r2mo-spring-cache` owns Spring-side cache integration and cache provider selection behavior.

Read it first for:

- cache implementation choice,
- cache provider priority,
- security-adjacent cache backend selection on Spring,
- `@SPID`-driven cache implementation ownership.

## 2. What Belongs Here

- Spring cache backend wiring,
- cache implementation declaration and priority,
- Spring-side cache provider landing,
- cache integration that requires container/runtime context.

## 3. What Does Not Belong Here

Do not place these here:

- generic cache abstraction that should stay container-neutral,
- project-private caching rules,
- auth plugin logic that belongs in `r2mo-spring-security*`,
- business-specific cache keys or domain invalidation policy.

## 4. Relationship With Neighbor Modules

### `r2mo-spring-cache` vs shared capability modules

- shared modules own reusable capability contracts,
- `r2mo-spring-cache` owns Spring landing and implementation selection.

### `r2mo-spring-cache` vs `r2mo-spring-security`

- `spring-cache`: cache provider integration
- `spring-security`: broader auth runtime assembly

## 5. Reading Rule

Use this order:

```text
spring-cache-guide.md -> mcp-route-spring-integrations.md -> extension-points.md -> exact r2mo-spring-cache source
```
