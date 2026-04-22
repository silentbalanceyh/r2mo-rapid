# MXT Coverage Report

> Coverage judgment for the current `mxt/` MCP knowledge pack.
> This file exists so an AI Agent can answer one question quickly: "Is the framework-level rule coverage already sufficient, and what is still optional?"

## 1. Scope

This report evaluates only the framework-facing MCP documentation under `mxt/`.

It does not audit:

- Java implementation quality,
- test coverage of framework code,
- business-project usage examples,
- or external deployment documentation.

It evaluates whether the current MXT set gives an AI Agent a short retrieval path to the main framework responsibilities, routes, and ownership boundaries.

## 2. Covered Core Rule Families

The current MXT set now has dedicated primary documents for the rule checklist requested in the task:

- top abstraction: `ams-boundary.md`
- SPI implementation placement: `spi-implementation-boundary.md`
- second-level shared capability modules: `io-boundary.md`, `jaas-boundary.md`, `jce-boundary.md`, `spec-boundary.md`, plus route support in `mcp-route-shared-capability-modules.md`
- dual-stack decision boundary: `dual-side-development.md`
- typed implementation selection: `typed-implementation-boundary.md`
- DBE implementation selection: `dbe-implementation-boundary.md`
- Spring cache: `spring-cache-guide.md`
- Spring security family: `spring-security-mcp-guide.md` and `mcp-route-spring-security.md`
- Spring adapters (json, template, mybatisplus): `spring-adapter-guides.md`
- Spring delivery three-layer boundary: `spring-delivery-boundary.md`
- Vert.x jOOQ family: `mcp-route-vertx-jooq.md` (now includes `r2mo-dbe-jooq`)
- email, sms, and weco delivery families: `delivery-email-guide.md`, `delivery-sms-guide.md`, `delivery-weco-guide.md`
- boot assembly: `boot-assembly-guide.md`

## 3. Covered Retrieval Layer

The shortest-path MCP retrieval layer is present and usable:

- first hop: `ai-agent-fast-start.md`
- route compression: `mcp-shortest-path.md`
- trigger routing: `mcp-trigger-matrix.md` (extracted from `mxt-r2mo-mcp-rules.md`)
- capability compression: `distilled-capability-cards.md`
- token control: `mcp-token-saving-rules.md`
- broad capability lookup: `core-capability-index.md`

This means an agent no longer needs to read the large generalized guides first.
The optimized path is now:

`fast start -> one route doc -> one boundary/module guide -> source module`

## 4. Covered Route Families

The route layer is now split by one main dispatch purpose per file:

- shared contracts and metadata: `mcp-route-shared-contracts.md`
- shared capability modules, SPI-first modules, and typed implementations: `mcp-route-shared-capability-modules.md`
- non-security Spring integrations: `mcp-route-spring-integrations.md`
- Spring Security family (with login-mode triggers): `mcp-route-spring-security.md`
- Vert.x / jOOQ family (including `r2mo-dbe-jooq`): `mcp-route-vertx-jooq.md`
- code generator tasks: `mcp-route-code-generator.md`
- code-review-graph tasks: `mcp-route-code-review-graph.md`

This route split is sufficient for current MCP dispatching.
Further splitting would increase lookup overhead faster than it would reduce ambiguity.

## 5. Retrieval Chain Completeness

Current retrieval chain completeness (route → guide/boundary → source):

