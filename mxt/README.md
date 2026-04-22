# R2MO Rapid / MXT Guide

> **MXT is an AI-first knowledge pack** — a structured knowledge base designed for AI Agents to rapidly understand and navigate the R2MO framework.

This directory serves two audiences:

1. Developers who need to quickly determine **whether a requirement belongs in the framework abstraction layer or should remain in a business project**.
2. People who need to locate boundaries between **boot / spring / spec / SPI / dual-container** in `r2mo-rapid`.

## 1. Understanding the Repository in One Sentence

`r2mo-rapid` is not a business system. It is a **shared abstraction, specification, implementation, and bootstrap assembly layer for Spring / Vert.x dual-container environments**.

The README clearly states the core building blocks:

- `Cc`: Component cache, preventing duplicate component creation.
- `Fn`: Functional wrapper that primarily handles `Checked` exceptions with lambda cooperation.
- `DBE`: Database access abstraction.
- `HFS / RFS`: File and remote transmission abstraction.
- `HED`: Encryption and decryption tool abstraction.
- `SPI`: Unified extension entry point.
- Dual-container model: `Spring Boot` coexists with `Vert.x`, though some implementations are mutually exclusive.

## 2. What to Read First

- Fastest AI Entry: [`ai-agent-fast-start.md`](./ai-agent-fast-start.md)
- MCP Shortest Path: [`mcp-shortest-path.md`](./mcp-shortest-path.md)
- Distilled Capability Cards: [`distilled-capability-cards.md`](./distilled-capability-cards.md)
- MCP Token-Saving Rules: [`mcp-token-saving-rules.md`](./mcp-token-saving-rules.md)
- Final AI Agent Entry: [`mxt-r2mo-ai-agent-guide.md`](./mxt-r2mo-ai-agent-guide.md)
- Core Capability Index: [`core-capability-index.md`](./core-capability-index.md)
- AMS Boundary: [`ams-boundary.md`](./ams-boundary.md)
- Framework Overview: [`framework-map.md`](./framework-map.md)
- Abstraction Escalation Rules: [`abstraction-rules.md`](./abstraction-rules.md)
- SPI Implementation Boundary: [`spi-implementation-boundary.md`](./spi-implementation-boundary.md)
- Extension Points Inventory: [`extension-points.md`](./extension-points.md)
- IO Boundary: [`io-boundary.md`](./io-boundary.md)
- JAAS Boundary: [`jaas-boundary.md`](./jaas-boundary.md)
- JCE Boundary: [`jce-boundary.md`](./jce-boundary.md)
- Spring Layer Map: [`spring-layer-map.md`](./spring-layer-map.md)
- Spring Cache Guide: [`spring-cache-guide.md`](./spring-cache-guide.md)
- `spec` Boundary: [`spec-boundary.md`](./spec-boundary.md)
- Dual-Side Development: [`dual-side-development.md`](./dual-side-development.md)
- Delivery Email Guide: [`delivery-email-guide.md`](./delivery-email-guide.md)
- Delivery SMS Guide: [`delivery-sms-guide.md`](./delivery-sms-guide.md)
- Delivery WeCo Guide: [`delivery-weco-guide.md`](./delivery-weco-guide.md)
- Code Review Graph Analysis: [`code-review-graph-r2mo-analysis.md`](./code-review-graph-r2mo-analysis.md)
- Code Review Graph Usage: [`code-review-graph-usage.md`](./code-review-graph-usage.md)
- MXT-R2MO MCP Rules: [`mxt-r2mo-mcp-rules.md`](./mxt-r2mo-mcp-rules.md)
- MCP Route / Shared Contracts: [`mcp-route-shared-contracts.md`](./mcp-route-shared-contracts.md)
- MCP Route / Shared Capability Modules: [`mcp-route-shared-capability-modules.md`](./mcp-route-shared-capability-modules.md)
- MCP Route / Spring Integrations: [`mcp-route-spring-integrations.md`](./mcp-route-spring-integrations.md)
- MCP Route / Vert.x / jOOQ: [`mcp-route-vertx-jooq.md`](./mcp-route-vertx-jooq.md)
- MCP Route / Code Review Graph: [`mcp-route-code-review-graph.md`](./mcp-route-code-review-graph.md)
- MCP Route / Spring Security: [`mcp-route-spring-security.md`](./mcp-route-spring-security.md)
- MCP Route / Code Generator: [`mcp-route-code-generator.md`](./mcp-route-code-generator.md)
- Spring Security MCP Guide: [`spring-security-mcp-guide.md`](./spring-security-mcp-guide.md)
- Code Generator Usage: [`code-generator-usage.md`](./code-generator-usage.md)
- Framework Trigger Matrix: [`framework-trigger-matrix.md`](./framework-trigger-matrix.md)
- Frontend React Admin Guide: [`frontend-react-admin-guide.md`](./frontend-react-admin-guide.md)
- Frontend Rust Leptos Guide: [`frontend-rust-leptos-guide.md`](./frontend-rust-leptos-guide.md)
- Frontend Admin Design System: [`frontend-admin-design-system.md`](./frontend-admin-design-system.md)
- Frontend Rust Admin Addendum: [`frontend-rust-admin-addendum.md`](./frontend-rust-admin-addendum.md)
- Backend Module Layering Guide: [`backend-module-layering-guide.md`](./backend-module-layering-guide.md)
- Backend DBE Guide: [`backend-dbe-guide.md`](./backend-dbe-guide.md)
- Backend Validation And Job Guide: [`backend-validation-and-job-guide.md`](./backend-validation-and-job-guide.md)
- Integration Contract-First Guide: [`integration-contract-first-guide.md`](./integration-contract-first-guide.md)
- Integration Frontend Backend Handshake: [`integration-frontend-backend-handshake.md`](./integration-frontend-backend-handshake.md)
- Integration Runtime Contract Guide: [`integration-runtime-contract-guide.md`](./integration-runtime-contract-guide.md)
- Runtime Configuration Surface: [`runtime-configuration-surface.md`](./runtime-configuration-surface.md)
- Search Hints: [`search-hints.md`](./search-hints.md)
- Evolution Rules: [`evolution-rules.md`](./evolution-rules.md)
- MXT Document SRP Audit: [`document-srp-audit.md`](./document-srp-audit.md)

