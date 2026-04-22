# MXT-R2MO MCP Rules

> Rules for AI agents connecting to `mxt-r2mo`.
> These rules are repository-reading rules, not business-project rules.
> They tell an agent how to combine `mxt/` documents, source code, and `code-review-graph` results when reading `r2mo-rapid` through MCP.

## 1. Purpose

`mxt-r2mo` exposes framework source trees through MCP so downstream projects can inspect R2MO directly.
When the connected repository is `r2mo-rapid`, the agent should not read the repo as if it were a business system.
It should read it as:

- a framework abstraction layer,
- a Spring/Vert.x dual-container scaffold,
- a reusable capability repository,
- and a graph-analyzable codebase with stable capability families.

The goal is to help an agent answer three questions efficiently:

1. Where does this requirement belong in the framework?
2. Which framework code path is most likely relevant?
3. Which source files should be opened first, and which should be deferred?

## 2. Required Reading Order

For framework questions against `mxt-r2mo`, use this order:

```text
1. Read mxt/ entry docs first.
2. Read root pom.xml and target module pom.xml.
3. Use code-review-graph output to narrow capability family / hotspot / community.
4. Read concrete source files only after narrowing.
5. If needed, confirm contracts in r2mo-spec.
```

Do not start by randomly opening Java files across many modules.
Do not start from business vocabulary alone.

## 3. Primary Documents to Read First

Before graph or source inspection, prefer these files:

- `mxt/ai-agent-fast-start.md`
- `mxt/mcp-shortest-path.md`
- `mxt/distilled-capability-cards.md`
- `mxt/mcp-token-saving-rules.md`
- one matching `mxt/mcp-route-*.md` file if the trigger family is already known
- `mxt/mxt-r2mo-ai-agent-guide.md`
- `mxt/core-capability-index.md`
- `mxt/README.md`
- `mxt/mcp-route-shared-contracts.md`
- `mxt/mcp-route-shared-capability-modules.md`
- `mxt/mcp-route-spring-integrations.md`
- `mxt/mcp-route-vertx-jooq.md`
- `mxt/mcp-route-code-review-graph.md`
- `mxt/mcp-route-spring-security.md`
- `mxt/mcp-route-code-generator.md`
- `mxt/framework-map.md`
- `mxt/abstraction-rules.md`
- `mxt/extension-points.md`
- `mxt/dual-side-development.md`
- `mxt/search-hints.md`
- `mxt/code-review-graph-r2mo-analysis.md`

Prefer the smallest valid entry doc.
Do not read all files above by default.

When the peer-side router already matched a specialized route, also read:

- `mxt/spring-security-mcp-guide.md` for Spring Security questions
- `mxt/code-generator-usage.md` for generator questions

If the task is specifically license/cryptography-related, also read:

- `mxt/framework-trigger-matrix.md`
- `mxt/code-review-graph-r2mo-analysis.md`
- and then enter the `r2mo-jce` module source directly

## 4. When Graph-Guided Reading Is Mandatory

Use graph-guided narrowing before broad source reading when at least one of the following is true:

1. The requirement is framework-level, not app-level.
2. The question could touch multiple R2MO modules.
3. The requirement uses vague terms like `login`, `query`, `cache`, `license`, `upload`, `token`, `provider`, `generator`.
4. You need to find the likely execution path, owner module, or change impact.
5. You suspect the behavior is implemented through helper chains or SPI rather than one obvious entry class.

In those cases, first consult the graph-backed analysis and narrow to a capability family.

## 4.1 MCP Regex Routing

For peer-side routing, prefer the dedicated route files:

- `mxt/mcp-route-shared-contracts.md`
- `mxt/mcp-route-shared-capability-modules.md`
- `mxt/mcp-route-spring-integrations.md`
- `mxt/mcp-route-vertx-jooq.md`
- `mxt/mcp-route-code-review-graph.md`
- `mxt/mcp-route-spring-security.md`
- `mxt/mcp-route-code-generator.md`

