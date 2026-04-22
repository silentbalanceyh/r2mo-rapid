# JAAS Boundary

> Single-purpose boundary guide for `r2mo-jaas` as the shared auth primitive layer.

## 1. What `r2mo-jaas` Owns

`r2mo-jaas` owns shared authentication primitives and claim/session-side abstractions.

Read it first for:

- user claim base semantics,
- session-adjacent shared primitives,
- user cache abstraction,
- auth-related shared SPI contracts below Spring Security plugins.

## 2. What Belongs Here

- container-neutral auth primitives,
- user/claim base contracts,
- shared cache abstraction for auth state,
- SPI-oriented extension points that multiple security implementations can consume.

## 3. What Does Not Belong Here

Do not place these in `r2mo-jaas`:

- Spring Security filter-chain logic,
- login endpoint routing,
- OAuth2 / JWT / LDAP plugin behavior,
- project-private account workflows.

Those belong in:

- `r2mo-spring-security` for Spring Security base runtime,
- `r2mo-spring-security-*` for mode-specific plugin behavior,
- business projects for domain-specific user flows.

## 4. Boundary With Neighbor Modules

### `r2mo-jaas` vs `r2mo-spring-security`

- `jaas`: shared auth primitives
- `spring-security`: Spring landing and runtime assembly

### `r2mo-jaas` vs `r2mo-jce`

- `jaas`: auth/session/claim semantics
- `jce`: crypto, signing, verification, key material handling

## 5. Reading Rule

Use this order:

```text
jaas-boundary.md -> mcp-route-shared-capability-modules.md -> spring-security-mcp-guide.md if needed -> exact source
```
