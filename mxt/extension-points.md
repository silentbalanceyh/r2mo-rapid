# Extension Points

This file records only **evidence-based extension entry points**, to avoid misclassifying ordinary business composition as framework extension points.

## 1. General Principle

R2MO extensions are not primarily built around inheriting large base classes. They are mainly built through:

- Unified discovery through `SPI`
- Implementation declaration and priority through `@SPID`
- Auto-configuration and configuration classes in Spring modules
- Default dependency bundles in boot modules

## 2. SPI Is the Primary Extension Channel

Based on actual code usage, multiple locations in the repository select implementations through `SPI.findOne`, `SPI.findOneOf`, and `SPI.findMany`.

Typical locations:

- `r2mo-io`
  - `FactoryIoCommon` selects underlying storage / transfer implementations via `SPI.findOne(HStore.class, name)` and `SPI.findOne(HTransfer.class, name)`.
  - `HFS` retrieves the IO action entry through `SPI.SPI_IO.ioAction()`.
- `r2mo-jaas`
  - `UserCache` selects a cache implementation through `SPI.findOneOf(UserCache.class)`.
  - `MSUser` selects a claim extension through `SPI.findOneOf(UserClaim.class)`.
- `r2mo-vertx-jooq-generate`
  - `JooqSourceConfigurer` collects jOOQ forced-type configuration through `SPI.findMany(TypeOfJooq.class)`.
- `r2mo-spring-sms`
  - `SmsClientImpl` selects an SMS provider through `SPI.findOne(UniProvider.class, "UNI_SMS")`.

## 3. @SPID Is the Entry Point for Implementation Declaration and Priority

`@SPID` appears in multiple places in the codebase:

- `r2mo-spring-cache`
  - `EhcacheCacheAtSecurity`
  - `CaffeineCacheAtSecurity`
  - `RedisCacheAtSecurity`
  - `RedCacheAtSecurity`
  - `SpringCacheAtSecurity`
- `r2mo-spring-security`
  - `UserAuthCache`
- `r2mo-vertx-jooq`
  - `AsyncDBContext`
- `r2mo-xync-sms`
  - `SmsProvider`

This reveals a common extension model:

1. Define a capability interface.
2. Mark multiple implementations with `@SPID`.
3. Select implementations by name or priority through `SPI` at runtime.

## 4. SPI Hotspots and Common Misreads

### High-value SPI hotspots

Agents should inspect these areas early because they repeatedly act as extension switches:

- `r2mo-io`: provider selection for storage and transfer
- `r2mo-jaas`: user cache and claim enrichment
- `r2mo-spring-security`: security-side cache and auth-related extension hooks
- `r2mo-spring-cache`: multiple cache implementations with `@SPID` priorities
- `r2mo-vertx-jooq-generate`: aggregated jOOQ type customization through SPI
- `r2mo-xync-sms` and `r2mo-spring-sms`: provider selection chain for SMS

### Common misreads

- Mistaking ordinary helper classes for extension points just because they sit in an extension module.
- Mistaking a single implementation with `@SPID` for proof that a capability is universally pluggable.
- Missing that some Spring modules consume SPI indirectly through higher-level wrapper classes.
- Treating `boot` dependency aggregation as if it were itself a runtime SPI hotspot.

A good rule: if there is no interface + multiple implementations + runtime selection path, it is probably not a real SPI hotspot.

## 5. Capabilities That Fit SPI Well

- Provider-style capabilities
  - Storage
  - Transfer
  - SMS
  - Email
  - Claim enrichment
  - Cache providers
- Container-internal strategy capabilities
  - User cache
  - Security cache
  - DB context selection
  - jOOQ type mapping

## 5. Capabilities That Should Not Become SPI

The following are usually not worth promoting into SPI:

- Rules used only once in a single project
- Strongly business-semantic workflow nodes
- Field conversions private to a single service
- Logic inside a Controller / Service that is only called by that class itself

## 6. Spring-Side Extension Points

### 6.1 `r2mo-spring`

Its responsibility is to provide the Spring base integration surface.
Suitable extension topics include:

