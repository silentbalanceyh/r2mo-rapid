# Framework Map

## 1. Layer Overview

`r2mo-rapid` can be understood as four concentric layers:

```text
Business Project
  -> Bootstrap Assembly Layer
    -> Container Core Layer
      -> Abstraction / Specification Layer
        -> Concrete Implementation Layer (swappable)
```

## 2. Module Map

### A. Abstraction and Specification Base

- `r2mo-ams`
  - Agreed Metadata Specification.
  - Provides foundational metadata semantics and serves as a component library for higher-level specifications.
- `r2mo-spec`
  - Unified interface specifications, OpenAPI, schemas, error codes, and Markdown contracts.
  - This is the shared contract layer for Spring / Zero / Vert.x and must not contain container-specific logic.
- `r2mo-dbe`
  - Database abstraction layer.
  - Defines unified query, CRUD, and JSON query syntax capabilities.
- `r2mo-io`
  - Storage and transmission abstraction.
  - Exposes unified interfaces such as `HFS / RFS` upward and discovers concrete providers through SPI downward.
- `r2mo-jce`
  - Encryption, decryption, and security algorithm abstraction.
- `r2mo-jaas`
  - Authentication and authorization base abstraction, reused by security extensions and container implementations.

### B. Concrete Implementation Layer

- `r2mo-dbe-mybatisplus`
- `r2mo-dbe-jooq`
- `r2mo-typed-hutool`
- `r2mo-typed-vertx`
- `r2mo-io-local`

This layer answers "how abstractions are implemented", not "what the business wants to do".

### C. Container Core Layer

- `r2mo-spring`
  - Spring core integration layer.
  - Provides exception handling and integrations for Web / AOP / Bean / Spring Security Core.
- `r2mo-spring-security`
  - Spring Security extension base.
- `r2mo-vertx`
  - Vert.x core integration layer.

### D. Container Extension Layer

- Spring general extensions
  - `r2mo-spring-json`
  - `r2mo-spring-mybatisplus`
  - `r2mo-spring-cache`
  - `r2mo-spring-doc`
  - `r2mo-spring-email`
  - `r2mo-spring-sms`
  - `r2mo-spring-template`
  - `r2mo-spring-excel`
  - `r2mo-spring-weco`
- Spring Security extensions
  - `r2mo-spring-security-jwt`
  - `r2mo-spring-security-ldap`
  - `r2mo-spring-security-oauth2`
  - `r2mo-spring-security-oauth2client`
  - `r2mo-spring-security-email`
  - `r2mo-spring-security-sms`
  - `r2mo-spring-security-weco`
- Vert.x-side extensions
  - `r2mo-vertx-jooq-shared`
  - `r2mo-vertx-jooq-jdbc`
  - `r2mo-vertx-jooq-generate`
  - `r2mo-vertx-jooq`

### E. Bootstrap Assembly Layer

- `r2mo-boot-spring`
  - Spring bootstrap abstraction layer.
  - Depends on `r2mo-dbe`, `r2mo-io`, `r2mo-spring`, `r2mo-jce`, and `r2mo-jaas`.
- `r2mo-boot-spring-default`
  - Spring default implementation bundle.
  - Additionally brings in `r2mo-spring-json`, `r2mo-typed-hutool`, `r2mo-io-local`, and `r2mo-spring-mybatisplus`.
- `r2mo-boot-vertx`
  - Vert.x bootstrap layer that directly includes implementation dependencies.

## 3. Dual-Container Main Lines

### Spring Line

```text
r2mo-spec / r2mo-ams / r2mo-dbe / r2mo-io / r2mo-jce / r2mo-jaas
  -> r2mo-spring
    -> r2mo-spring-security
    -> r2mo-spring-*
    -> r2mo-spring-security-*
      -> r2mo-boot-spring
        -> r2mo-boot-spring-default
```

### Vert.x Line

```text
r2mo-spec / r2mo-ams / r2mo-dbe / r2mo-io / r2mo-jce / r2mo-jaas
  -> r2mo-vertx
    -> r2mo-vertx-jooq-*
      -> r2mo-boot-vertx
```

## 4. Where the Core Objects Fit

- `Cc`: Primarily a runtime component cache pattern scattered across implementation layers and framework internals to avoid repeated initialization.
- `Fn`: A general-purpose functional utility, closer to the foundational programming model.
- `DBE`: The main entry point of the abstraction layer, with MyBatis Plus / jOOQ implementations attached below it.
- `SPI`: Spans the abstraction and implementation layers and is the key path for "abstraction finding implementation".

## 5. Three-Framework Upper-Layer Relationship

R2MO operates within a broader framework ecosystem. Understanding the three-layer relationship prevents incorrect assumptions about ownership:

```text
rachel-momo / zero-ecotope  (upper-layer business frameworks)
       |
  r2mo-rapid                (mid-layer rapid development scaffold)
       |
  spring-boot / vert.x      (lower-layer runtime containers)
```

### `r2mo-rapid` as the mid-layer

- `r2mo-rapid` sits **between business frameworks and raw containers**.
- It does not implement business logic, but it defines the shared contracts and extension entry points that business frameworks build on top of.
- Business frameworks such as `rachel-momo` or `zero-ecotope` consume `r2mo-rapid` modules and extend them at the `SPI` layer.

### `r2mo-spec` as the shared standardization layer

`r2mo-spec` is the most cross-cutting module in `r2mo-rapid`. Its role:

- Defines the **schema, error, API, and documentation contracts** that all three upper-layer frameworks can depend on without container coupling.
- Acts as the **single source of truth** for OpenAPI specs, shared data shapes, and error code taxonomy.
- Is consumed by Spring-side (`spring-doc`, `spring-security`), Vert.x-side, and upper business frameworks alike.
- Must stay strictly container-neutral — nothing in `r2mo-spec` should import Spring or Vert.x classes.

Any time an agent is unsure whether a schema or error definition belongs to a business framework or to `r2mo-rapid`, the right question is: **"Do multiple different container-side modules or business frameworks need this definition?"** If yes, it belongs in `r2mo-spec`.

## 6. Decision Handles

If you do not know where to change something, ask three questions first:

1. Is this a **contract problem**, a **container problem**, or an **implementation problem**?
2. Is this **Spring-specific**, **Vert.x-specific**, or **shared by both sides**?
3. Is this **bootstrap assembly**, **abstract definition**, or a **concrete provider**?

Once these three questions are answered, the landing spot is usually clear.

## 7. Companion Document

If the task is primarily about runtime env/application interpretation rather than module topology, read:

- `runtime-configuration-surface.md`
