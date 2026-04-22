# MXT Document SRP Audit

> Final single-responsibility audit for `mxt/*.md`.
> This file records whether each document keeps one primary purpose that an agent can understand quickly.

## 1. Audit Rule

A document is considered SRP-aligned when it answers one primary question for one main audience.

This audit uses three statuses:

- `Pass`: one clear responsibility
- `Borderline`: one dominant responsibility exists, but the scope is expanding
- `Needs Split`: multiple distinct responsibilities are mixed and should become separate files

`README.md` is treated as a controlled exception because an index/entry document is allowed to summarize the whole set.

## 2. Audit Results

| File | Primary responsibility | Status | Note |
|---|---|---|---|
| `README.md` | entry/index for the MXT set | Borderline | acceptable as an entry doc, but should not absorb specialized guidance |
| `abstraction-rules.md` | framework escalation rules | Pass | focused on abstraction admission and rejection |
| `ai-agent-fast-start.md` | cheapest first-hop routing for AI agents | Pass | one fast-entry responsibility |
| `ams-boundary.md` | `r2mo-ams` boundary and ownership | Pass | one module-boundary responsibility |
| `backend-module-layering-guide.md` | backend module topology and DPA ownership | Pass | layering-only responsibility |
| `backend-dbe-guide.md` | DBE and query-shape guidance | Pass | persistence-only responsibility |
| `backend-validation-and-job-guide.md` | validation, exception, pagination, and job boundaries | Pass | execution-boundary-only responsibility |
| `code-generator-usage.md` | generator usage and customization routing | Pass | focused and evidence-backed |
| `code-review-graph-r2mo-analysis.md` | repository graph findings | Pass | analysis-only responsibility |
| `code-review-graph-usage.md` | graph operation workflow | Pass | usage-only responsibility |
| `core-capability-index.md` | graph-backed capability extraction index | Pass | one capability-index responsibility |
| `distilled-capability-cards.md` | compressed capability lookup cards | Pass | one compressed-lookup responsibility |
| `delivery-email-guide.md` | email delivery ownership | Pass | one delivery-channel responsibility |
| `delivery-sms-guide.md` | SMS delivery ownership | Pass | one delivery-channel responsibility |
| `delivery-weco-guide.md` | WeCom delivery ownership | Pass | one delivery-channel responsibility |
| `document-srp-audit.md` | SRP audit of the MXT set | Pass | governance-only responsibility |
| `dual-side-development.md` | Spring vs Vert.x development boundary | Pass | one architectural decision theme |
| `extension-points.md` | extension and SPI entry points | Pass | focused on extension seams |
| `framework-map.md` | repository architecture map | Pass | architecture-only after runtime-surface extraction |
| `framework-trigger-matrix.md` | trigger-word to capability-family mapping | Pass | routing-only responsibility |
| `frontend-react-admin-guide.md` | React enterprise admin frontend rules | Pass | React-only responsibility |
| `frontend-rust-leptos-guide.md` | Rust / Leptos / Tauri frontend rules | Pass | Rust stack-only responsibility |
| `frontend-admin-design-system.md` | admin design tokens and UI patterns | Pass | design-system-only responsibility |
| `frontend-rust-admin-addendum.md` | Rust admin app-shell and routing addendum | Pass | app-pattern-only responsibility |
| `integration-contract-first-guide.md` | contract-first integration modeling | Pass | contract-only responsibility |
| `integration-frontend-backend-handshake.md` | frontend/backend integration handshake | Pass | cross-layer execution-only responsibility |
| `integration-runtime-contract-guide.md` | runtime configuration and secret contract | Pass | runtime-only responsibility |
| `io-boundary.md` | `r2mo-io` boundary and ownership | Pass | one module-boundary responsibility |
| `jaas-boundary.md` | `r2mo-jaas` boundary and ownership | Pass | one module-boundary responsibility |
| `jce-boundary.md` | `r2mo-jce` boundary and ownership | Pass | one module-boundary responsibility |
| `mcp-route-shared-contracts.md` | shared contract and metadata MCP route | Pass | one route, one purpose |
| `mcp-route-shared-capability-modules.md` | shared capability / SPI-first MCP route | Pass | one route, one purpose |
| `mcp-route-spring-integrations.md` | non-security Spring integration MCP route | Pass | one route, one purpose |
| `mcp-route-vertx-jooq.md` | Vert.x / jOOQ MCP route | Pass | one route, one purpose |
| `mcp-route-code-generator.md` | generator MCP regex route | Pass | one route, one purpose |
| `mcp-route-code-review-graph.md` | graph MCP regex route | Pass | one route, one purpose |
| `mcp-route-spring-security.md` | Spring Security MCP regex route | Pass | one route, one purpose |
| `mcp-shortest-path.md` | shortest MCP retrieval path | Pass | one retrieval-optimization responsibility |
| `mcp-token-saving-rules.md` | MCP token-saving heuristics | Pass | one token-optimization responsibility |
| `mxt-change-log-rules.md` | MXT change-log convention | Pass | one governance responsibility |
| `mxt-file-creation-rules.md` | MXT file creation criteria | Pass | one governance responsibility |
| `mxt-r2mo-ai-agent-guide.md` | final generalized agent entry guide | Pass | broad, but still one audience and one job |
| `mxt-r2mo-mcp-rules.md` | MCP reading rules for this repository | Pass | large, but still one operational responsibility |
| `mxt-sync-rules.md` | MXT synchronization order after framework change | Pass | one governance responsibility |
| `mxt-upgrade-verification.md` | MXT verification checklist after upgrades | Pass | one verification responsibility |
| `project-rule-awareness.md` | handling project-local rule files | Pass | one execution concern |
| `search-hints.md` | search/navigation hints | Pass | navigation-only responsibility |
| `runtime-configuration-surface.md` | runtime env/application interpretation | Pass | runtime-surface-only responsibility |
| `spi-implementation-boundary.md` | shared/native/Spring SPI placement boundary | Pass | one boundary question |
| `spec-boundary.md` | `r2mo-spec` boundary rules | Pass | one boundary question |
| `spring-layer-map.md` | Spring-side layer ownership | Pass | Spring-only scope |
| `spring-cache-guide.md` | `r2mo-spring-cache` ownership | Pass | one module guide responsibility |
| `spring-security-mcp-guide.md` | Spring Security specialized reading path | Pass | one subsystem guide |

## 3. Immediate Fix Applied

The original MCP regex guidance was previously grouped into one file.
It is now split into three route-specific files:

- `mcp-route-code-review-graph.md`
- `mcp-route-spring-security.md`
- `mcp-route-code-generator.md`

That change makes the MCP routing layer SRP-aligned.

The current route coverage has now been extended without undoing that split:

- `mcp-route-shared-contracts.md`
- `mcp-route-shared-capability-modules.md`
- `mcp-route-spring-integrations.md`
- `mcp-route-vertx-jooq.md`

## 4. Highest-Priority Follow-Up Splits

No urgent split remains in the MCP route layer after the shared-contract, shared-capability, Spring-integration, and Vert.x/jOOQ routes were extracted.

The current optimization direction is no longer route splitting first.
It is now entry compression and retrieval-depth reduction for AI-agent MCP consumption.

The previous governance broad file `evolution-rules.md` has been split into file-creation, sync, verification, and change-log rules.

## 5. Final Test For New Files

When adding a new `mxt/*.md` file, use this test first:

1. Can the file title be expressed as one question?
2. Does the file serve one main audience?
3. If one whole section were removed, would the remaining document still answer the same question?

If the answer to any of these is `no`, the content should probably be split.
