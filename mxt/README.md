# R2MO Rapid / MXT Guide

> **MXT is an AI-first knowledge pack** — a structured knowledge base designed for AI Agents to rapidly understand and navigate the R2MO framework.

`r2mo-rapid` is not a business system. It is a **shared abstraction, specification, implementation, and bootstrap assembly layer for Spring / Vert.x dual-container environments**.

## 1. What to Read First

Start with the smallest valid entry:

- Fastest AI Entry: [`ai-agent-fast-start.md`](./ai-agent-fast-start.md)
- MCP Shortest Path: [`mcp-shortest-path.md`](./mcp-shortest-path.md)
- Cross-Repo Biological Network: [`../../zero-ecotope/mxt/biological-network-overview.md`](../../zero-ecotope/mxt/biological-network-overview.md)
- Pairwise Matrix: [`../../zero-ecotope/mxt/biological-network-pairwise-matrix.md`](../../zero-ecotope/mxt/biological-network-pairwise-matrix.md)
- MCP Trigger Matrix: [`mcp-trigger-matrix.md`](./mcp-trigger-matrix.md)
- Distilled Capability Cards: [`distilled-capability-cards.md`](./distilled-capability-cards.md)
- MCP Token-Saving Rules: [`mcp-token-saving-rules.md`](./mcp-token-saving-rules.md)
- MXT Coverage Report: [`mxt-coverage-report.md`](./mxt-coverage-report.md)

Cross-repository rule:

- solve inside `r2mo-rapid` alone when possible
- use one partner repository when needed
- treat any further repository as optional escalation only

## 2. MCP Route Files

- [`mcp-route-shared-contracts.md`](./mcp-route-shared-contracts.md) — `r2mo-ams`, `r2mo-spec`, schema, marker, metadata
- [`mcp-route-shared-capability-modules.md`](./mcp-route-shared-capability-modules.md) — `r2mo-dbe`, `r2mo-io`, `r2mo-jaas`, `r2mo-jce`, `r2mo-typed-*`, SPI/provider
- [`mcp-route-spring-integrations.md`](./mcp-route-spring-integrations.md) — non-security `r2mo-spring-*`, `r2mo-xync-*`
- [`mcp-route-spring-security.md`](./mcp-route-spring-security.md) — `r2mo-spring-security`, login modes, auth plugins
- [`mcp-route-vertx-jooq.md`](./mcp-route-vertx-jooq.md) — `r2mo-vertx`, `r2mo-vertx-jooq*`, `r2mo-dbe-jooq`
- [`mcp-route-code-generator.md`](./mcp-route-code-generator.md) — generator and processor
- [`mcp-route-code-review-graph.md`](./mcp-route-code-review-graph.md) — graph-first analysis
- [`mcp-route-zero-ecotope-handshake.md`](./mcp-route-zero-ecotope-handshake.md) — cross-pack routing into Zero Ecotope exmodule rules from R2MO MCP clients

## 3. Module Boundary and Guide Documents

### Shared foundation

- [`ams-boundary.md`](./ams-boundary.md) — `r2mo-ams`
- [`spec-boundary.md`](./spec-boundary.md) — `r2mo-spec`
- [`abstraction-rules.md`](./abstraction-rules.md) — framework escalation rules
- [`extension-points.md`](./extension-points.md) — extension and SPI entry points
- [`spi-implementation-boundary.md`](./spi-implementation-boundary.md) — shared/native/Spring SPI placement

### Shared capability

- [`io-boundary.md`](./io-boundary.md) — `r2mo-io`
- [`hfs-hstore-usage.md`](./hfs-hstore-usage.md) — MDC rules for `HFS`, `HStore`, `RFS`, transfer sessions, range download, and storage providers
- [`jaas-boundary.md`](./jaas-boundary.md) — `r2mo-jaas`
- [`jce-boundary.md`](./jce-boundary.md) — `r2mo-jce`
- [`typed-implementation-boundary.md`](./typed-implementation-boundary.md) — `r2mo-typed-hutool` vs `r2mo-typed-vertx`
- [`dbe-implementation-boundary.md`](./dbe-implementation-boundary.md) — `r2mo-dbe-mybatisplus` vs `r2mo-dbe-jooq`
- [`backend-dbe-guide.md`](./backend-dbe-guide.md) — DBE and query-shape

### Spring layer

