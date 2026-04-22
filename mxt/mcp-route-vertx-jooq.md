# MCP Route — Vert.x / jOOQ

> Final MCP route for Vert.x-side runtime and jOOQ-related work in `r2mo-rapid`.

## 1. Activation

Activate this route when the request is clearly about:

- `r2mo-vertx`,
- `r2mo-vertx-jooq`,
- `r2mo-vertx-jooq-shared`,
- `r2mo-vertx-jooq-jdbc`,
- `r2mo-vertx-jooq-generate`,
- async DB context,
- jOOQ runtime bridging,
- or Vert.x-side generator/runtime ownership.

Preferred regex:

```regex
(?i)\b(r2mo-vertx|r2mo-vertx-jooq(?:-shared|-jdbc|-generate)?|vertx|asyncdbcontext|dbcontext|dbjx|dbvector|jooq runtime|jdbc bridge|forced type|typeofjooq|jooq source)\b
```

## 2. Mandatory Reading Set

- `dual-side-development.md`
- `framework-map.md`
- `code-generator-usage.md`
- `search-hints.md`
- `mxt-r2mo-mcp-rules.md`

## 3. Execution Contract

When this route matches, the agent should:

1. classify whether the work is runtime, shared bridge, JDBC landing, or generator work,
2. read `r2mo-vertx` together with the concrete `r2mo-vertx-jooq*` module,
3. confirm whether query-shape ownership still belongs in `r2mo-dbe`,
4. separate jOOQ generation concerns from async runtime concerns before editing.

Primary module families:

- `r2mo-vertx` for Vert.x-side runtime base
- `r2mo-vertx-jooq-shared` for shared jOOQ-side support
- `r2mo-vertx-jooq-jdbc` for JDBC landing
- `r2mo-vertx-jooq` for runtime bridge behavior
- `r2mo-vertx-jooq-generate` for jOOQ source/type generation

## 4. Allowed Combinations

This route can combine with:

- `mcp-route-code-review-graph.md`
- `mcp-route-code-generator.md`
- `mcp-route-shared-capability-modules.md`

Examples:

- `where should a forced type rule change land` -> Vert.x/jOOQ route + code generator route
- `does this async query bug belong to dbe or vertx-jooq` -> Vert.x/jOOQ route + shared capability modules route
- `which module owns DB context selection` -> Vert.x/jOOQ route + graph route

## 5. Do Not Do

- Do not treat all `jooq` wording as generator-only.
- Do not skip `r2mo-vertx` when reading a concrete `r2mo-vertx-jooq*` module.
- Do not move shared query semantics into Vert.x modules without checking `r2mo-dbe` first.

## 6. Final Rule

Use this reading order:

```text
vertx base -> exact vertx-jooq family module -> dbe ownership check -> generator/runtime specialization
```