Those route files are intentionally more specific than the generic trigger mapping in this file.
Use them first for:

- shared contract and metadata requests,
- shared capability and SPI-first requests,
- non-security Spring and delivery-provider integration requests,
- Vert.x / jOOQ-side requests,
- graph-first analysis requests,
- Spring Security-specific requests,
- generator-specific requests.

If the agent is still cold before routing, first read:

- `mxt/ai-agent-fast-start.md`
- then `mxt/mcp-shortest-path.md`

If the agent already knows the module family, skip both and jump straight to the matching route or module guide.

## 5. When Graph-Guided Reading Is Optional

Graph use is optional when:

1. The user already gave an exact file path.
2. The symbol is already known and unique.
3. The question is only about one local doc file under `mxt/`.
4. The task is purely editorial and not about code ownership or execution logic.
5. A single boundary/guide file already answers the ownership question.

## 6. Trigger-to-Family Mapping

When a requirement contains the following trigger terms, narrow to the matching graph family first.

### 6.1 Security / auth triggers

Trigger words:

- login
- auth
- authenticate
- authorize
- session
- captcha
- token
- jwt
- oauth2
- ldap
- security
- principal
- claim

Preferred targets:

- communities: `auth-security`, `oauth2-token`, `session-user`
- modules: `r2mo-spring-security`, `r2mo-spring-security-*`, `r2mo-jaas`

### 6.2 License / crypto triggers

Trigger words:

- license
- activation
- sign
- verify
- key
- pem
- encrypt
- decrypt
- jce
- fingerprint
- rsa
- ecc
- ed25519
- sm2
- sm4

Preferred targets:

- community: `common-license`
- module: `r2mo-jce`
- docs: `framework-trigger-matrix.md`, `code-review-graph-r2mo-analysis.md`

### 6.3 DBE / query / generator triggers

Trigger words:

- dbe
- criteria
- pager
- sorter
- projection
- query
- generator
- sql
- jooq
- mybatisplus
- converter
- metadata generation

Preferred targets:

- communities: `generate-generator`, `jooq-async`, `dbe-async`, `spi-jooq`, `postgres-converter`
- modules: `r2mo-dbe*`, `r2mo-vertx-jooq*`, `r2mo-dbe-mybatisplus`

### 6.4 IO / transfer triggers

Trigger words:

- io
- file
- upload
- download
- storage
- transfer
- hstore
- htransfer
- local file
- remote file

Preferred targets:

- communities: `operation-transfer`, `common-transfer`
- modules: `r2mo-io`, `r2mo-io-local`

### 6.5 Cache / SPI / provider-selection triggers

Trigger words:

- cache
- spi
- spid
- provider
- extension point
- implementation selection
- plugin selection

Preferred targets:

- communities: `cache-cache`, `spi-impl`, `spi-jooq`
- modules: `r2mo-spring-cache`, `r2mo-spring-security`, `r2mo-io`, `r2mo-jaas`

## 7. Core Repository Reading Rules

### Rule 1 — Treat module boundaries as the first ownership signal

For `r2mo-rapid`, module ownership from the root `pom.xml` is more reliable than guessing from class names.
Always identify the module family first.

### Rule 2 — Treat graph communities as a narrowing signal, not final truth

Graph communities are very useful for clustering likely related files, but they are not authoritative architecture.
Use them to narrow the search space.
Use source code and `mxt/` docs to confirm conclusions.

### Rule 3 — Prefer `CALLS` and `IMPORTS_FROM` over inheritance hunting

This repository is method-heavy and helper-heavy.
Most real reading value comes from:

- `CALLS` for execution logic,
- `IMPORTS_FROM` for ownership and dependency context,
- `INHERITS` only as a secondary signal.

### Rule 4 — Read shared abstractions before container-specific landings

If a requirement could belong to `ams / spec / dbe / io / jaas / jce`, inspect that shared layer first.
Only move into `r2mo-spring*` or `r2mo-vertx*` after confirming the requirement is container-specific.

### Rule 5 — For Spring-side behavior, read the base and the plugin together

