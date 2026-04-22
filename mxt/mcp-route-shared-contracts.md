# MCP Route — Shared Contracts

> Final MCP route for contract-first reading in `r2mo-rapid`.

## 1. Activation

Activate this route when the request is clearly about:

- `r2mo-ams`,
- `r2mo-spec`,
- shared metadata semantics,
- OpenAPI, schema, marker, or error-code ownership,
- cross-framework contract standardization,
- or “should this stay container-neutral?” decisions.

Preferred regex:

```regex
(?i)\b(r2mo-ams|r2mo-spec|shared contract|contract-first|openapi|schema|marker|metadata|error code|spec boundary|container-neutral|shared vocabulary)\b
```

## 2. Mandatory Reading Set

- `framework-map.md`
- `spec-boundary.md`
- `abstraction-rules.md`
- `mxt-r2mo-mcp-rules.md`

## 3. Execution Contract

When this route matches, the agent should:

1. classify whether the request belongs to `r2mo-ams` or `r2mo-spec`,
2. confirm that the expected output is contract or metadata, not runtime behavior,
3. keep Spring, Vert.x, and business-project assumptions outside the contract layer,
4. move back to runtime modules only after the contract boundary is confirmed.

Practical ownership split:

- `r2mo-ams` for shared vocabulary, metadata semantics, and foundational agreement structures
- `r2mo-spec` for OpenAPI, schemas, markers, error structure, and documentation-facing contracts

## 4. Allowed Combinations

This route can combine with:

- `mcp-route-code-review-graph.md`
- `mcp-route-shared-capability-modules.md`

Examples:

- `where should a shared marker definition live` -> shared contracts route
- `does this metadata shape belong to ams or spec` -> shared contracts route + graph route

## 5. Do Not Do

- Do not infer runtime filter, cache, or database behavior from `r2mo-spec`.
- Do not put Spring or Vert.x implementation assumptions into `r2mo-ams` or `r2mo-spec`.
- Do not treat business-project wording as proof that a contract belongs in the framework.

## 6. Final Rule

Use this reading order:

```text
contract question -> ams/spec boundary -> exact contract files -> runtime landing only if needed
```
