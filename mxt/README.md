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

- Final AI Agent Entry: [`mxt-r2mo-ai-agent-guide.md`](./mxt-r2mo-ai-agent-guide.md)
- Framework Overview: [`framework-map.md`](./framework-map.md)
- Abstraction Escalation Rules: [`abstraction-rules.md`](./abstraction-rules.md)
- Extension Points Inventory: [`extension-points.md`](./extension-points.md)
- Spring Layer Map: [`spring-layer-map.md`](./spring-layer-map.md)
- `spec` Boundary: [`spec-boundary.md`](./spec-boundary.md)
- Dual-Side Development: [`dual-side-development.md`](./dual-side-development.md)
- Code Review Graph Analysis: [`code-review-graph-r2mo-analysis.md`](./code-review-graph-r2mo-analysis.md)
- Code Review Graph Usage: [`code-review-graph-usage.md`](./code-review-graph-usage.md)
- MXT-R2MO MCP Rules: [`mxt-r2mo-mcp-rules.md`](./mxt-r2mo-mcp-rules.md)
- Framework Trigger Matrix: [`framework-trigger-matrix.md`](./framework-trigger-matrix.md)
- Search Hints: [`search-hints.md`](./search-hints.md)
- Evolution Rules: [`evolution-rules.md`](./evolution-rules.md)

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

1. First read `mxt-r2mo-ai-agent-guide.md` as the main entry for downstream AI agents.
2. Then read `framework-map.md` to understand the big picture.
3. Read `spec-boundary.md`, `spring-layer-map.md`, and `dual-side-development.md` to lock down ownership and side boundaries.
4. For requirement decisions, see `abstraction-rules.md` and `framework-trigger-matrix.md`.
5. For implementation narrowing, see `extension-points.md`, `search-hints.md`, and the graph documents.
6. For long-term maintenance after upgrades, see `evolution-rules.md`.

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
