# Distilled Capability Cards

> Ultra-compact capability cards for AI-agent retrieval.

## 1. Shared Foundation

- `r2mo-ams`: shared framework vocabulary and base semantics
- `r2mo-spec`: schema, marker, OpenAPI, error contract

## 2. Shared Capability Layer

- `r2mo-dbe`: query, CRUD, criteria, pager, sorter, projection
- `r2mo-io`: storage and transfer abstraction
- `r2mo-jaas`: auth primitives, claim base, user cache
- `r2mo-jce`: crypto, signing, verification, license primitives
- `r2mo-typed-hutool`: Hutool-based synchronous data-type implementation
- `r2mo-typed-vertx`: Vert.x-based asynchronous data-type implementation

## 2.5 Implementation Selection Layer

- `r2mo-dbe-mybatisplus`: MyBatis-Plus DBE implementation → `dbe-implementation-boundary.md`
- `r2mo-dbe-jooq`: jOOQ DBE abstraction contracts → `dbe-implementation-boundary.md`

## 3. Spring Runtime Layer

- `r2mo-spring`: Spring base integration
- `r2mo-spring-security`: Spring Security base runtime
- `r2mo-spring-*`: Spring-side integrations such as cache, doc, email, sms, excel, weco, json, template, mybatisplus
- `r2mo-spring-security-*`: auth plugins (jwt, oauth2, ldap, sms, email, weco) → `spring-delivery-boundary.md` for login vs delivery boundary

## 4. Delivery Foundation

- `r2mo-xync-email`: provider-facing email foundation
- `r2mo-xync-sms`: provider-facing SMS foundation
- `r2mo-xync-weco`: provider-facing WeCom foundation

## 5. Vert.x Runtime Layer

- `r2mo-vertx`: Vert.x base runtime
- `r2mo-vertx-jooq*`: Vert.x async DB bridge and jOOQ runtime/generation

## 6. Bootstrap Layer

- `r2mo-boot-spring`: Spring bootstrap abstraction → `boot-assembly-guide.md`
- `r2mo-boot-spring-default`: default Spring startup bundle → `boot-assembly-guide.md`
- `r2mo-boot-vertx`: Vert.x bootstrap assembly → `boot-assembly-guide.md`

## 7. Retrieval Rule

If one card is enough to identify ownership, stop and open the matching route or boundary document only.
