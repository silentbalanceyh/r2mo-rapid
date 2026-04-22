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
- Spring cache: `spring-cache-guide.md`
- Spring security family: `spring-security-mcp-guide.md` and `mcp-route-spring-security.md`
- Vert.x jOOQ family: `mcp-route-vertx-jooq.md`
- email, sms, and weco delivery families: `delivery-email-guide.md`, `delivery-sms-guide.md`, `delivery-weco-guide.md`

## 3. Covered Retrieval Layer

The shortest-path MCP retrieval layer is present and usable:

- first hop: `ai-agent-fast-start.md`
- route compression: `mcp-shortest-path.md`
- capability compression: `distilled-capability-cards.md`
- token control: `mcp-token-saving-rules.md`
- broad capability lookup: `core-capability-index.md`

This means an agent no longer needs to read the large generalized guides first.
The optimized path is now:

`fast start -> one route doc -> one boundary/module guide -> source module`

## 4. Covered Route Families

The route layer is now split by one main dispatch purpose per file:

- shared contracts and metadata: `mcp-route-shared-contracts.md`
- shared capability modules and SPI-first modules: `mcp-route-shared-capability-modules.md`
- non-security Spring integrations: `mcp-route-spring-integrations.md`
- Spring Security family: `mcp-route-spring-security.md`
- Vert.x / jOOQ family: `mcp-route-vertx-jooq.md`
- code generator tasks: `mcp-route-code-generator.md`
- code-review-graph tasks: `mcp-route-code-review-graph.md`

This route split is sufficient for current MCP dispatching.
Further splitting would increase lookup overhead faster than it would reduce ambiguity.

## 5. Graph-Backed Gap Check

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

The previously missing high-value document gaps for:

- Spring runtime base,
- OAuth2 token/client flow,
- Spring doc/swagger,
- Spring excel,

have already been filled by:

- `spring-runtime-guide.md`
- `oauth2-token-guide.md`
- `spring-doc-guide.md`
- `spring-excel-guide.md`

Current graph evidence does not show another missing first-order MCP rule that is as critical as the gaps already filled.

## 6. Optional Enhancements Only

The remaining opportunities are optional refinements, not coverage blockers:

- add narrower docs for very specific Spring-side utility modules such as `r2mo-spring-json`, `r2mo-spring-template`, or `r2mo-spring-mybatisplus` if those become frequent user entry points,
- add a dedicated test-support guide if AI agents often need `r2mo-spring-junit5` or `r2mo-vertx-junit5`,
- add a small "module-to-doc lookup table" if MCP consumers need exact module-name dispatch without natural-language routing.

These are second-order improvements.
They are not required for the current framework-level MCP pack to be usable and coherent.

## 7. Not Recommended To Split Further

The following document families should not be split further unless new evidence shows repeated retrieval failure:

- `ai-agent-fast-start.md`
- `mcp-shortest-path.md`
- `distilled-capability-cards.md`
- the current `mcp-route-*.md` files
- `core-capability-index.md`

Why:

- they already serve one retrieval responsibility each,
- their main value is compression,
- and further splitting would increase first-hop cost for AI agents.

## 8. Current Completeness Judgment

Current judgment: framework-level MCP coverage is sufficient and already strong for AI-agent consumption.

More precise judgment:

- core rule checklist coverage: sufficient
- shortest-path routing coverage: sufficient
- SRP alignment of the current document set: sufficient
- remaining missing items: optional, low priority, and frequency-dependent

The framework-level MCP pack is not "finished forever", but it is already complete enough to support practical AI-agent retrieval with low token cost and short routing depth.
