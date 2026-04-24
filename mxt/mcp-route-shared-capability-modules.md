# MCP Route — Shared Capability Modules

> Final MCP route for shared capability modules that sit above container-specific landings in `r2mo-rapid`.

## 1. Activation

Activate this route when the request is clearly about:

- `r2mo-dbe`,
- `r2mo-io`,
- `r2mo-jaas`,
- `r2mo-jce`,
- `r2mo-typed-hutool`,
- `r2mo-typed-vertx`,
- shared SPI/provider-selection behavior,
- native implementation ownership versus Spring/Vert.x landing,
- or a capability that might still be container-neutral.

Preferred regex:

```regex
(?i)\b(r2mo-dbe|r2mo-io|r2mo-jaas|r2mo-jce|r2mo-typed-hutool|r2mo-typed-vertx|shared capability|provider selection|spi|spid|claim base|user cache|license|encrypt|decrypt|storage|transfer|criteria|pager|sorter|projection|typed.hutool|typed.vertx|type implementation)\b
```

## 2. Mandatory Reading Set

- `framework-map.md`
- `extension-points.md`
- `dual-side-development.md`
- `framework-trigger-matrix.md`
- `mxt-r2mo-mcp-rules.md`

## 3. Execution Contract

When this route matches, the agent should:

1. start from the shared capability module before reading Spring or Vert.x landings,
2. distinguish interface ownership from implementation ownership,
3. check SPI call sites and `@SPID` declarations before inventing new extension seams,
4. confirm whether the behavior should remain native/shared or move into container code.

Primary module families:

- `r2mo-dbe` for shared query, CRUD, and query-shape ownership
- `r2mo-io` for storage and transfer abstraction plus provider selection
  - for large-file upload, start from `RFS`, `HTransfer`, `TransferRequest`, `TransferToken`, `TransferLargeService`, and the concrete provider such as `r2mo-io-local`
- `r2mo-jaas` for auth primitives, user cache, and claim-base semantics
- `r2mo-jce` for crypto, signature, verification, and license-style primitives
- `r2mo-typed-hutool` for Hutool-based synchronous data-type implementations
- `r2mo-typed-vertx` for Vert.x-based asynchronous data-type implementations

## 4. Native vs Container Rule

Use this route to answer a recurring question:

```text
Does the capability still belong in the shared/native abstraction layer,
or has it already crossed into Spring/Vert.x runtime behavior?
```

Default reading order:

```text
shared capability module -> SPI/provider path -> native implementation module -> container landing if required
```

## 5. Allowed Combinations

This route can combine with:

- `mcp-route-code-review-graph.md`
- `mcp-route-spring-integrations.md`
- `mcp-route-vertx-jooq.md`
- `mcp-route-spring-security.md`

Examples:

- `storage provider mismatch` -> shared capability modules route + graph route
- `does token cache belong to jaas or spring security` -> shared capability modules route + Spring Security route
- `should query-shape logic live in dbe or vertx-jooq` -> shared capability modules route + Vert.x/jOOQ route

## 6. Do Not Do

- Do not jump straight to Spring classes when the requirement still smells shared.
- Do not treat one `@SPID` implementation as proof that the abstraction boundary is wrong.
- Do not patch generated or container code first when the shared capability contract is the actual owner.

## 7. Final Rule

If the first nouns are shared capability nouns rather than container nouns, use this route first.
