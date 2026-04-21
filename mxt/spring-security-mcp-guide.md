# Spring Security MCP Guide

> Final subsystem guide for the Spring Security family in `r2mo-rapid`.
> Use this guide when the task is clearly about Spring Security behavior rather than generic auth vocabulary.

## 1. Scope

This guide covers:

- `r2mo-spring-security`
- `r2mo-spring-security-jwt`
- `r2mo-spring-security-oauth2`
- `r2mo-spring-security-oauth2client`
- `r2mo-spring-security-ldap`
- `r2mo-spring-security-email`
- `r2mo-spring-security-sms`
- `r2mo-spring-security-weco`

It also depends on shared auth/session contracts from:

- `r2mo-jaas`

## 2. Why This Family Deserves a Dedicated Guide

This repository has a generic security/auth trigger path, but the real Spring Security surface is larger and plugin-based.

The base module owns:

- filter-chain composition,
- shared `security.*` property binding,
- login-mode routing,
- token/captcha/basic infrastructure,
- and SPI hooks for plugin modules.

The plugin modules then layer mode-specific behavior such as:

- JWT
- OAuth2
- LDAP
- SMS
- email
- WeCom

An agent should therefore not read one plugin file and assume it understands the whole auth system.

## 3. Default Reading Order

Use this order:

```text
1. spring-layer-map.md
2. this guide
3. r2mo-spring-security base module
4. the concrete r2mo-spring-security-* plugin module
5. META-INF/services and config resources
6. exact source methods
```

## 4. Core Entry Points

### 4.1 `SecurityWebConfiguration`

Read this class first for Spring Security runtime assembly.

It owns:

- shared `HttpSecurity` setup,
- base REST-style defaults,
- `SecurityFilterChain` construction,
- SPI-driven `SecurityWebConfigurer` loading,
- and plugin participation in high-priority or normal filter chains.

This is the correct first stop for:

- filter ordering,
- request authorization,
- auth/resource chain conflicts,
- plugin registration effects,
- and request-cache behavior.

### 4.2 `ConfigSecurity`

Read this class first for configuration semantics.

It owns:

- `security.*` property binding,
- ignore URI parsing,
- token-type fallback rules,
- `basic`, `jwt`, `captcha`, and `oauth2` enable/disable checks.

This is the correct first stop for:

- YAML meaning,
- enablement questions,
- fallback token behavior,
- and “why is this mode active or inactive?” debugging.

### 4.3 `AuthService` and `AuthServiceManager`

Read these when the task is about login modes and execution flow.

`AuthService` documents the mode-specific endpoints:

- pre-auth:
  - SMS -> `/auth/sms-send`
  - email -> `/auth/email-send`
  - LDAP -> none
  - WeCom -> `/auth/wecom-qrcode`
  - password -> `/auth/captcha`
- login:
  - SMS -> `/auth/sms-login`
  - email -> `/auth/email-login`
  - LDAP -> `/auth/ldap-login`
  - WeCom -> `/auth/wecom-login`
  - password -> `/auth/login`

`AuthServiceManager` owns:

- pre-auth provider selection,
- user-provider selection by `TypeLogin`,
- auth-code caching through `UserCache`,
- successful session write through `UserSession`.

This is the correct first stop for:

- login-mode routing,
- captcha/send-code behavior,
- session write timing,
- and provider lookup failures.

### 4.4 `SecurityWebConfigurer`

Read this interface when the task is about plugin participation in the filter chain.

It provides:

- `configure(...)` for the normal shared resource chain,
- `configureHighPriority(...)` for split or higher-priority chains.

This matters especially for OAuth2 because the repository supports plugin modules that need a separate high-priority chain.

## 5. Plugin-Specific Reading Hints

### JWT

Read:

- `r2mo-spring-security`
- `r2mo-spring-security-jwt`

Typical anchors:

- `JwtSecurityConfigurer`
- `JwtTokenBuilder`
- `JwtTokenGenerator`
- `JwtTokenRefresher`

### OAuth2

Read:

- `r2mo-spring-security`
- `r2mo-spring-security-oauth2`
- optionally `r2mo-spring-security-oauth2client`

Typical anchors:

- `OAuth2SpringConfigurer`
- `OAuth2Switcher`
- `OAuth2Endpoint`
- `OAuth2SpringAuthResource`
- `ConfigOAuth2`

Important note:

`r2mo-spring-security-oauth2` registers a `SecurityWebConfigurer` through `META-INF/services`, so SPI wiring is part of the runtime behavior rather than a side detail.

### LDAP

Read:

- `r2mo-spring-security`
- `r2mo-spring-security-ldap`

Typical anchors:

- `LdapConfig`
- `LdapCommonController`
- `LdapServiceImpl`

### SMS / email / WeCom

Read:

- `r2mo-spring-security`
- the matching plugin module

Treat these as mode-specific login plugins rather than app-local controllers.

## 6. What Agents Should Assume

- The base module is authoritative for shared filter-chain behavior.
- Plugin modules extend the base module through SPI and Spring configuration.
- `r2mo-jaas` still matters because session, token, and auth cache contracts are not owned only by Spring classes.
- The same requirement may involve both base security and one plugin module.

## 7. What Agents Should Not Assume

- Do not assume one plugin module explains the full login flow.
- Do not assume YAML alone explains active behavior; verify effective dependencies and SPI resources.
- Do not treat OAuth2, JWT, and Basic as the same path; they can coexist but are not interchangeable.
- Do not patch app-local controllers first when the behavior is clearly framework-owned.

## 8. Search Terms That Usually Work

Search these first:

- `SecurityWebConfiguration`
- `ConfigSecurity`
- `AuthServiceManager`
- `SecurityWebConfigurer`
- `UserCache`
- `META-INF/services/io.r2mo.spring.security.config.SecurityWebConfigurer`
- `META-INF/services/io.r2mo.spring.security.extension.AuthSwitcher`

## 9. Final Rule

If the task is Spring Security specific, read:

```text
base security module -> plugin module -> SPI resources -> exact methods
```

That order is the final default.
