# MCP Trigger Matrix

> Trigger-word to capability-family mapping for AI agents reading `r2mo-rapid` through MCP.
> This file was extracted from `mxt-r2mo-mcp-rules.md` for SRP alignment.

## 1. Purpose

When a requirement contains the following trigger terms, narrow to the matching graph family first. Then route to the corresponding `mcp-route-*.md` file for detailed reading order.

## 2. Security / Auth Triggers

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
- oauth2client
- ldap
- security
- principal
- claim
- sms login
- email login
- wecom login

Preferred targets:

- communities: `auth-security`, `oauth2-token`, `session-user`
- modules: `r2mo-spring-security`, `r2mo-spring-security-*`, `r2mo-jaas`
- route: `mcp-route-spring-security.md`

Boundary: `r2mo-spring-security-email/sms/weco` = authentication login. `r2mo-spring-email/sms/weco` = notification delivery. See `spring-delivery-boundary.md`.

## 3. License / Crypto Triggers

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
- route: `mcp-route-shared-capability-modules.md`
- docs: `framework-trigger-matrix.md`, `code-review-graph-r2mo-analysis.md`

## 4. DBE / Query / Generator Triggers

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
- modules: `r2mo-dbe*`, `r2mo-vertx-jooq*`, `r2mo-dbe-mybatisplus`, `r2mo-dbe-jooq`
- routes: `mcp-route-shared-capability-modules.md`, `mcp-route-vertx-jooq.md`, `mcp-route-code-generator.md`

Boundary: `r2mo-dbe-jooq` owns the DBE abstraction contract. `r2mo-vertx-jooq*` owns the Vert.x runtime implementation. See `dbe-implementation-boundary.md`.

## 5. IO / Transfer Triggers

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
- hfs
- rfs
- range download
- configurable storage

Preferred targets:

- communities: `operation-transfer`, `common-transfer`
- modules: `r2mo-io`, `r2mo-io-local`
- route: `mcp-route-shared-capability-modules.md`
- rule: `hfs-hstore-usage.md`

Zero handshake:

- If the same task mentions Ambient, Integration, attachment metadata, `ExAttachment`, `ExIo`, or Zero configurable storage, continue to `mcp-route-zero-ecotope-handshake.md` and the matching `mxt-zero` rule.

## 5.1 Zero Ecotope Exmodule Triggers

Trigger words:

- ambient
- activity log
- audit log
- change log
- activity rule
- expr rule
- expression rule
- modulat
- dynamic module
- modular operation
- bag block
- B_BAG
- B_BLOCK
- ExModulat
- ExAttachment
- ExIo

Preferred targets:

- server: `mxt-zero`
- route: `mcp-route-zero-ecotope-handshake.md`
- Zero rules: `ambient-activity-expression-rules.md`, `modulat-dynamic-operation-rules.md`, `attachment-storage-configurable-storage.md`, `io-utility-hfs-hstore-rules.md`

## 6. Cache / SPI / Provider-Selection Triggers

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
- routes: `mcp-route-shared-capability-modules.md`, `mcp-route-spring-integrations.md`

## 7. Typed Implementation Triggers

Trigger words:

- typed-hutool
- typed-vertx
- type implementation
- hutool json
- vertx json

Preferred targets:

- modules: `r2mo-typed-hutool`, `r2mo-typed-vertx`
- route: `mcp-route-shared-capability-modules.md`
- doc: `typed-implementation-boundary.md`

## 8. Boot Assembly Triggers

Trigger words:

- boot-spring
- boot-spring-default
- boot-vertx
- default dependencies
- startup bundle
- bootstrap assembly

Preferred targets:

- modules: `r2mo-boot-spring`, `r2mo-boot-spring-default`, `r2mo-boot-vertx`
- doc: `boot-assembly-guide.md`

## 9. Final Rule

When a trigger matches, use this sequence:

```text
trigger term -> this matrix -> matching mcp-route-*.md -> one guide/boundary doc -> exact source
```

Do not skip the route file. The route file provides the mandatory reading set and execution contract.
