---
runAt: 2026-04-22.18-04-24
title: 强化MCP对接说明
author:
---
- 全英文版本
- 在现有的对接上做升级、合并
- 如果追加规则中有重复规则，想办法合并或升级，尽可能不做删除
- code-reviewer-graph
- 地址 mxt/ 之下
- 规则使用单一职责规则

## 规则清单
- 顶层抽象 r2mo-ams
- SPI实现层处理（原生实现、基于Spring容器实现）
- 功能抽象（二级）r2mo-dbe, r2mo-io, r2mo-jaas, r2mo-jce, r2mo-spec
- 双技术栈
- 基于 Spring 的缓存描述：r2mo-spring-cache
- 基于 Spring 的安全说明：r2mo-spring-security-*
- 证书和安全通用服务：r2mo-jaas, r2mo-jce
- 规范和标准：r2mo-spec
- 基于 Vertx 中的 Jooq 生成器：r2mo-vertx-jooq-*
- 邮件功能 -email
- 短信功能 -sms
- 微信功能 -weco

## Changes

### 2026-04-22

- Upgraded the MCP-facing documentation under `mxt/` in English without removing the existing route split.
- Added four new SRP-aligned route documents:
  - `mcp-route-shared-contracts.md`
  - `mcp-route-shared-capability-modules.md`
  - `mcp-route-spring-integrations.md`
  - `mcp-route-vertx-jooq.md`
- Expanded the route coverage so the MCP entry layer now explicitly covers:
  - `r2mo-ams` and `r2mo-spec`
  - `r2mo-dbe`, `r2mo-io`, `r2mo-jaas`, and `r2mo-jce`
  - non-security `r2mo-spring-*` integration modules such as cache, email, sms, weco, doc, and excel
  - `r2mo-vertx` and `r2mo-vertx-jooq-*`
- Updated `mxt/README.md`, `mxt/mxt-r2mo-mcp-rules.md`, and `mxt/framework-trigger-matrix.md` to route wording to the new specialized MCP documents first.
- Fixed and refreshed `mxt/document-srp-audit.md` so the new route files are audited and the misplaced `runtime-configuration-surface.md` row is normalized.
- Performed a deeper `code-review-graph` pass and added `core-capability-index.md` as a graph-backed high-level entry for AI agents extracting framework core functions.
- Filled an additional gap for delivery-provider ownership by extending the reading path to include `r2mo-xync-email`, `r2mo-xync-sms`, and `r2mo-xync-weco`, not only the Spring-side integration modules.
- Added independent single-purpose documents for the previously merged rule items: `ams-boundary.md`, `spi-implementation-boundary.md`, `io-boundary.md`, `jaas-boundary.md`, `jce-boundary.md`, `spring-cache-guide.md`, `delivery-email-guide.md`, `delivery-sms-guide.md`, and `delivery-weco-guide.md`.
- Updated `mxt/README.md` and `mxt/document-srp-audit.md` so the rule checklist can now map each missing detail rule to a dedicated primary document instead of only a family-level route.
- Added `test/test_mxt_rules.py` with 50 executable `unittest` cases to validate the current `mxt/` rule set, route index wiring, SRP audit coverage, delivery-module coverage, and task-file change tracking.
- Added a second optimization layer for AI-agent consumption: `ai-agent-fast-start.md`, `mcp-shortest-path.md`, `distilled-capability-cards.md`, and `mcp-token-saving-rules.md`.
- Reordered the MXT reading strategy so broad docs become second-line references while MCP can now follow a shortest path of `fast start -> one route -> one module guide -> exact source`.
- Extended the test suite from 50 to 72 executable `unittest` cases so the new fast-entry and token-saving rules are also enforced.
- Expanded the regression suite to 200 executable `unittest` cases for document-routing consistency, fast-entry coverage, route compression, and token-saving behavior, then removed the temporary test files after verification as requested.
- Split the previously broad `evolution-rules.md` governance file into four single-purpose rules: `mxt-file-creation-rules.md`, `mxt-sync-rules.md`, `mxt-upgrade-verification.md`, and `mxt-change-log-rules.md`.
- Ran another `code-review-graph` pass and filled the next high-value rule gaps with `spring-runtime-guide.md`, `oauth2-token-guide.md`, `spring-doc-guide.md`, and `spring-excel-guide.md`.
- No explicit Team mode switch was found in the current workspace task context, so the task was executed directly by the leader without spawning Workers.
