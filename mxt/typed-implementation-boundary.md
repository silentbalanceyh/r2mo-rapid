# Typed Implementation Boundary

> Boundary document for `r2mo-typed-hutool` and `r2mo-typed-vertx` in `r2mo-rapid`.

## 1. Purpose

These two modules provide concrete data-type implementations for the shared vocabulary defined in `r2mo-ams`. They are not container-specific, but they choose different runtime libraries for JSON, serialization, and utility operations.

## 2. Module Ownership

### `r2mo-typed-hutool`

- Depends on: `r2mo-ams`, `hutool-json`
- Provides: synchronous, Hutool-based implementations of shared data types
- Primary use: Spring-side projects that already depend on Hutool
- Typical classes: JSON utilities, type converters, string operations backed by Hutool

### `r2mo-typed-vertx`

- Depends on: `r2mo-ams`, `vertx-core`
- Provides: asynchronous, Vert.x-based implementations of shared data types
- Primary use: Vert.x-side projects that use the Vert.x JSON API
- Typical classes: JSON utilities backed by `io.vertx.core.json`, async-compatible type converters

## 3. Selection Boundary

Choose `r2mo-typed-hutool` when:

- the project runs on Spring Boot,
- Hutool is already in the dependency tree,
- synchronous JSON and utility operations are sufficient.

Choose `r2mo-typed-vertx` when:

- the project runs on Vert.x,
- the Vert.x JSON API (`io.vertx.core.json.JsonObject` / `JsonArray`) is the native format,
- async compatibility matters.

Do not include both in the same project unless a bridge layer is needed.

## 4. Relationship to `r2mo-spring-json`

`r2mo-typed-hutool` provides the data-type layer. `r2mo-spring-json` provides the Spring Boot auto-configuration for Jackson serialization. They operate at different levels:

- `r2mo-typed-hutool` = shared data-type implementation (no Spring dependency)
- `r2mo-spring-json` = Spring-side Jackson / JSON serialization landing

Read `r2mo-typed-hutool` first when the question is about data-type implementation. Read `r2mo-spring-json` first when the question is about Spring request/response serialization configuration.

## 5. Do Not Do

- Do not put Spring-specific code into `r2mo-typed-hutool` or `r2mo-typed-vertx`.
- Do not add a third typed module unless a fundamentally different runtime library is introduced.
- Do not bypass `r2mo-ams` vocabulary when adding new type implementations.

## 6. Final Rule

Typed modules are implementation choices, not architectural boundaries. Read the shared vocabulary in `r2mo-ams` first, then choose one typed module based on the runtime environment.

## 7. Pairwise Handling

Preferred pairs:

- `r2mo-rapid` alone for typed implementation selection
- `r2mo-rapid` + `zero-ecotope` when the unresolved point is how Vert.x-side typed behavior lands in the Zero runtime ecosystem

## 8. Direct Deep Retrieval Rule

Direct `code-review-graph` lookup is valid when:

- one typed implementation symbol is already known
- the unresolved point is structural spread between shared vocabulary, Hutool/Vert.x typed modules, and downstream runtime consumers