## 3. Core Decision Framework

### Requirements That Should Enter the Framework Layer

Consider escalating to `r2mo-rapid` if any of the following apply:

1. **Repeatedly appears across business projects**, not a single tenant or process-specific case.
2. **Affects both Spring and Vert.x**, or at least needs to retain bilateral compatibility.
3. Needs to be unified into abstractions such as `DBE`, `SPI`, unified exceptions, unified JSON, unified IO.
4. Needs standardized assembly such as `boot` default dependencies, `spring-security-*` pluggable authentication modules.
5. Needs to enter `spec` as shared contracts at the OpenAPI / schema / error code level.

### Requirements That Should Remain in Business Projects

Do not pollute the framework if any of the following apply:

1. Serves only a single business domain such as finance, reports, approvals, or customer-specific customizations.
2. Rules strongly depend on organizations, tenants, regions, or processes and lack generalizability.
3. Are merely combinations of existing framework capabilities without requiring new abstractions.
4. Naming, fields, or interface conventions clearly carry business semantics.
5. Used only once within a project with insufficient reuse scope.

## 4. Repository Module Backbone

From the root `pom.xml`, four main layers are visible:

1. **Unified Abstraction Interface Layer**
   - `r2mo-ams`
   - `r2mo-dbe`
   - `r2mo-jce`
   - `r2mo-jaas`
   - `r2mo-io`
   - `r2mo-spec`
2. **Concrete Implementation Layer**
   - `r2mo-dbe-mybatisplus` / `r2mo-dbe-jooq`
   - `r2mo-typed-hutool` / `r2mo-typed-vertx`
   - `r2mo-io-local`
3. **Container Core Layer**
   - `r2mo-spring`
   - `r2mo-spring-security`
   - `r2mo-vertx`
4. **Bootstrap Assembly Layer**
   - `r2mo-boot-spring`
   - `r2mo-boot-spring-default`
   - `r2mo-boot-vertx`

## 5. Recommended Reading Order

1. First read `ai-agent-fast-start.md` when the goal is shortest correct routing.
2. Then read `mcp-shortest-path.md` when MCP token cost is the main constraint.
3. Read `distilled-capability-cards.md` or `core-capability-index.md` only if the trigger is still vague.
4. Read one matching `mcp-route-*.md` file.
5. Read one boundary or guide file for the chosen module family.
6. Open exact source only after ownership is locked.

Use `mxt-r2mo-ai-agent-guide.md` and `README.md` as broader second-line references, not as the default first hop for every request.

For MCP peer-side routing, add this refinement:

