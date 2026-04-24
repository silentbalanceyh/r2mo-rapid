# MXT-R2MO AI Agent Guide (Final)

> Final generalized guide for AI agents that read `r2mo-rapid` through `mxt-r2mo`.
> This guide is for **all downstream projects** that depend on the framework, not only one current app.

## 1. What This Repository Is

`r2mo-rapid` is a **framework repository**, not a business application.
Agents should read it as a shared capability base that contains:

- shared abstractions,
- shared contracts and metadata,
- Spring-side runtime integration,
- Vert.x-side runtime integration,
- default bootstrap assembly,
- SPI-based provider selection.

When connected through `mxt-r2mo`, the effective reading surface is usually:

- `r2mo-rapid/` for framework runtime and abstractions,
- `r2mo-spec/` for contracts / OpenAPI / marker / metadata,
- `r2mo-ui/` only when the task is really frontend-scaffold related.

## 2. Who Should Use This Guide

Use this guide when the current task comes from:

- an **R2MO-first Spring project**,
- a **Zero-first project** that still consumes shared R2MO contracts or abstractions,
- a framework contributor,
- or an AI agent that only knows vague business wording but must find the correct framework code.

Especially for **Zero-first** projects, do not assume `mxt-r2mo` is irrelevant.
Those projects can still depend on shared pieces such as:

- `r2mo-spec`,
- `r2mo-dbe`,
- `r2mo-io`,
- `r2mo-jce`,
- `r2mo-jaas`,
- and other container-neutral contracts.

## 3. When To Enter `r2mo-rapid` From a Downstream Project

Enter this framework repo when one or more of these is true:

1. The requirement is reusable across more than one project.
2. The task smells like shared contract, shared abstraction, or framework default behavior.
3. The requirement mentions framework nouns such as `DBE`, `SPI`, `claim`, `token`, `schema`, `marker`, `HFS`, `RFS`, `HED`, `boot`, `security`, `generator`.
4. The downstream project behavior appears to be controlled by framework bootstrap, provider selection, or shared runtime contract.
5. You need to answer “where should this fix live?” rather than only “how do I patch this app locally?”.

Stay in the business project when the task is only:

- domain workflow assembly,
- project-private orchestration,
- tenant/customer-specific rules,
- or plain usage of already-existing framework capability.

## 4. Mandatory Reading Order

Use this default order unless the user gives an exact file path.

```text
1. Normalize the requirement into a framework capability family.
2. Read this file first.
3. Read relevant `mxt/` docs.
4. Read root `pom.xml` and target module `pom.xml`.
5. Use code-review-graph to narrow the module family / community / hotspot.
6. Open the smallest relevant source set.
7. If the question is contract-oriented, confirm again in `r2mo-spec`.
8. Answer in ownership order, not in file-open order.
```

## 5. Primary Ownership Model

### 5.1 Shared foundation and contracts

| Family | Primary role | Enter first when |
|---|---|---|
| `r2mo-ams` | agreed metadata / shared framework vocabulary | metadata, shared structural semantics, common foundation |
| `r2mo-spec` | contract / OpenAPI / marker / schema / error model | schema, marker, metadata, error code, OpenAPI, documentation protocol |
| `r2mo-dbe` | query / CRUD / database abstraction | criteria, pager, sorter, projection, generic query behavior |
| `r2mo-io` | storage / transfer abstraction | upload, download, storage, remote transfer, provider mismatch |
| `r2mo-jce` | crypto / sign / verify / license-style primitives | key, signature, verify, encrypt, decrypt, activation/license-like cryptography |
| `r2mo-jaas` | auth primitives / user cache / claim base | user claim, token base semantics, session primitives, cache-backed auth base |

### 5.2 Container-specific runtime families

| Family | Primary role | Enter first when |
|---|---|---|
| `r2mo-spring` | Spring runtime base | MVC, Bean lifecycle, exception handling, Web/runtime integration |
| `r2mo-spring-security` + `r2mo-spring-security-*` | Spring auth/security plugin family | jwt, oauth2, ldap, captcha, login modes, token/runtime filters |
| `r2mo-spring-*` | Spring-side integrations | JSON, cache, docs, email, sms, template, excel, MyBatis Plus Spring landing |
| `r2mo-vertx` + `r2mo-vertx-jooq*` | Vert.x runtime and async DB bridge | async DB context, jOOQ runtime, generator/runtime bridge |

### 5.3 Bootstrap assembly families

| Family | Primary role | Enter first when |
|---|---|---|
| `r2mo-boot-spring` | Spring bootstrap abstraction | shared Spring startup assembly / dependency baseline |
| `r2mo-boot-spring-default` | default Spring bundle | fastest default Spring onboarding behavior |
| `r2mo-boot-vertx` | Vert.x bootstrap assembly | Vert.x startup assembly and included implementation path |

## 6. Trigger Normalization Rules

Before opening source code, normalize vague requirement words into one or more capability families.

