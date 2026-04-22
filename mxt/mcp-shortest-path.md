# MCP Shortest Path

> Single-purpose rule for shortest MCP retrieval in `r2mo-rapid`.

## 1. Goal

Minimize retrieval depth and token cost.

The target is:

```text
one entry doc -> one route doc -> one module/boundary doc -> exact source
```

## 2. Preferred Path by Trigger Shape

### Specific module already known

Use:

```text
exact module guide/boundary doc -> exact module source
```

Examples:

- `r2mo-jce` -> `jce-boundary.md`
- `r2mo-jaas` -> `jaas-boundary.md`
- `r2mo-io` -> `io-boundary.md`
- `r2mo-spring-cache` -> `spring-cache-guide.md`

### Capability family known, module still unclear

Use:

```text
matching mcp-route-*.md -> one boundary/guide doc -> exact source
```

### Everything is vague

Use:

```text
ai-agent-fast-start.md -> core-capability-index.md -> one mcp-route-*.md -> exact source
```

## 3. Token-Saving Rules

- Prefer one route file over `README.md`.
- Prefer one module boundary file over large family maps.
- Prefer graph-backed capability narrowing before opening multiple source files.
- Stop after one correct ownership answer is found.

## 4. Anti-Patterns

- `README.md` -> many docs -> many modules -> many files
- all `r2mo-spring-*` scanning
- all `r2mo-vertx-*` scanning
- reading graph analysis before a route file when the trigger is already concrete

## 5. Final Rule

Shortest correct path always beats broad early context.