- use `ai-agent-fast-start.md` when the agent starts cold and needs the cheapest first decision,
- use `mcp-shortest-path.md` when MCP retrieval depth must stay minimal,
- use `distilled-capability-cards.md` when the agent needs a one-screen capability summary,
- use `mcp-token-saving-rules.md` when the main optimization target is token budget,
- use `ams-boundary.md` when the target is specifically `r2mo-ams` rather than the broader shared-contract route,
- use `spi-implementation-boundary.md` when the task is about SPI placement between shared/native/Spring layers,
- use `io-boundary.md`, `jaas-boundary.md`, and `jce-boundary.md` when the target shared capability family is already known,
- use `spring-cache-guide.md` when the target is specifically `r2mo-spring-cache`,
- use `delivery-email-guide.md`, `delivery-sms-guide.md`, and `delivery-weco-guide.md` when the target is one delivery channel rather than the whole integration family,
- use `mcp-route-shared-contracts.md` for `r2mo-ams` / `r2mo-spec` / schema / marker / metadata / error-code wording,
- use `mcp-route-shared-capability-modules.md` for `r2mo-dbe` / `r2mo-io` / `r2mo-jaas` / `r2mo-jce` / SPI-first wording,
- use `core-capability-index.md` when the task is to extract the framework's main functions quickly from graph-backed capability clusters,
- use `mcp-route-spring-integrations.md` for non-security `r2mo-spring-*` and `r2mo-xync-*` integration modules such as cache, email, sms, weco, doc, and excel,
- use `mcp-route-vertx-jooq.md` for Vert.x-side runtime, jOOQ bridge, and `r2mo-vertx-jooq-*` wording,
- use the `mcp-route-*.md` files before the generic trigger matrix when the peer is dispatching by user wording,
- use `spring-security-mcp-guide.md` when the request is clearly Spring Security specific,
- use `code-generator-usage.md` when the request is clearly about generated code or generator customization.

## 6. Fast Path Selection for Agents

### Choose the Spring path when

- The requirement depends on Spring Bean lifecycle, AutoConfiguration, MVC, WebMVC, Spring Security, or container-managed filters.
- The change belongs to `r2mo-spring-*`, `r2mo-spring-security-*`, or `r2mo-boot-spring*` modules.
- The extension is primarily synchronous request/response behavior or Spring-side bootstrap assembly.

### Choose the Vert.x path when

- The requirement depends on async runtime flow, Vert.x execution style, or jOOQ runtime bridging.
- The change belongs to `r2mo-vertx*` modules.
- The extension is primarily async DB execution, generator/runtime bridging, or Vert.x-specific infrastructure.

### Stop and abstract first when

- The requirement touches both Spring and Vert.x terminology.
- The change smells like shared query shape, shared claim structure, shared IO contract, shared error contract, or shared schema.
- The same behavior would otherwise be implemented twice.

## 7. R2MO and the Dual-Container Reality

R2MO is **Spring-dominant in practice** — the majority of real-world projects use the Spring side, and most plugin modules (`spring-security-*`, `spring-cache`, `spring-doc`, etc.) exist only in the Spring family. The Vert.x side is present but thinner.

This does not mean you should ignore dual-container principles. It means:

- **Abstractions in `r2mo-dbe`, `r2mo-io`, `r2mo-jaas`, `r2mo-jce`, and `r2mo-spec` must remain container-neutral** — even though most current consumers are Spring-side.
- When you write something in the shared abstraction layer, mentally test it against both sides: "would this work in Vert.x too?" If the answer is no, it belongs in `r2mo-spring*`, not in the shared layer.
- The Vert.x side (`r2mo-boot-vertx`, `r2mo-vertx-jooq*`) is an equally valid first-class citizen for projects that need it.

### Environment Variables Are Architecture, Not Incidental Deployment Config

In R2MO, environment variables are part of the **framework runtime architecture** rather than an afterthought of deployment.

Why this matters:

- In **multi-tenant** setups, environment variables often choose tenant-facing runtime context before business services execute.
- In **multi-language** setups, they can define default locale, language family, and resource-loading behavior.
- In **multi-style** setups, they can control runtime style families, theme-like variants, serialization style, or framework-level presentation mode.
- In **multi-application-mode** setups, they often decide whether the runtime behaves as a full business app, a lighter service mode, a docs/spec mode, or another framework-defined application shape.

Treat these variables as the first control surface of the application model:

- they influence which framework modules are active,
- how bootstrap assembly behaves,
- which shared providers are selected,
- and how the same codebase is interpreted at runtime.

### What Counts as Shared Runtime Contract

The shared runtime contract is not every environment variable in the system. It is the subset whose **name and meaning must remain stable across projects** because framework modules depend on it.

Typical shared runtime-contract categories include:

- active profile and application mode selection,
- tenant and environment selection,
- locale / i18n defaults,
- framework-level style or presentation mode selectors,
- storage / DB / security provider selectors,
- documentation and API exposure mode selectors.

By contrast, project-private business toggles are not shared runtime contract and should stay out of framework-level assumptions.

- Extracting business rules into framework capabilities.
- Accidentally writing Spring-specific implementations into `spec`.
- Wrapping logic used only in one project as `SPI`.
- Writing business semantics in the `boot` layer instead of doing assembly.
- Forgetting the dual-container model, causing abstractions to be inherently tied to Spring.