| Trigger words | Normalize to | Read first |
|---|---|---|
| login, auth, session, jwt, oauth2, ldap, captcha, token, principal, claim | security/auth | `spring-layer-map.md`, `framework-trigger-matrix.md`, `mxt-r2mo-mcp-rules.md` |
| license, activation, sign, verify, key, rsa, ecc, ed25519, sm2, sm4, encrypt, decrypt | license/crypto | `framework-trigger-matrix.md`, `code-review-graph-r2mo-analysis.md` |
| criteria, pager, sorter, projection, query, generator, sql, jooq, mybatisplus | DBE/query/generator | `framework-map.md`, `dual-side-development.md`, `search-hints.md` |
| upload, download, file, storage, transfer, hstore, htransfer, chunk, resume, complete, cancel | IO/transfer | `framework-map.md`, `extension-points.md`, `search-hints.md` |
| cache, spi, spid, provider, extension point | cache/SPI/provider selection | `extension-points.md`, `mxt-r2mo-mcp-rules.md`, `search-hints.md` |
| marker, metadata, schema, contract, openapi, error code | spec/contract | `spec-boundary.md`, `search-hints.md` |
| bean, mvc, filter, interceptor, autoconfiguration | Spring runtime | `spring-layer-map.md`, `dual-side-development.md` |
| vertx, async, jooq runtime, jdbc bridge | Vert.x runtime | `dual-side-development.md`, `search-hints.md` |
| boot, default boot, startup bundle, assembly | bootstrap assembly | `framework-map.md`, `abstraction-rules.md` |

If a task matches multiple rows, read in this order:

```text
shared abstraction -> container/runtime landing -> bootstrap/default assembly
```

## 7. Graph-Guided Reading Rules

Use code-review-graph as a **narrowing tool**, not as final truth.

### 7.1 Graph use is mandatory when

- the requirement is framework-level,
- ownership is unclear,
- multiple modules may be involved,
- the user uses vague words,
- or the behavior is likely hidden behind helper chains / SPI.

### 7.2 Graph use is optional when

- the exact file path is already known,
- the exact symbol is unique and confirmed,
- or the task is only editorial documentation work.

### 7.3 How to interpret graph output

Prefer these signals in order:

1. module ownership from `pom.xml`,
2. graph community / capability family,
3. `CALLS` for execution path,
4. `IMPORTS_FROM` for dependency context,
5. `INHERITS` only as a secondary clue.

### 7.4 Repo-local graph command

Prefer the repository wrapper:

```bash
bin/mxt-r2mo-graph status
bin/mxt-r2mo-graph build
bin/mxt-r2mo-graph update
```

Do not run parallel graph commands against the same repo database.

## 8. Default Execution Paths for Agents

### 8.1 “How does this framework feature work?”

```text
trigger normalization -> mxt docs -> module ownership -> graph narrowing -> critical source path -> summarized logic
```

### 8.2 “Where should this fix land?”

```text
business project vs framework check -> shared family vs container family -> source confirmation -> landing recommendation
```

### 8.3 “I only know a business phrase”

```text
business phrase -> framework family -> minimal module set -> exact source symbols
```

## 9. Rules for Zero-First Consumers

When the current application is Zero-first, use these extra rules:

1. Do not jump into Spring modules unless the trigger is clearly Spring-specific.
2. First ask whether the requirement is actually about shared contract or shared abstraction.
3. Prefer `r2mo-spec`, `r2mo-dbe`, `r2mo-io`, `r2mo-jce`, `r2mo-jaas`, and `r2mo-ams` before assuming container ownership.
4. Only enter `r2mo-spring*` when the requirement explicitly depends on Spring runtime mechanisms.

This prevents Zero-side agents from misreading `r2mo-rapid` as a Spring-only repository.

## 10. Rules for R2MO-First Spring Consumers

When the current application is R2MO-first / Spring-first:

1. Still check whether the change belongs in shared abstraction before editing Spring modules.
2. Read `r2mo-spring-security` together with the specific plugin module; do not read only the plugin.
3. Treat `r2mo-boot-spring-default` as default assembly, not as proof that all behavior belongs there.
4. Keep `r2mo-spec` contract-only even if the current app is fully Spring-based.

## 11. Anti-Patterns

Avoid these common mistakes:

1. Reading the framework as if it were a business monolith.
2. Randomly opening many Java files before reading `pom.xml` and `mxt/` docs.
3. Starting from business words without normalizing them into framework families.
4. Placing Spring/Vert.x runtime logic into `r2mo-spec`.
5. Placing project-private semantics into shared abstractions.
6. Assuming graph communities are authoritative architecture.
7. Assuming every downstream project should take the Spring path first.
8. Treating bootstrap modules as business implementation modules.

## 12. Final Default Rule

If uncertain, use this exact path:

```text
this guide -> relevant mxt docs -> root/module pom -> graph family narrowing -> exact source -> answer
```

That is the intended final reading protocol for AI agents consuming `mxt-r2mo`.
