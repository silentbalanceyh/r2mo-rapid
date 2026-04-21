# R2MO Rapid — Code Review Graph Analysis

> This document records an evidence-based repository analysis for `r2mo-rapid` using `code-review-graph`.
> It is intended for projects connecting through `mxt-r2mo`, so AI agents can quickly understand where framework logic lives and which areas are likely to own a requirement before reading large parts of the codebase manually.

## 1. Build Context

Graph build target:

- Repository: `/Users/lang/zero-cloud/app-zero/r2mo-rapid`
- Tool: `code-review-graph 2.3.2`
- Branch: `master`
- Commit at build time: `8efe73d15296`
- Build date (tool status): `2026-04-19T18:43:51`

Build command used:

```bash
code-review-graph build --repo /Users/lang/zero-cloud/app-zero/r2mo-rapid
```

Status command used:

```bash
code-review-graph status --repo /Users/lang/zero-cloud/app-zero/r2mo-rapid
```

## 2. High-Level Graph Statistics

Observed status after build:

- Files: `929`
- Nodes: `5281`
- Edges: `22980`
- Languages: `bash`, `java`

Observed raw build summary:

- Parsed files: `929`
- Raw graph after build: `7260 nodes`, `23349 edges`
- Post-processing: full
- Community detection fallback: file-based (because `igraph` was not available)

This means the repository is large enough that blind file-by-file crawling is expensive. Agents should use graph-guided narrowing first when the question is about ownership, entry points, hotspots, or likely execution paths.

### 2.1 Fresh MCP re-check on 2026-04-21

Fresh `code-review-graph` MCP queries reconfirmed:

- graph stats: `929 files`, `5281 nodes`, `22980 edges`
- languages: `bash`, `java`
- embeddings: `0`
- architecture overview: `40 communities`, `0 cross-community edges`, `0 warnings`

Interpretation:

1. The graph remains large enough that graph-first narrowing is still the correct default.
2. The current graph build is structurally usable for ownership and family detection.
3. The absence of embeddings means semantic search is not currently enriched by vector indexing.

## 3. Graph Schema Surface Actually Produced

The generated graph database at `.code-review-graph/graph.db` contains these primary tables:

- `nodes`
- `edges`
- `communities`
- `community_summaries`
- `flows`
- `flow_memberships`
- `risk_index`
- `metadata`
- full-text tables: `nodes_fts*`

This matters because the graph is not only a parser dump. It already includes:

- symbol nodes,
- relation edges,
- community clustering,
- flow snapshots,
- FTS search,
- simple risk indexing.

For `mxt-r2mo`, this is enough to support repository-orientation rules even when a project has not manually curated a separate architecture map.

## 4. What the Graph Says About Repository Shape

### 4.1 Node and edge composition

Node counts by kind:

- `Function`: `3370`
- `Class`: `982`
- `File`: `929`

Edge counts by kind:

- `CALLS`: `9604`
- `IMPORTS_FROM`: `6454`
- `CONTAINS`: `6331`
- `INHERITS`: `591`

Interpretation:

1. This repository is method-heavy rather than inheritance-heavy.
2. Most agent questions should first be answered through:
   - file/module boundaries,
   - import edges,
   - call edges,
   rather than by searching for huge abstract base classes.
3. Runtime logic is distributed across many smaller methods, so “find the one main framework class” is often the wrong strategy.

### 4.2 Dominant top-level modules by file count

Measured from file nodes:

| Module | File count |
|---|---:|
| `r2mo-ams` | 221 |
| `r2mo-spring-security` | 79 |
| `r2mo-jce` | 58 |
| `r2mo-vertx-jooq-generate` | 48 |
| `r2mo-spring` | 44 |
| `r2mo-spring-security-oauth2` | 38 |
| `r2mo-io-local` | 36 |
| `r2mo-vertx` | 35 |
| `r2mo-vertx-jooq-shared` | 33 |
| `r2mo-dbe-jooq` | 33 |
| `r2mo-dbe-mybatisplus` | 32 |
| `r2mo-xync-weco` | 31 |
| `r2mo-jaas` | 23 |
| `r2mo-vertx-jooq` | 22 |
| `r2mo-io` | 21 |
| `r2mo-spring-cache` | 20 |

Interpretation:

