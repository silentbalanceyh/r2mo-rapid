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

## 6. Source and Resource Path

Primary proof targets:

- `r2mo-jaas/src/main/java/io/r2mo/jaas/session/UserClaim.java`
- `r2mo-jaas/src/main/java/io/r2mo/jaas/session/UserCache.java`
- `r2mo-jaas/src/main/java/io/r2mo/jaas/session/UserSession.java`
- `r2mo-jaas/src/main/java/io/r2mo/jaas/token/*`
- `r2mo-jaas/src/main/java/io/r2mo/jaas/auth/*`

Read `r2mo-spring-security` only when the unresolved point moves into Spring runtime landing.

## 7. Pairwise Handling

Preferred pairs:

- `r2mo-rapid` alone for shared auth/session primitives
- `r2mo-rapid` + `zero-ecotope` when a Zero-side session/security question must be compared against shared auth primitives
- `r2mo-rapid` + `r2mo-spec` only when claim/token payload meaning is the unresolved contract surface

## 8. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one auth primitive is already known,
- the unresolved point is structural spread between claim/session/token primitives and runtime consumers,
- graph results are verified against source afterward.
