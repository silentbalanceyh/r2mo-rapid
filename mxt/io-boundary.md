# IO Boundary

> Single-purpose boundary guide for `r2mo-io` and its storage / transfer abstraction role.

## 1. What `r2mo-io` Owns

`r2mo-io` owns shared storage and transfer abstraction.

Read it first for:

- file/storage abstraction,
- upload and download semantics,
- remote transfer contracts,
- provider selection for storage and transfer,
- `HFS`, `RFS`, `HStore`, and `HTransfer` ownership.

## 2. What Belongs Here

- upload session / transfer token / chunk / resume / complete / cancel semantics for large-file transfer,
- transfer progress and uploaded/waiting chunk visibility,
- container-neutral facades such as `RFS` and transfer-provider selection,

- container-neutral storage interfaces,
- transfer contracts,
- shared IO action semantics,
- provider lookup contracts,
- metadata needed by multiple IO implementations.

## 3. What Does Not Belong Here

Do not place these in `r2mo-io`:

- local filesystem-specific implementation code,
- Spring MVC multipart wiring,
- project-private file business rules,
- tenant-specific storage policy.

Those belong in:

- `r2mo-io-local` for local implementation,
- `r2mo-spring-*` when Spring runtime integration is required,
- business projects for domain-specific file rules.

## 4. Boundary With Neighbor Modules

### `r2mo-io` vs `r2mo-io-local`

- `io`: abstraction and provider-selection contract
- `io-local`: one concrete implementation

### `r2mo-io` vs Spring modules

- `io`: container-neutral
- Spring modules: request/runtime landing and integration behavior

## 5. Reading Rule

Use this order:

```text
io-boundary.md -> extension-points.md -> mcp-route-shared-capability-modules.md -> r2mo-io -> concrete implementation module
```

## 6. Source and Resource Path

Primary proof targets:

- `r2mo-ams/src/main/java/io/r2mo/base/io/HStore.java`
- `r2mo-ams/src/main/java/io/r2mo/base/io/HTransfer.java`
- `r2mo-ams/src/main/java/io/r2mo/base/io/modeling/*`
- `r2mo-ams/src/main/java/io/r2mo/base/io/common/*`

Read implementation modules only after the abstraction boundary is clear.

## 7. Pairwise Handling

Preferred pairs:

- `r2mo-rapid` alone for shared IO abstraction ownership
- `r2mo-rapid` + `zero-ecotope` when a Zero exmodule/plugin issue falls through into shared storage or transfer abstractions
- `r2mo-rapid` + `r2mo-spec` only when file-transfer payload semantics become a shared contract problem

## 8. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one IO symbol is already known,
- the unresolved point is structural spread between abstraction interfaces and concrete landing modules,
- source remains the final proof.