- `r2mo-ams` is the densest foundational module and should be assumed to hold a large amount of shared vocabulary and base contracts.
- The Spring security family is one of the heaviest concrete framework regions, which matches the repo’s practical Spring-dominant usage.
- `r2mo-jce` is a significant shared-core zone, not a small utility corner.
- Vert.x and jOOQ logic is real and non-trivial, but still smaller than the Spring/security side.

## 5. Community Clusters Detected by the Graph

Top detected communities:

| Community | Size | Cohesion | Dominant language | Interpreted purpose |
|---|---:|---:|---|---|
| `util-string` | 1034 | 0.3984 | java | broad shared utility / common core |
| `auth-security` | 283 | 0.2445 | java | auth + security infrastructure |
| `generate-generator` | 282 | 0.2921 | java | generation / generator logic |
| `common-license` | 278 | 0.3251 | java | JCE / license-related logic |
| `jooq-async` | 244 | 0.6166 | java | async jOOQ / Vert.x bridge |
| `operation-transfer` | 222 | 0.2654 | java | local/transfer operations |
| `webflow-handle` | 191 | 0.2915 | java | Spring-side web flow handling |
| `dbe-async` | 178 | 0.4444 | java | Vert.x-side DBE async zone |
| `spi-jooq` | 175 | 0.2368 | java | SPI + jOOQ composition |
| `postgres-converter` | 171 | 0.5357 | java | jOOQ / database conversion |
| `spi-impl` | 168 | 0.2454 | java | implementation-side SPI landing |
| `oauth2-token` | 153 | 0.2126 | java | OAuth2 token handling |
| `common-transfer` | 106 | 0.3926 | java | IO / transfer layer |
| `cache-cache` | 102 | 0.2997 | java | cache abstraction/implementation |
| `session-user` | 98 | 0.3016 | java | JAAS / user-session logic |

Interpretation:

1. The graph confirms that the repo is not just split by Maven modules; it also has cross-module semantic communities.
2. Security, license, generator, jOOQ, transfer, and cache logic each form recognizable reading clusters.
3. When a requirement touches one of these themes, an agent should inspect the whole cluster, not only one same-name module.

### 5.1 Fresh architecture-overview interpretation

Fresh MCP architecture overview reported `40 communities` with `0 cross-community edges` and `0 warnings`.

Interpretation:

1. The current post-processed graph is strongly directory-shaped rather than strongly cross-community-linked.
2. This is still useful for module-family narrowing, but weaker for reasoning about deep architectural coupling.
3. Agents should therefore trust the graph more for clustering and ownership than for cross-community dependency diagnosis.

## 6. Risk Signals From the Graph

Top risk-indexed symbols include:

- `r2mo-dbe-mybatisplus/.../GenProcessorSqlMySQL.java::GenProcessorSqlMySQL`
- `r2mo-jce/.../HEDBase.encrypt`
- `r2mo-jce/.../HEDBase.decrypt`
- `r2mo-spring-security/.../CacheAtSecurityBase.ofToken`
- `r2mo-io-local/.../LocalReader.executeInJarFileSystem`
- `r2mo-jce/.../JceProvider.SecretKeyFactory`
- several OAuth2 token/settings helpers
- several `r2mo-ams` DBE/credential/transfer contract symbols

Observed traits in those top rows:

- high security relevance flags,
- low or unknown test coverage labels,
- non-trivial caller counts for some core symbols.

Interpretation:

- JCE encrypt/decrypt helpers are central and security-sensitive.
- generator and security-cache entry points deserve extra caution when changing defaults.
- `r2mo-ams` contains foundational abstractions that may not look “dangerous” from filenames alone, but graph risk indexing shows they are impact-bearing.

## 7. What This Means for AI Agent Reading Strategy

### 7.1 Do not start from business words only

The graph confirms that the repository is organized around reusable framework capability families, not business domains.
An agent should therefore not begin with vague business words like:

- login
- token
- cache
- query
- upload
- license

Instead, convert the question into one or more framework capability families first, then map to graph-backed module/cluster candidates.

### 7.2 The most reliable narrowing dimensions are

1. **top-level module** from `pom.xml` and file distribution,
2. **community family** from graph clustering,
3. **edge kind**:
   - `IMPORTS_FROM` for ownership/context,
   - `CALLS` for runtime path,
   - `INHERITS` only as a secondary signal.

