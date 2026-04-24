# JCE Boundary

> Single-purpose boundary guide for `r2mo-jce` as the shared crypto and license primitive layer.

## 1. What `r2mo-jce` Owns

`r2mo-jce` owns shared cryptography, signing, verification, and license-style primitives.

Read it first for:

- encryption and decryption,
- signing and verification,
- key-related helper ownership,
- activation and license-style cryptographic flows.

## 2. What Belongs Here

- container-neutral crypto primitives,
- shared signing and verification logic,
- license-style core services,
- common key/material handling below container wiring.

## 3. What Does Not Belong Here

Do not place these in `r2mo-jce`:

- Spring Security runtime assembly,
- request filter behavior,
- project-private certificate workflows,
- business-facing licensing policy.

Those belong in:

- `r2mo-spring-security*` when runtime auth wiring is required,
- business projects when the rule is domain/private policy,
- bootstrap modules only when the question is dependency assembly.

## 4. Boundary With Neighbor Modules

### `r2mo-jce` vs `r2mo-jaas`

- `jce`: crypto and license primitives
- `jaas`: auth/session/claim primitives

### `r2mo-jce` vs Spring Security

- `jce`: shared primitive layer
- Spring Security: runtime integration and plugin flow

## 5. Reading Rule

Use this order:

```text
jce-boundary.md -> mcp-route-shared-capability-modules.md -> framework-trigger-matrix.md -> exact r2mo-jce source
```

## 6. Source and Resource Path

Primary proof targets:

- `r2mo-jce/src/main/java/io/r2mo/jce/common/HED.java`
- `r2mo-jce/src/main/java/io/r2mo/jce/common/JceProvider.java`
- `r2mo-jce/src/main/java/io/r2mo/jce/component/lic/AbstractLicenseService.java`
- `r2mo-jce/src/main/java/io/r2mo/jce/component/lic/io/LicenseIo.java`
- `r2mo-jce/src/main/java/io/r2mo/jce/component/lic/owner/PreActiveService.java`
- concrete algorithm implementations such as `LicenseServiceRSA`, `LicenseServiceSM2`, `LicenseServiceECC`, `LicenseServiceEd25519`

## 7. Pairwise Handling

Preferred pairs:

- `r2mo-rapid` alone for shared crypto and license primitives
- `r2mo-rapid` + `rachel-momo` when the unresolved point is dependency/plugin governance for crypto libraries
- `r2mo-rapid` + `zero-ecotope` only when a Zero-side capability consumes these primitives and ownership is ambiguous

## 8. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one crypto or license symbol is already known,
- the unresolved point is structural spread between primitive services, algorithm-specific implementations, and runtime consumers,
- source remains the final proof.
