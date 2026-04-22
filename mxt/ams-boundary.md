# AMS Boundary

> Single-purpose boundary guide for `r2mo-ams` in `r2mo-rapid`.

## 1. What `r2mo-ams` Owns

`r2mo-ams` is the shared foundation layer for framework vocabulary and base semantics.

Use it for:

- common structural metadata,
- shared low-level framework primitives,
- cross-module agreement objects,
- utility-level foundation that multiple capability families depend on.

This is the module to read first when the requirement sounds like shared semantics rather than one concrete runtime capability.

## 2. What Belongs Here

Strong candidates:

- common metadata structures,
- base shared value objects,
- framework-wide low-level helpers,
- neutral primitives reused by `dbe`, `io`, `jaas`, `jce`, or `spec`.

Good test:

```text
If several framework families would need the same foundational term or primitive,
it is a candidate for r2mo-ams.
```

## 3. What Does Not Belong Here

Do not place these in `r2mo-ams`:

- OpenAPI contracts and schema-facing definitions,
- DBE query execution logic,
- storage provider implementations,
- Spring Bean / Web / Security runtime logic,
- Vert.x runtime bridges,
- business-domain names or workflows.

Those belong in:

- `r2mo-spec` for contracts,
- `r2mo-dbe` / `r2mo-io` / `r2mo-jaas` / `r2mo-jce` for capability abstractions,
- `r2mo-spring*` or `r2mo-vertx*` for container code,
- business projects for domain-specific logic.

## 4. Boundary With Neighbor Modules

### `r2mo-ams` vs `r2mo-spec`

- `ams`: shared semantics and framework vocabulary
- `spec`: contract, schema, marker, OpenAPI, and error definitions

### `r2mo-ams` vs capability abstractions

- `ams`: foundational shared primitives
- `dbe` / `io` / `jaas` / `jce`: capability-specific abstractions and behavior

### `r2mo-ams` vs Spring / Vert.x

- `ams`: container-neutral
- Spring / Vert.x modules: runtime landing and integration

## 5. Reading Rule

Use this order:

```text
ams-boundary.md -> mcp-route-shared-contracts.md -> framework-map.md -> exact r2mo-ams source
```
