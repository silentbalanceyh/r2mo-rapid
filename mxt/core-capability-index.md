# Core Capability Index

> Graph-backed capability index for AI agents reading `r2mo-rapid`.
> Use this file when the goal is to extract the framework's core functions quickly before opening many source files.

## 1. Why This File Exists

The repository is large enough that module names alone are not always the best first summary.

Recent `code-review-graph` analysis of this repository shows:

- `929` files
- `5281` graph nodes
- `22980` graph edges
- a method-heavy structure dominated by `CALLS` and `IMPORTS_FROM`

That means an agent often needs a capability-first view, not just a module-first view.

This file turns the strongest graph communities into an AI-agent reading index.

## 2. How To Use This Index

When the user asks what the framework can do, or when the wording is vague:

1. find the nearest capability cluster below,
2. read the listed docs first,
3. narrow to the listed modules,
4. only then open exact source files.

This file is an index, not an execution guide.
Routing details still belong to the `mcp-route-*.md` files.

## 3. Graph-Backed Core Capability Clusters

### 3.1 Shared foundation and agreed semantics

Graph signals:

- community: `util-string`
- dominant module family: `r2mo-ams`
- strongest role: shared vocabulary, base semantics, framework primitives

Read first:

- `framework-map.md`
- `abstraction-rules.md`
- `mcp-route-shared-contracts.md`

Typical ownership:

- agreed metadata
- shared foundation types
- base utility and framework vocabulary

### 3.2 Shared contract and specification surface

Primary module:

- `r2mo-spec`

Read first:

- `spec-boundary.md`
- `mcp-route-shared-contracts.md`
- `framework-map.md`

Typical ownership:

- OpenAPI and schemas
- marker metadata
- error-code and contract definitions

### 3.3 Authentication and security

Graph signals:

- communities: `auth-security`, `oauth2-token`, `session-user`
- strong module families: `r2mo-spring-security*`, `r2mo-jaas`

Read first:

- `spring-security-mcp-guide.md`
- `mcp-route-spring-security.md`
- `mcp-route-shared-capability-modules.md`

Typical ownership:

- filter-chain composition
- login-mode routing
- token and session behavior
- auth cache and claim-base contracts

### 3.4 Cryptography, signing, licensing

Graph signals:

- community: `common-license`
- dominant module: `r2mo-jce`

Read first:

- `mcp-route-shared-capability-modules.md`
- `framework-trigger-matrix.md`
- `code-review-graph-r2mo-analysis.md`

Typical ownership:

- encryption and decryption
- signing and verification
- key handling
- license generation, validation, and activation

### 3.5 Query, CRUD, DBE, and persistence execution

Graph signals:

- communities: `spi-impl`, `dbe-async`, `jooq-async`, `postgres-converter`
- dominant module families: `r2mo-dbe*`, `r2mo-dbe-mybatisplus`, `r2mo-dbe-jooq`, `r2mo-vertx-jooq*`

Read first:

- `backend-dbe-guide.md`
- `dbe-implementation-boundary.md` — MyBatis-Plus vs jOOQ selection boundary
- `mcp-route-shared-capability-modules.md`
- `mcp-route-vertx-jooq.md`
- `code-generator-usage.md` when generation is involved

Typical ownership:

- query shape
- CRUD semantics
- MyBatis-Plus implementation
- async Vert.x/jOOQ runtime bridge

### 3.6 Code generation

Graph signals:

- community: `generate-generator`
- dominant modules: `r2mo-vertx-jooq-generate`, `r2mo-dbe-mybatisplus`, `r2mo-boot-spring`

Read first:

- `code-generator-usage.md`
- `mcp-route-code-generator.md`
- `mcp-route-vertx-jooq.md`

Typical ownership:

- Spring scaffold generation
- MyBatis-Plus generator pipeline
- jOOQ forced type and source generation

### 3.7 Storage and transfer

Graph signals:

- communities: `operation-transfer`, `common-transfer`
- dominant modules: `r2mo-io`, `r2mo-io-local`