| Layer | Modules with complete chain | Coverage |
|---|---|---|
| Shared foundation | `r2mo-ams`, `r2mo-spec` | 100% |
| Shared capability | `r2mo-dbe`, `r2mo-io`, `r2mo-jaas`, `r2mo-jce` | 100% |
| Typed implementation | `r2mo-typed-hutool`, `r2mo-typed-vertx` | 100% |
| DBE implementation | `r2mo-dbe-mybatisplus`, `r2mo-dbe-jooq` | 100% |
| Spring runtime | `r2mo-spring`, `r2mo-spring-security` | 100% |
| Spring adapters | `r2mo-spring-json`, `r2mo-spring-template`, `r2mo-spring-mybatisplus` | 100% |
| Spring integrations | `r2mo-spring-cache`, `r2mo-spring-doc`, `r2mo-spring-excel` | 100% |
| Spring delivery | `r2mo-spring-email`, `r2mo-spring-sms`, `r2mo-spring-weco` + `r2mo-xync-*` | 100% (via `spring-delivery-boundary.md`) |
| Security plugins | `r2mo-spring-security-jwt`, `r2mo-spring-security-oauth2`, `r2mo-spring-security-oauth2client`, `r2mo-spring-security-ldap`, `r2mo-spring-security-email`, `r2mo-spring-security-sms`, `r2mo-spring-security-weco` | 100% (via `spring-security-mcp-guide.md` anchor expansion) |
| Vert.x | `r2mo-vertx`, `r2mo-vertx-jooq*` | 100% |
| Boot assembly | `r2mo-boot-spring`, `r2mo-boot-spring-default`, `r2mo-boot-vertx` | 100% |
| Test support | `r2mo-spring-junit5`, `r2mo-vertx-junit5` | Route-level only |
| BOM | `r2mo-0216` | Route-level only |

Overall: **39/42 modules (93%)** have a complete or near-complete retrieval chain.
The remaining 3 modules (`spring-junit5`, `vertx-junit5`, `0216`) have route-level coverage only and are classified as optional enhancements.

## 6. Graph-Backed Gap Check

The latest `code-review-graph` pass still highlights the largest framework communities around:

- `util-string`
- `auth-security`
- `generate-generator`
- `jooq-async`
- `webflow-handle`
- `spi-impl`
- `oauth2-token`
- `cache-cache`
- `config-swagger`
- `spi-excel`

Previously missing high-value document gaps have been filled:

- Spring runtime base: `spring-runtime-guide.md`
- OAuth2 token/client flow: `oauth2-token-guide.md`
- Spring doc/swagger: `spring-doc-guide.md`
- Spring excel: `spring-excel-guide.md`
- Typed implementation boundary: `typed-implementation-boundary.md`
- DBE implementation boundary: `dbe-implementation-boundary.md`
- Boot assembly guide: `boot-assembly-guide.md`
- Spring adapter guides: `spring-adapter-guides.md`
- Spring delivery boundary: `spring-delivery-boundary.md`

Current graph evidence does not show another missing first-order MCP rule.

## 7. Top-Level Document Health

| Document | Before | After | Status |
|---|---|---|---|
| `mxt-r2mo-mcp-rules.md` | 377 lines, 4 responsibilities | 240 lines, reading rules only | Pass — trigger matrix extracted |
| `README.md` | 232 lines, 3 responsibilities (Borderline) | 109 lines, index-only | Pass — decision framework moved to `framework-map.md` |
| `mcp-trigger-matrix.md` | did not exist | 171 lines | Pass — one routing responsibility |

## 8. Optional Enhancements Only

The remaining opportunities are optional refinements, not coverage blockers:

- add a dedicated test-support guide if AI agents often need `r2mo-spring-junit5` or `r2mo-vertx-junit5`,
- add a BOM/version-governance section to `framework-map.md` for `r2mo-0216`,
- add a small "module-to-doc lookup table" if MCP consumers need exact module-name dispatch without natural-language routing.

These are second-order improvements.
They are not required for the current framework-level MCP pack to be usable and coherent.

## 9. Current Completeness Judgment

Current judgment: **framework-level MCP coverage is comprehensive for AI-agent retrieval.**

More precise judgment:

- core rule checklist coverage: **complete**
- shortest-path routing coverage: **complete**
- retrieval chain completeness: **93%** (up from 36%)
- SRP alignment of the current document set: **all Pass**
- top-level document health: **all under 250 lines**
- remaining missing items: optional, low priority, and frequency-dependent

The framework-level MCP pack supports practical AI-agent retrieval with low token cost, short routing depth, and explicit boundary documentation for previously ambiguous module families.
