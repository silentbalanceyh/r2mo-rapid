# Search Hints

This file gives future maintainers a set of high-hit search entry points so they do not have to blindly crawl a large repository.

Start with [`mxt-r2mo-ai-agent-guide.md`](./mxt-r2mo-ai-agent-guide.md) when the requirement comes from a downstream project and the correct framework family is still unclear.

## 1. Search by Topic

### Unified abstraction layer

Search module names:

- `r2mo-spec`
- `r2mo-dbe`
- `r2mo-io`
- `r2mo-jaas`
- `r2mo-jce`
- `r2mo-ams`

Search keywords:

- `DBE`
- `HFS`
- `RFS`
- `HED`
- `SPI`
- `BuilderOf`
- `Cc`
- `Fn`

### Spring side

Search these first:

- `r2mo-spring`
- `r2mo-spring-security`
- `r2mo-spring-security-oauth2`
- `SecurityWebConfiguration`
- `SpringExceptionHandler`
- `SpringE`

### Vert.x side

Search these first:

- `r2mo-vertx`
- `r2mo-vertx-jooq`
- `AsyncDBContext`
- `DBContext`
- `DBJxJson`
- `DBExBase`

## 2. Search by Extension Mechanism

### Search SPI usage points

Keywords:

- `SPI.findOne(`
- `SPI.findOneOf(`
- `SPI.findMany(`
- `SPI.J(`
- `SPI.V_`

High-value locations:

- `r2mo-io`
- `r2mo-jaas`
- `r2mo-vertx-jooq-generate`
- `r2mo-spring-sms`

### Search SPI implementation declarations

Keywords:

- `@SPID`
- `priority =`

High-value locations:

- `r2mo-spring-cache`
- `r2mo-spring-security`
- `r2mo-vertx-jooq`
- `r2mo-xync-sms`

## 3. Search by Layer

### Inspect bootstrap assembly

Search:

- `r2mo-boot-spring/pom.xml`
- `r2mo-boot-spring-default/pom.xml`
- `r2mo-boot-vertx/pom.xml`

Questions this answers:

- What capabilities are included by default?
- What is the difference between abstract boot and default boot?

### Inspect shared contracts

Search:

- `r2mo-spec/pom.xml`
- `openapi`
- `schemas`
- `error code`

### Inspect Spring security plugins

Search:

- `r2mo-spring-security-*`
- `oauth2`
- `jwt`
- `ldap`
- `captcha`
- `UserAuthCache`

## 4. Search by Problem Type

### I want to know whether to change abstraction or implementation

Search first:

- Module list in the root `pom.xml`
- `dependencyManagement`
- Dependency chains in `r2mo-boot-*`

### I want to know whether some capability already supports SPI

Search first:

- `interface name + SPI.find`
- `interface name + @SPID`

### I want to know whether some capability belongs to Spring or spec

Search first:

- `pom.xml` dependency relations
- Whether it directly depends on `spring-*` packages
- Whether OpenAPI / schema / Markdown resource semantics appear

### I am debugging a configuration problem

Search first:

- active profile and application-mode definitions
- env-to-configuration binding points
- `r2mo-boot-*` dependency chain
- provider-selection keywords such as `SPI.findOne`, `SPI.findOneOf`, and `@SPID`

Debugging order for agents:

1. inspect env/application model
2. inspect bootstrap and shared runtime contract
3. inspect provider selection path
4. only then inspect business code

Why this order matters:

- in R2MO, many configuration failures are actually runtime-model selection issues,
- shared env variables can affect tenant, locale, style, and app mode before business services run,
- and looking at business code first often hides the real root cause.

## 5. Recommended Reading Paths

### To add a new authentication mode

1. `r2mo-spring-security/pom.xml`
2. `r2mo-spring-security-*`
3. `r2mo-spring-security-oauth2/README.md`
4. Search `@SPID` / `SPI.findOneOf`

### To add a new storage backend

1. `r2mo-io`
2. Search `HStore`, `HTransfer`
3. Search `FactoryIoCommon`
4. Search `SPI.findOne(`

### To modify query capabilities

1. `r2mo-dbe`
2. `r2mo-dbe-mybatisplus` / `r2mo-dbe-jooq`
3. `r2mo-vertx-jooq`
4. Search `criteria`, `pager`, `sorter`

### To inspect framework ownership with graph assistance

1. Read [`mxt-r2mo-ai-agent-guide.md`](./mxt-r2mo-ai-agent-guide.md) for the final generalized agent reading protocol.
2. Read [`code-review-graph-r2mo-analysis.md`](./code-review-graph-r2mo-analysis.md) for the latest graph-backed repository shape.
3. Read [`mxt-r2mo-mcp-rules.md`](./mxt-r2mo-mcp-rules.md) to map triggers to capability families.
4. Read [`framework-trigger-matrix.md`](./framework-trigger-matrix.md) when the requirement starts from vague business wording.
5. Use the root `pom.xml` plus the graph family to narrow to the smallest relevant module set.
6. Open source files only after the family is narrowed.

### To operate code-review-graph for this repository

1. Prefer the repository wrapper: `bin/mxt-r2mo-graph`.
2. Read [`code-review-graph-usage.md`](./code-review-graph-usage.md) for build/update/status/visualize/serve guidance.
3. Use `build` for first-time or large refreshes, `update` for daily incremental refreshes.
4. Avoid parallel graph commands against the same repo database.

## 6. Recommended Agent Tooling for Java/Maven Navigation

For agent work in `r2mo-rapid`, the most effective navigation pattern is usually:

1. read the root `pom.xml`
2. read the target module `pom.xml`
3. search for symbol and SPI usage
4. unfold only the classes that look like real entry points

Recommended tooling approach:

- Use `Read` for root and module `pom.xml` files first.
- Use `Grep` for `SPI.findOne`, `SPI.findOneOf`, `SPI.findMany`, `@SPID`, module names, and key framework nouns such as `DBE`, `HFS`, `RFS`, `SpringE`, `SecurityWebConfiguration`.
- Use symbol-aware search or outline tools when available to inspect Java classes without reading whole files.
- Prefer dependency-chain reasoning before class-by-class crawling.

Good agent defaults for this repository:

- Start with module boundaries before reading implementation code.
- Treat `pom.xml` files as the fastest truth source for ownership and layering.
- When the target is Spring security behavior, inspect `r2mo-spring-security` and the concrete `r2mo-spring-security-*` module together.
- When the target is DB behavior, inspect `r2mo-dbe` first, then the concrete implementation module, then the container-specific side.

## 7. Final Handle

When you cannot find the entry point, do not search business words first. Search these first:

- Module boundaries
- SPI call sites
- `pom.xml` dependency chains

In this repository, **dependency relationships themselves are the most reliable navigation map.**