If the target is Spring security or a Spring-side extension:

- read `r2mo-spring` or `r2mo-spring-security` first,
- then read the specific `r2mo-spring-*` or `r2mo-spring-security-*` plugin module.

Do not read only the plugin and assume you understand the full behavior.

### Rule 6 — For Vert.x/jOOQ behavior, read async and shared modules together

If the target is Vert.x-side DB behavior:

- read `r2mo-vertx` and `r2mo-vertx-jooq*` together,
- then confirm whether the query model actually belongs in `r2mo-dbe`.

### Rule 7 — High-risk graph symbols require conservative reasoning

If a symbol appears in graph `risk_index` as high risk or security relevant, do not infer behavior casually.
Open the exact source and confirm the implementation directly.

### Rule 8 — `r2mo-spec` is contract-only

If you are reading `r2mo-spec`, do not expect runtime behavior there.
Use it for:

- shared schema,
- OpenAPI contracts,
- marker metadata,
- error structure,
- documentation conventions.

Move back into runtime modules for execution logic.

## 8. How To Find Development Logic Under MCP

When the user asks “how does this framework feature work?”, follow this sequence:

```text
1. Determine trigger family.
2. Map to likely module(s) and graph community.
3. Read mxt docs for ownership/boundary first.
4. Read module pom.xml if ownership is still unclear.
5. Open the smallest relevant source set.
6. Trace only the critical call path.
7. Summarize the logic in ownership order, not file-open order.
```

That gives stable answers with lower token cost than scanning broad directory trees.

## 9. How To Handle Framework Code On Demand

If the requirement only needs one framework capability:

- do not read unrelated families;
- do not scan all `r2mo-spring-*` or all `r2mo-vertx-*` modules;
- do not expand to business-project assumptions.

Read only the minimal capability family implied by the trigger set.

Shortest valid paths:

- exact module known -> module guide -> exact source
- route family known -> route file -> one guide -> exact source
- trigger vague -> `ai-agent-fast-start.md` -> `core-capability-index.md` -> one route file

Examples:

- JWT issue → `r2mo-spring-security` + `r2mo-spring-security-jwt`
- Shared marker or metadata issue → `mcp-route-shared-contracts.md` + `r2mo-ams` / `r2mo-spec`
- License verification → `r2mo-jce` + `framework-trigger-matrix.md` + exact `r2mo-jce` source
- Upload/transfer issue → `r2mo-io` + `r2mo-io-local`
- Query syntax issue → `r2mo-dbe` + concrete implementation module
- Spring cache/email/sms/weco issue → `mcp-route-spring-integrations.md` + exact `r2mo-spring-*` module
- Delivery provider issue → `mcp-route-spring-integrations.md` + exact `r2mo-xync-*` module
- Vert.x runtime or jOOQ bridge issue → `mcp-route-vertx-jooq.md` + exact `r2mo-vertx-jooq*` module

## 10. How To Understand Framework Code Mentioned Indirectly by Requirements

Many requirements use business phrasing while actually referring to framework capability.
Map them before reading code.

Examples:

- “token refresh problem” → not business logic first; probably `jaas` / `spring-security-*`
- “license activation” → not subscription or billing first; probably `jce`
- “dynamic search” → not controller first; probably `dbe`
- “storage provider mismatch” → not service class first; probably `io` + SPI

The agent should normalize the requirement into a framework family before opening source files.

## 11. Known Operational Notes for code-review-graph in This Repo

1. The generated database lives under `.code-review-graph/graph.db`.
2. It should remain ignored by git.
3. Avoid running parallel graph commands against the same repository database; `status` can fail with `database is locked` if another graph process is active.
4. Graph output is a narrowing tool, not a replacement for source confirmation.

## 12. Final Default

If uncertain, use this default:

```text
ai-agent-fast-start.md -> one route file -> one boundary/guide doc -> root/module pom if needed -> graph narrowing if needed -> exact source files -> answer
```

That is the intended MCP reading path for `mxt-r2mo`.
