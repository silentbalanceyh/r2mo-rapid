# Boot Assembly Guide

> Guide for `r2mo-boot-spring`, `r2mo-boot-spring-default`, and `r2mo-boot-vertx` in `r2mo-rapid`.

## 1. Purpose

Boot modules are assembly layers. They aggregate framework capabilities into a ready-to-use dependency package so that business projects can start with minimal dependency configuration.

They are not implementation layers. Business logic must not be placed in boot modules.

## 2. Module Ownership

### `r2mo-boot-spring`

- Depends on: `r2mo-dbe`, `r2mo-io`, `r2mo-spring`, `r2mo-jce`, `r2mo-jaas`, `mysql-connector-j`, `flyway-mysql`, `flyway-core`, `mybatis-plus-annotation`, `jakarta.validation-api`, `spring-boot-starter-web` (Jetty)
- Role: Spring bootstrap abstraction
- Provides: standard Spring project foundation without choosing concrete implementations
- For: projects that want to select their own typed, IO, and persistence implementations

### `r2mo-boot-spring-default`

- Depends on: `r2mo-boot-spring`, `r2mo-spring-json`, `r2mo-typed-hutool`, `r2mo-io-local`, `r2mo-spring-mybatisplus`
- Role: default Spring onboarding package
- Provides: opinionated default choices on top of `boot-spring`
- For: projects that want the fastest possible startup

### `r2mo-boot-vertx`

- Depends on: `r2mo-dbe`, `r2mo-io`, `r2mo-vertx`, `r2mo-jce`, `r2mo-jaas`
- Role: Vert.x bootstrap assembly
- Provides: standard Vert.x project foundation
- For: projects running on the Vert.x container

## 3. Assembly Chain

### Spring chain

```text
r2mo-boot-spring (choose implementations yourself)
  -> add r2mo-spring-json
  -> add r2mo-typed-hutool (or r2mo-typed-vertx)
  -> add r2mo-io-local
  -> add r2mo-spring-mybatisplus
```

Or use the shortcut:

```text
r2mo-boot-spring-default (all defaults included)
```

### Vert.x chain

```text
r2mo-boot-vertx (includes core Vert.x foundation)
  -> add r2mo-vertx-jooq* as needed
  -> add r2mo-typed-vertx
```

## 4. Forbidden Placement Zones

Do not place any of the following in boot modules:

- business service logic,
- domain-specific defaults,
- tenant-specific configurations,
- project-specific bean definitions,
- controller or endpoint classes.

Boot modules are for assembly only. If code has business meaning, it belongs in the business project.

## 5. Typical Requirement Routing

| Requirement | Landing point |
|---|---|
| Change default JSON library | `r2mo-boot-spring-default` dependency swap, not boot code |
| Add a new framework module to all projects | add to `r2mo-boot-spring` or `r2mo-boot-spring-default` |
| Change Jetty to Tomcat | `r2mo-boot-spring` exclusion/swap |
| Add Vert.x jOOQ support | add `r2mo-vertx-jooq*` to project, boot-vertx already includes core |

## 6. Do Not Do

- Do not inherit `boot-spring-default` and then override half of its defaults. Use `boot-spring` instead.
- Do not add security modules to boot unless they are truly universal. Security plugins belong in the project.
- Do not treat boot modules as a service layer.

## 7. Final Rule

Boot = assembly, not implementation. Read the boot POM to understand what is included, then read each included module's guide for ownership details.
