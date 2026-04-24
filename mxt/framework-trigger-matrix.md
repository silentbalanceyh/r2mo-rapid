# Framework Trigger Matrix

> Trigger-word matrix for AI agents reading `r2mo-rapid` through `mxt-r2mo`.
> This file turns vague requirement words into concrete framework-reading routes.

## 1. How To Use This Matrix

When a requirement is phrased in business or generic technical language:

1. find the closest trigger row,
2. map to the capability family,
3. read the listed docs first,
4. then read the listed modules,
5. then inspect exact source symbols.

This prevents agents from over-reading unrelated framework code.

## 2. Trigger Matrix

| Trigger words | Capability family | Read docs first | Read modules first | Typical source symbols |
|---|---|---|---|---|
| login, auth, security, session, jwt, oauth2, ldap, captcha, claim | Security / auth | `framework-map.md`, `spring-layer-map.md`, `mxt-r2mo-mcp-rules.md`, `code-review-graph-r2mo-analysis.md` | `r2mo-jaas`, `r2mo-spring-security`, `r2mo-spring-security-*` | `SecurityWebConfiguration`, `UserAuthCache`, token/cache helpers |
| license, activation, sign, verify, encrypt, decrypt, key, fingerprint, rsa, sm2, ecc, ed25519 | License / crypto | `code-review-graph-r2mo-analysis.md`, `mxt-r2mo-mcp-rules.md` | `r2mo-jce` | `LicenseService`, `LicenseIo`, `PreActiveService`, `HED`, `JceProvider` |
| criteria, pager, sorter, projection, query, dbe, sql, jooq, mybatisplus, generator | DBE / query / generator | `framework-map.md`, `dual-side-development.md`, `search-hints.md`, `code-review-graph-r2mo-analysis.md` | `r2mo-dbe*`, `r2mo-vertx-jooq*`, `r2mo-dbe-mybatisplus` | `DBE`, `QQuery`, generator/converter classes |
| io, file, upload, download, storage, transfer, hstore, htransfer | IO / transfer | `framework-map.md`, `extension-points.md`, `search-hints.md`, `code-review-graph-r2mo-analysis.md` | `r2mo-io`, `r2mo-io-local` | `HStore`, `HTransfer`, `FactoryIoCommon` |
| cache, provider, spi, spid, extension point, implementation selection | Cache / SPI | `extension-points.md`, `search-hints.md`, `mxt-r2mo-mcp-rules.md` | `r2mo-spring-cache`, `r2mo-spring-security`, `r2mo-io`, `r2mo-jaas` | `SPI.findOne*`, `@SPID`, provider/cache implementations |
| doc, openapi, schema, marker, metadata, contract, error code | Spec / contract | `spec-boundary.md`, `search-hints.md` | `r2mo-spec` | `components/schemas`, `openapi`, `marker.md` |
| spring bean, mvc, filter, interceptor, autoconfiguration, webflow | Spring-side runtime | `spring-layer-map.md`, `dual-side-development.md` | `r2mo-spring`, `r2mo-spring-*`, `r2mo-boot-spring*` | configuration classes, exception handlers, filter chains |
| aop, aspect, captcha aspect, around advice, before advice, after advice | Spring AOP / cross-cutting | `spring-aop-guide.md`, `spring-layer-map.md`, `spring-security-mcp-guide.md` | `r2mo-spring`, `r2mo-spring-security`, matching `r2mo-spring-security-*` | `CaptchaValidationAspect`, `CaptchaOn`, Spring AOP configuration |
| vertx, async, event loop, jooq runtime, jdbc bridge | Vert.x-side runtime | `framework-map.md`, `dual-side-development.md`, `search-hints.md` | `r2mo-vertx`, `r2mo-vertx-jooq*`, `r2mo-boot-vertx` | `AsyncDBContext`, Vert.x DB classes |
| boot, default boot, startup bundle, assembly | Bootstrap assembly | `framework-map.md`, `abstraction-rules.md` | `r2mo-boot-spring`, `r2mo-boot-spring-default`, `r2mo-boot-vertx` | boot `pom.xml`, assembly/config chain |

## 2.1 Preferred MCP Route Overrides

When peer-side dispatch is driven by user wording, prefer these route files before falling back to the generic matrix row:

| Trigger focus | Preferred route |
|---|---|
| `r2mo-ams`, `r2mo-spec`, metadata, marker, schema, error code | `mcp-route-shared-contracts.md` |
| `r2mo-dbe`, `r2mo-io`, `r2mo-jaas`, `r2mo-jce`, SPI/provider wording | `mcp-route-shared-capability-modules.md` |
| non-security `r2mo-spring-*` or `r2mo-xync-*` integrations such as cache, email, sms, weco, doc, excel | `mcp-route-spring-integrations.md` |
| `r2mo-vertx*`, async DB bridge, `r2mo-vertx-jooq-*`, Vert.x-side jOOQ generation/runtime | `mcp-route-vertx-jooq.md` |
| graph-first ownership/hotspot/impact wording | `mcp-route-code-review-graph.md` |
| Spring Security runtime/plugin wording | `mcp-route-spring-security.md` |
| generated code / processor / template wording | `mcp-route-code-generator.md` |

## 3. Escalation Rules

### Rule 1 — One trigger row is enough to start, but not enough to finish

A trigger row is only a narrowing device.
After narrowing, confirm ownership in:

- root `pom.xml`,
- target module `pom.xml`,
- actual source code.

### Rule 2 — Multiple trigger rows mean mixed capability

If a requirement matches multiple rows, read in this order:

1. shared abstraction family,
2. container-specific family,
3. final source path.

Example:

- `oauth2 token cache` → security/auth + cache/SPI
- `encrypted license upload` → license/crypto + IO/transfer
- `dynamic query in vertx` → DBE/query + Vert.x runtime
- `shared marker referenced by aspect` → `r2mo-spec` for meaning, then runtime repo for execution

### Rule 3 — If a requirement smells shared, start shared

When both shared and container-specific interpretations are possible, start from:

- `r2mo-ams`
- `r2mo-spec`
- `r2mo-dbe`
- `r2mo-io`
- `r2mo-jaas`
- `r2mo-jce`

Then move down into Spring or Vert.x.

## 4. Companion Docs

This matrix is intended to be used together with:

- `code-review-graph-r2mo-analysis.md`
- `mxt-r2mo-mcp-rules.md`
- `search-hints.md`
- the matching `mcp-route-*.md` file when the request already has a clear wording pattern
- `../../zero-ecotope/mxt/biological-network-overview.md` when the question spans Momo, Spec, R2MO, and Zero

## 5. Direct Deep Retrieval Rule

If the trigger already places the task inside `r2mo-rapid` and the unresolved part is structural, direct `code-review-graph` lookup is valid before opening more broad route docs.

Examples:

- locate callers of `CaptchaValidationAspect`
- confirm whether a symbol belongs to shared capability or Spring integration
- inspect change impact inside one Spring module family
