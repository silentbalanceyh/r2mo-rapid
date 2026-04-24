---
description: R2MO-side MCP routing rules for external projects that need Zero Ecotope exmodule knowledge through mxt-zero.
globs:
  - "mxt/**/*"
alwaysApply: false
---

# MCP Route — Zero Ecotope Handshake

Load this rule in `r2mo-rapid` when an external MCP client starts from R2MO but the topic actually belongs to Zero Ecotope exmodules or requires a Zero/R2MO cross-pack answer.

## Route to `mxt-zero` First When

- the topic mentions `zero-exmodule-ambient`, `ambient`, attachment metadata, activity logs, activity rules, or system configuration,
- the topic mentions `zero-exmodule-integration`, configurable document directory, `ExIo`, or Integration `Fs`,
- the topic mentions `zero-exmodule-modulat`, dynamic module, bag/block configuration, or `ExModulat`,
- the topic is about business exmodule behavior rather than R2MO core abstractions.

## Keep `mxt-r2mo` First When

- the topic mentions `HFS`, `HStore`, `RFS`, `HTransfer`, transfer token, chunked upload internals, or storage-provider implementation,
- the topic is about `r2mo-io`, `r2mo-io-local`, `r2mo-ams`, DBE, JCE, JAAS, Spring runtime, or Vert.x/JOOQ runtime.

## Cross-Pack Reading Order

| Topic | First | Then |
|---|---|---|
| attachment upload/download in Zero | `mxt-zero/attachment-storage-configurable-storage.md` | `mxt-r2mo/hfs-hstore-usage.md` |
| `Ut.ioXxx`, `HFS`, `HStore`, `RFS` from Zero code | `mxt-zero/io-utility-hfs-hstore-rules.md` | `mxt-r2mo/hfs-hstore-usage.md` |
| activity log / EXPR rules | `mxt-zero/ambient-activity-expression-rules.md` | source anchors in `zero-exmodule-ambient` |
| dynamic modular operation | `mxt-zero/modulat-dynamic-operation-rules.md` | source anchors in `zero-exmodule-modulat` |

## Script Boundary

Do not change `R2MO_HOME/bin/mo-mcp` for this route if `mxt-zero` and `mxt-r2mo` are already mounted. This file is a routing layer, not a connection-layer requirement.
