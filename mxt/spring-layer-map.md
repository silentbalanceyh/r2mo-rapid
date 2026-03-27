# Spring Layer Map

This map explains only the Spring line and does not cover Vert.x.

## 1. Spring Main Chain

```text
r2mo-spec / r2mo-ams / r2mo-dbe / r2mo-io / r2mo-jce / r2mo-jaas
  -> r2mo-spring
    -> r2mo-spring-security
    -> r2mo-spring-* / r2mo-spring-security-*
      -> r2mo-boot-spring
        -> r2mo-boot-spring-default
```

## 2. Responsibility of Each Layer

### 2.1 `r2mo-spring`

This is the Spring foundation layer.
From `pom.xml`, it directly depends on:

- `r2mo-ams`
- Spring Boot / Autoconfigure
- Spring AOP
- `spring-web` / `spring-webmvc`
- `spring-security-core`

This means its responsibilities are:

- Providing foundational shared capabilities inside the Spring environment
- Handling base integrations such as exceptions, Web, Bean, and context
- Offering a unified Spring base for higher-level extensions

It should not carry specific authentication modes, specific SMS vendors, or business-domain logic.

### 2.2 `r2mo-spring-security`

This is the Spring security base.
From `pom.xml`, it depends on:

- `r2mo-spring`
- `r2mo-jaas`
- `r2mo-spec`
- `spring-boot-starter-security`
- `spring-cloud-context`
- `hutool-captcha`

This means it is responsible for:

- Landing JAAS abstractions into the Spring Security system
- Carrying shared security infrastructure such as users, claims, tokens, and captcha
- Providing a common foundation for modules such as `jwt / oauth2 / ldap / sms / email / weco`

### 2.3 `r2mo-spring-*`

This is the Spring general capability extension layer, for example:

- `r2mo-spring-json`
- `r2mo-spring-mybatisplus`
- `r2mo-spring-cache`
- `r2mo-spring-doc`
- `r2mo-spring-template`
- `r2mo-spring-email`
- `r2mo-spring-sms`

Its role is typically to land some shared base capability into Spring.

### 2.4 `r2mo-spring-security-*`

This is the Spring login / authentication / security plugin layer, for example:

- `jwt`
- `ldap`
- `oauth2`
- `oauth2client`
- `sms`
- `email`
- `weco`

If the requirement is essentially "add a new authentication mode", this is the preferred landing zone.

### 2.5 `r2mo-boot-spring`

This is the Spring bootstrap abstraction layer.
From its dependencies, it aggregates the following foundational capabilities:

- `r2mo-dbe`
- `r2mo-io`
- `r2mo-spring`
- `r2mo-jce`
- `r2mo-jaas`
- MySQL / Flyway
- Validation
- `spring-boot-starter-web`

This means it is oriented toward **project bootstrap standardization**, not plugin implementation.

### 2.6 `r2mo-boot-spring-default`

This is the default Spring onboarding package.
On top of `r2mo-boot-spring`, it additionally includes:

- `r2mo-spring-json`
- `r2mo-typed-hutool`
- `r2mo-io-local`
- `r2mo-spring-mybatisplus`

The meaning is straightforward:

- If you want the **fastest project startup**, use `default`.
- If you want to **choose implementations yourself**, inherit only `boot-spring` and add modules as needed.

## 3. Typical Requirement Landing Points

### Add a unified JSON serialization strategy

Landing point: `r2mo-spring-json`

### Add an authentication plugin based on Spring Security

Landing point: `r2mo-spring-security-*`

### Add a global Web exception handling convention

Landing point: `r2mo-spring`

### Add a default Spring scaffold dependency combination

Landing point: `r2mo-boot-spring-default`

## 4. Boot Assembly Chain and Forbidden Placement Zones

### Assembly chain

For Spring projects, the practical assembly chain is:

`r2mo-spec / r2mo-dbe / r2mo-io / r2mo-jaas / r2mo-jce` -> `r2mo-spring` -> `r2mo-spring-security` and `r2mo-spring-*` -> `r2mo-boot-spring` -> `r2mo-boot-spring-default`.

Agents should read this chain left to right:

- left side = shareable contract and abstraction base
- middle = Spring runtime base and plugin layers
- right side = project bootstrap packaging

### Where business logic must not go

Do not place business logic in:

- `r2mo-boot-spring`
- `r2mo-boot-spring-default`
- generic `r2mo-spring` container base code

These layers are for assembly, onboarding, and runtime infrastructure. They are not for customer rules, domain workflows, tenant-specific defaults, or project-specific service logic.

## 5. What Should Not Enter the Spring Layer

- Shared OpenAPI schemas
- Pure contract-style error code definitions
- Container-independent abstraction interfaces
- Strong business service logic

These should instead go back to `spec`, abstraction layers, or business projects.

## 5. A Simplified Decision Rule

- If it needs Spring Bean / Web / Security mechanisms, it belongs to the Spring layer.
- If it is only a shared contract, it belongs to `spec`.
- If it is only a project-specific login flow detail, it belongs to the business project.