- Common exception handling
- Shared Bean / AOP / Web cross-cutting capabilities
- Container-level cross-cutting logic without business semantics

### 6.2 `r2mo-spring-security`

This is the Spring security extension base.
Suitable additions include:

- New login modes
- New authentication providers
- New token / claim strategies
- New cache backends

Existing evidence includes:

- `SecurityWebConfiguration`
- `UserAuthCache`
- The `ActuatorRequestUri` comment, which states that it can be auto-registered through SPI and take effect across all R2MO Spring projects.

### 6.3 `r2mo-spring-security-*`

This is the clearest plugin layer:

- email
- jwt
- ldap
- oauth2
- oauth2client
- sms
- weco

`r2mo-spring-security-oauth2/README.md` explicitly states:

- SPI auto-discovery and registration
- Coexistence with existing Basic/JWT authentication
- OAuth2 JWT mode can automatically disable the legacy JWT Filter

These modules illustrate one rule: **when adding a new authentication mode, prefer landing it in `r2mo-spring-security-*`, not directly in business projects.**

## 7. Boot-Layer Extension Points

### `r2mo-boot-spring`

It extends "bootstrap abstraction", not business functionality.
Suitable changes include:

- Bringing in new default base dependencies
- Providing new standard bootstrap combinations
- Adjusting default container assembly strategy

### `r2mo-boot-spring-default`

It extends the "default combination bundle".
The current default combination includes:

- `r2mo-spring-json`
- `r2mo-typed-hutool`
- `r2mo-io-local`
- `r2mo-spring-mybatisplus`

Only if a requirement is essentially "all our Spring projects should bring X by default" should it be considered here.

## 8. Recommended Extension Decisions

### Scenario A: Add a unified storage backend

Landing point: `r2mo-io` abstraction + a new provider implementation module.

### Scenario B: Add a Spring login mode

Landing point: `r2mo-spring-security-*`.

### Scenario C: Add a default Spring bootstrap combination

Landing point: `r2mo-boot-spring-default` or a new default boot package.

### Scenario D: Add unified API documentation fragments or error codes

Landing point: `r2mo-spec`.

## 9. SPI in Modular Design: When to Extend SPI vs Change Default Implementations

This is the most important judgment call for framework extension work in `r2mo-rapid`.

### Prefer SPI extension when

- The default implementation is already correct for most projects but you need a **different behavior for specific projects or environments** (e.g., different cache backend, different SMS vendor, different storage provider).
- You want to **coexist** with the default rather than replace it globally.
- You need **runtime selection** based on configuration, environment, or named keys.
- The new implementation fits the same interface contract without changing callers.

### Prefer changing the default implementation when

- The **default is wrong or broken** for all use cases, not just your specific project.
- The abstraction interface itself needs to change, which means the SPI boundary must also evolve.
- The existing `@SPID` priority mechanism would require a workaround rather than a natural extension.

### Never do this

- Do not bypass SPI by directly hardcoding a class instantiation in framework code that used to call `SPI.findOne*`.
- Do not add project-specific default behavior to the shared boot or abstraction layer.
- Do not create a new SPI interface just to avoid refactoring — check if an existing one already covers the need.

### SPI hotspots to watch in `r2mo-rapid`

These are the highest-value SPI extension points by module impact:

| Module | SPI target | Impact area |
|---|---|---|
| `r2mo-io` | `HStore`, `HTransfer` | Storage and remote file transfer |
| `r2mo-jaas` | `UserCache`, `UserClaim` | User session and claim enrichment |
| `r2mo-spring-cache` | `*CacheAtSecurity` implementations | Security-side cache provider selection |
| `r2mo-spring-sms` | `UniProvider` | SMS vendor selection |
| `r2mo-vertx-jooq-generate` | `TypeOfJooq` | jOOQ type system customization |
| `r2mo-vertx-jooq` | `DBContext` | Async DB context routing |

## 10. One Hard Rule

Only when an extension point can be described as "interface + multiple implementations + unified selection strategy" is it worth designing as a framework extension point.

Otherwise, it is usually just a code organization concern within a project.