### 7.3 Practical reading priority by question type

| Question type | Read order |
|---|---|
| “where does this capability belong?” | `mxt/*.md` → root `pom.xml` → graph community/module → source |
| “what actually happens at runtime?” | graph `CALLS`/`flows` → target source methods |
| “is this shared abstraction or single-side implementation?” | `framework-map.md` / `dual-side-development.md` → graph community → module `pom.xml` |
| “what code is risky to change?” | graph `risk_index` → source + local docs |
| “what existing framework logic matches this requirement?” | `mxt` docs → graph communities/symbols → source |

## 8. Evidence-Based Capability Families the Graph Makes Obvious

The graph makes the following families especially visible and therefore suitable as MCP reading triggers:

### 8.1 Security / auth family

Trigger words:

- auth
- security
- login
- session
- captcha
- token
- jwt
- oauth2
- ldap
- authorize

Graph-backed reading targets:

- `auth-security`
- `oauth2-token`
- `session-user`
- heavy modules: `r2mo-spring-security`, `r2mo-spring-security-oauth2`, `r2mo-jaas`

### 8.2 License / cryptography family

Trigger words:

- license
- activation
- sign
- verify
- encrypt
- decrypt
- keypair
- fingerprint
- jce
- SM2 / RSA / ECC / Ed25519

Graph-backed reading targets:

- `common-license`
- heavy module: `r2mo-jce`
- high-risk symbols: `HEDBase.encrypt`, `HEDBase.decrypt`

### 8.3 DBE / query / generator family

Trigger words:

- dbe
- criteria
- pager
- sorter
- projection
- jooq
- mybatisplus
- generate
- sql
- converter

Graph-backed reading targets:

- `generate-generator`
- `jooq-async`
- `dbe-async`
- `spi-jooq`
- `postgres-converter`
- heavy modules: `r2mo-dbe*`, `r2mo-vertx-jooq*`, `r2mo-dbe-mybatisplus`

### 8.4 IO / transfer family

Trigger words:

- io
- storage
- transfer
- upload
- download
- local
- remote
- hstore
- htransfer

Graph-backed reading targets:

- `operation-transfer`
- `common-transfer`
- heavy modules: `r2mo-io`, `r2mo-io-local`

### 8.5 Cache / SPI family

Trigger words:

- cache
- provider
- spi
- spid
- implementation selection
- extension point

Graph-backed reading targets:

- `cache-cache`
- `spi-impl`
- `spi-jooq`
- modules: `r2mo-spring-cache`, `r2mo-spring-security`, `r2mo-io`, `r2mo-jaas`

## 9. Current Limits of the Graph for This Repository

The graph is useful, but the current output has some limitations that agents should understand:

1. `flows` are currently shallow and not very semantically rich for this repo.
2. `igraph` was unavailable, so community detection used a fallback mode.
3. `status` cannot run concurrently with another graph operation against the same database; a second process can hit `database is locked`.
4. Top `CALLS` targets may include common variable or helper names (`Objects`, `log`, `out`) that are less useful than domain-specific targets.
5. During the 2026-04-21 MCP re-check, some advanced MCP graph tools such as hub-node, bridge-node, and knowledge-gap queries failed with repository-resolution errors, while architecture overview and graph stats succeeded.

Therefore:

- use the graph for narrowing and clustering,
- do not treat it as a perfect architecture oracle,
- always confirm final reasoning in source code and `mxt` docs.

## 10. Recommended MCP-Level Usage Pattern

When a project connects to `mxt-r2mo`, a good practical rule is:

```text
1. Read mxt/*.md first for repository semantics and ownership.
2. If the task is framework-level or cross-module, consult code-review-graph output next.
3. Use the graph to choose the smallest relevant capability family and module set.
4. Only then open source files and trace concrete methods.
5. If the graph and source disagree, source wins.
```

This keeps graph usage token-efficient while preserving correctness.

## 11. Companion Document

This analysis is paired with:

- [`mxt-r2mo-mcp-rules.md`](./mxt-r2mo-mcp-rules.md)

That file converts the graph observations into actionable AI-agent reading rules for MCP-connected projects.