- [`spring-layer-map.md`](./spring-layer-map.md) — Spring-side layer ownership
- [`spring-runtime-guide.md`](./spring-runtime-guide.md) — `r2mo-spring` runtime base
- [`spring-aop-guide.md`](./spring-aop-guide.md) — Spring AOP / AspectJ ownership between `r2mo-spring` and `r2mo-spring-security`
- [`spring-cache-guide.md`](./spring-cache-guide.md) — `r2mo-spring-cache`
- [`spring-doc-guide.md`](./spring-doc-guide.md) — `r2mo-spring-doc`
- [`spring-excel-guide.md`](./spring-excel-guide.md) — `r2mo-spring-excel`
- [`spring-adapter-guides.md`](./spring-adapter-guides.md) — `r2mo-spring-json`, `r2mo-spring-template`, `r2mo-spring-mybatisplus`
- [`spring-delivery-boundary.md`](./spring-delivery-boundary.md) — security-email/sms/weco vs spring-email/sms/weco vs xync-email/sms/weco
- [`spring-security-mcp-guide.md`](./spring-security-mcp-guide.md) — Spring Security subsystem
- [`oauth2-token-guide.md`](./oauth2-token-guide.md) — OAuth2 token and client flow

### Delivery foundation

- [`delivery-email-guide.md`](./delivery-email-guide.md) — `r2mo-xync-email`
- [`delivery-sms-guide.md`](./delivery-sms-guide.md) — `r2mo-xync-sms`
- [`delivery-weco-guide.md`](./delivery-weco-guide.md) — `r2mo-xync-weco`

### Boot and assembly

- [`boot-assembly-guide.md`](./boot-assembly-guide.md) — `r2mo-boot-spring`, `r2mo-boot-spring-default`, `r2mo-boot-vertx`
- [`dual-side-development.md`](./dual-side-development.md) — Spring vs Vert.x boundary

### Code review graph

- [`code-review-graph-r2mo-analysis.md`](./code-review-graph-r2mo-analysis.md) — repository graph findings
- [`code-review-graph-usage.md`](./code-review-graph-usage.md) — graph operation workflow
- [`../../zero-ecotope/mxt/biological-network-dynamic-lookup.md`](../../zero-ecotope/mxt/biological-network-dynamic-lookup.md) — cross-repo dynamic lookup and direct deep retrieval rules

### Code generator

- [`code-generator-usage.md`](./code-generator-usage.md) — generator usage and customization

## 4. Architecture and Integration Guides

- [`framework-map.md`](./framework-map.md) — repository architecture map and decision framework
- [`core-capability-index.md`](./core-capability-index.md) — graph-backed capability index
- [`framework-trigger-matrix.md`](./framework-trigger-matrix.md) — trigger-word to capability-family mapping
- [`search-hints.md`](./search-hints.md) — search/navigation hints
- [`runtime-configuration-surface.md`](./runtime-configuration-surface.md) — runtime env/application interpretation
- [`backend-module-layering-guide.md`](./backend-module-layering-guide.md) — backend module topology
- [`backend-validation-and-job-guide.md`](./backend-validation-and-job-guide.md) — validation, exception, pagination, job
- [`integration-contract-first-guide.md`](./integration-contract-first-guide.md) — contract-first integration
- [`integration-frontend-backend-handshake.md`](./integration-frontend-backend-handshake.md) — frontend/backend handshake
- [`integration-runtime-contract-guide.md`](./integration-runtime-contract-guide.md) — runtime configuration and secret contract

## 5. Frontend Guides

- [`frontend-react-admin-guide.md`](./frontend-react-admin-guide.md) — React enterprise admin
- [`frontend-rust-leptos-guide.md`](./frontend-rust-leptos-guide.md) — Rust / Leptos / Tauri
- [`frontend-admin-design-system.md`](./frontend-admin-design-system.md) — admin design tokens
- [`frontend-rust-admin-addendum.md`](./frontend-rust-admin-addendum.md) — Rust admin app-shell

## 6. MXT Governance

- [`mxt-r2mo-mcp-rules.md`](./mxt-r2mo-mcp-rules.md) — MCP reading rules for this repository
- [`mxt-r2mo-ai-agent-guide.md`](./mxt-r2mo-ai-agent-guide.md) — generalized agent entry guide
- [`mxt-file-creation-rules.md`](./mxt-file-creation-rules.md) — file creation criteria
- [`mxt-sync-rules.md`](./mxt-sync-rules.md) — synchronization after framework change
- [`mxt-upgrade-verification.md`](./mxt-upgrade-verification.md) — verification after upgrades
- [`mxt-change-log-rules.md`](./mxt-change-log-rules.md) — change-log convention
- [`mxt-coverage-report.md`](./mxt-coverage-report.md) — current MCP coverage judgment
- [`document-srp-audit.md`](./document-srp-audit.md) — SRP audit of the MXT set
- [`project-rule-awareness.md`](./project-rule-awareness.md) — handling project-local rule files
