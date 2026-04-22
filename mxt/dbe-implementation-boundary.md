# DBE Implementation Boundary

> Boundary document for `r2mo-dbe-mybatisplus` and `r2mo-dbe-jooq` in `r2mo-rapid`.

## 1. Purpose

These two modules provide concrete persistence implementations for the shared query-shape abstractions defined in `r2mo-dbe`. They represent the two supported ORM strategies in the framework.

## 2. Module Ownership

### `r2mo-dbe-mybatisplus`

- Depends on: `r2mo-dbe`, `freemarker`, `mybatis`, `mybatis-plus-core/extension/annotation`, `spring-context`
- Provides: MyBatis-Plus-based implementation of DBE query and CRUD abstractions
- Primary use: Spring Boot projects using MyBatis-Plus as the persistence layer
- Key capabilities:
  - code generation through Freemarker templates,
  - dynamic query construction via MyBatis-Plus wrappers,
  - multi-datasource support (bridged through `r2mo-spring-mybatisplus`)

### `r2mo-dbe-jooq`

- Depends on: `r2mo-dbe`, `jooq`, `jooq-codegen`, `jooq-meta`
- Provides: jOOQ-based implementation of DBE abstractions
- Primary use: projects that need type-safe SQL construction, especially Vert.x-side
- Key capabilities:
  - type-safe SQL generation through jOOQ,
  - forced-type and code-generation configuration,
  - shared jOOQ abstraction contracts consumed by `r2mo-vertx-jooq*`

## 3. Selection Boundary

Choose `r2mo-dbe-mybatisplus` when:

- the project runs on Spring Boot,
- MyBatis-Plus is the persistence standard,
- dynamic wrapper-based queries are preferred.

Choose `r2mo-dbe-jooq` when:

- the project needs type-safe SQL,
- the Vert.x runtime is in use,
- jOOQ code generation fits the workflow.

## 4. Layer Relationship

```text
r2mo-dbe (shared abstractions: criteria, pager, sorter, projection)
  -> r2mo-dbe-mybatisplus (MyBatis-Plus implementation)
     -> r2mo-spring-mybatisplus (Spring Boot auto-configuration landing)
  -> r2mo-dbe-jooq (jOOQ abstraction contracts)
     -> r2mo-vertx-jooq* (Vert.x runtime bridge)
```

Read `r2mo-dbe` first for interface ownership. Read the implementation module for execution logic. Read the landing module (`spring-mybatisplus` or `vertx-jooq*`) for runtime wiring.

## 5. Do Not Do

- Do not put Spring Boot auto-configuration into `r2mo-dbe-mybatisplus` itself; that belongs in `r2mo-spring-mybatisplus`.
- Do not put Vert.x runtime execution into `r2mo-dbe-jooq` itself; that belongs in `r2mo-vertx-jooq*`.
- Do not add a third DBE implementation module unless a fundamentally different ORM is introduced.

## 6. Final Rule

DBE implementation modules are persistence strategy choices. Read the shared interface in `r2mo-dbe` first, then read one implementation module based on the ORM strategy in use.
