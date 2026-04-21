# MCP Route — Code Review Graph

> Final MCP route for graph-first analysis against `r2mo-rapid`.

## 1. Activation

Activate this route when the user is asking for:

- ownership,
- hotspot analysis,
- impact or blast radius,
- module targeting,
- graph-backed execution-path narrowing,
- or repository-level risk analysis.

Preferred regex:

```regex
(?i)\b(code[- ]review(?:er)?[- ]graph|graph[- ]based|graph analysis|impact radius|blast radius|risk index|hotspot|community|call path|execution path|ownership|which module|where should .* belong)\b
```

## 2. Mandatory Reading Set

- `code-review-graph-r2mo-analysis.md`
- `code-review-graph-usage.md`
- `mxt-r2mo-mcp-rules.md`

## 3. Execution Contract

When this route matches, the agent should:

1. use `code-review-graph` before broad file reads,
2. narrow to the smallest relevant community or module family,
3. open exact source files only after graph narrowing.

Strong graph communities for this repository include:

- `auth-security`
- `generate-generator`
- `common-license`
- `jooq-async`
- `operation-transfer`
- `cache-cache`

## 4. Allowed Combinations

This route can combine with:

- `mcp-route-spring-security.md`
- `mcp-route-code-generator.md`

Examples:

- `which module owns JWT token refresh` -> graph route + Spring Security route
- `where should I add a new jOOQ forced type` -> graph route + code generator route

## 5. Do Not Do

- Do not start with broad Java file reads.
- Do not treat graph output as final truth.
- Do not skip source confirmation after narrowing.

## 6. Final Rule

If this route matches, graph-first narrowing is mandatory unless the user already provided an exact file or a unique symbol.