Read first:

- `mcp-route-shared-capability-modules.md`
- `search-hints.md`
- `extension-points.md`

Typical ownership:

- storage abstraction
- local file implementation
- transfer token, chunk, and progress flows

### 3.8 Cache and provider selection

Graph signals:

- community: `cache-cache`
- related communities: `spi-impl`, `session-user`
- dominant modules: `r2mo-spring-cache`, `r2mo-jaas`, `r2mo-spring-security`

Read first:

- `extension-points.md`
- `mcp-route-spring-integrations.md`
- `mcp-route-shared-capability-modules.md`

Typical ownership:

- cache backend selection
- security-adjacent token/cache management
- SPI-driven provider choice

### 3.8.5 Typed implementation and data-type selection

Primary module families:

- `r2mo-typed-hutool`
- `r2mo-typed-vertx`

Read first:

- `typed-implementation-boundary.md`
- `mcp-route-shared-capability-modules.md`

Typical ownership:

- Hutool-based synchronous data-type operations
- Vert.x-based asynchronous data-type operations
- selection between typed implementations based on runtime environment

### 3.9 Spring-side integrations and delivery adapters

Graph signals:

- communities visible in module form: `config-swagger`, `spi-excel`
- related delivery clusters: `email-email`, `sms-sms`, `weco-we`

Read first:

- `mcp-route-spring-integrations.md`
- `spring-layer-map.md`
- `spring-adapter-guides.md` — json, template, mybatisplus adapter ownership
- `spring-delivery-boundary.md` — three-layer email/sms/weco architecture
- `extension-points.md`

Typical ownership:

- cache, docs, excel, json, MyBatis Plus Spring landing
- Spring-side email, SMS, template, and WeCom integrations

### 3.10 Delivery-provider foundation

Primary module families:

- `r2mo-xync-email`
- `r2mo-xync-sms`
- `r2mo-xync-weco`

Read first:

- `mcp-route-spring-integrations.md`
- `extension-points.md`
- `framework-map.md`

Typical ownership:

- vendor/provider-side email contracts
- SMS provider abstractions
- WeCom / WeChat integration foundations

These modules are easy to miss if an agent only follows Spring-side names.
They are part of the reusable delivery foundation and often sit below the Spring integrations.

### 3.11 Vert.x-side runtime

Graph signals:

- communities: `jooq-async`, `dbe-async`
- dominant modules: `r2mo-vertx`, `r2mo-vertx-jooq*`

Read first:

- `mcp-route-vertx-jooq.md`
- `dual-side-development.md`
- `search-hints.md`

Typical ownership:

- async DB context
- runtime bridge execution
- Vert.x-side query and JSON mapping

## 4. Fast Routing Table

| User intent | Start here |
|---|---|
| What are the framework's main capabilities? | this file |
| Where should this shared capability live? | `mcp-route-shared-capability-modules.md` |
| Is this contract or runtime? | `mcp-route-shared-contracts.md` |
| Which Spring integration owns this? | `mcp-route-spring-integrations.md` |
| Which Vert.x/jOOQ module owns this? | `mcp-route-vertx-jooq.md` |
| Which generator family owns this? | `mcp-route-code-generator.md` |
| Which security plugin or auth flow owns this? | `mcp-route-spring-security.md` |
| Login vs delivery boundary for email/sms/weco? | `spring-delivery-boundary.md` |
| Typed-hutool vs typed-vertx selection? | `typed-implementation-boundary.md` |
| MyBatis-Plus vs jOOQ DBE implementation? | `dbe-implementation-boundary.md` |
| Boot assembly and default dependencies? | `boot-assembly-guide.md` |
| Spring adapter (json/template/mybatisplus) ownership? | `spring-adapter-guides.md` |

## 5. Final Rule

If the immediate goal is capability extraction rather than source debugging, read:

```text
core-capability-index.md -> matching mcp-route-*.md -> module docs -> exact source
```
