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