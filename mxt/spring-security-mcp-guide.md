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

- `JwtSecurityConfigurer` — SPI entry point that registers JWT filter and token processing into the security chain
- `JwtTokenBuilder` — constructs JWT token content (claims, subject, expiration)
- `JwtTokenGenerator` — signs and serializes the built token into its final string form
- `JwtTokenRefresher` — handles refresh-token validation and new token issuance
- `JwtLoginController` — REST endpoint for JWT login (`/auth/login`)
- `JwtSpringAuthenticator` — Spring Security `AuthenticationProvider` implementation for JWT validation

Ownership split: `JwtTokenBuilder` owns claim composition, `JwtTokenGenerator` owns signing/serialization, `JwtTokenRefresher` owns refresh lifecycle. Read the builder first when token content is wrong. Read the generator first when signing fails.

### OAuth2 (Authorization Server)

Read:

- `r2mo-spring-security`
- `r2mo-spring-security-oauth2`

Typical anchors:

- `OAuth2SpringConfigurer` — SPI entry that registers OAuth2 authorization server into the security chain
- `OAuth2SpringAuthorizationServer` — configures the authorization server endpoints (token, authorize, jwk)
- `OAuth2TokenBuilder` — builds and customizes OAuth2 tokens (access + refresh)
- `OAuth2JwtTokenCustomizer` — customizes JWT claims for OAuth2-issued tokens
- `OAuth2JwkSourceManager` — manages JWK key source for token signing and verification
- `OAuth2SpringEncoder` — password encoder configuration for OAuth2 client secrets
- `ConfigOAuth2Spring` — `security.oauth2.*` property binding for the authorization server

Important note: `r2mo-spring-security-oauth2` registers a `SecurityWebConfigurer` through `META-INF/services`. It also uses a **high-priority** filter chain for authorization server endpoints, separate from the normal resource chain.

### OAuth2 Client (Client Registration)

Read:

- `r2mo-spring-security`
- `r2mo-spring-security-oauth2client`

Typical anchors:

- `OAuth2RegisteredClientAuto` — auto-configures registered OAuth2 clients from `security.oauth2.client.*` properties
- `OAuth2RegisteredClientHelper` — helper for client registration lookup and validation

Boundary: `r2mo-spring-security-oauth2` is the **authorization server** (issues tokens). `r2mo-spring-security-oauth2client` is the **client registration** (stores and retrieves registered clients). Read the server module first when the question is about token issuance. Read the client module first when the question is about client configuration or registration.

### LDAP

Read:

- `r2mo-spring-security`
- `r2mo-spring-security-ldap`

Typical anchors:

- `LdapConfig` — Spring configuration that binds `spring.ldap.*` properties to the LDAP context source
- `LdapServiceImpl` — implements LDAP bind authentication against the configured directory
- `LdapCommonController` — REST endpoint for LDAP login (`/auth/ldap-login`)
- `LdapLoginRequest` — request model for LDAP authentication
- `LdapService` — service interface for LDAP authentication

Ownership split: `LdapConfig` owns directory connection configuration. `LdapServiceImpl` owns the authentication bind logic. `LdapCommonController` owns the endpoint. Read `LdapConfig` first when connection fails. Read `LdapServiceImpl` first when authentication logic is wrong.

### SMS (Login)

Read:

- `r2mo-spring-security`
- `r2mo-spring-security-sms`

Typical anchors:

- `SmsService` — service interface for SMS login (pre-auth send + login verification)
- `SmsServiceImpl` — implements verification code generation, caching, and validation
- `SmsServicePreAuth` — handles the pre-auth step: sending verification code to mobile (`/auth/sms-send`)
- `SmsCommonController` — REST endpoints for SMS login flow (`/auth/sms-send`, `/auth/sms-login`)
- `SmsCaptchaConfig` — `security.sms.*` property binding for captcha/code configuration
- `SmsLoginRequest` — request model for SMS login
- `UserAtBaseSMS` — user identity model for SMS-authenticated users

Boundary with `r2mo-spring-sms`: this module handles **authentication login via SMS verification code**. The `r2mo-spring-sms` module handles **notification delivery via SMS**. Do not confuse them. See `spring-delivery-boundary.md` for the full three-layer model.

### Email (Login)

Read:

- `r2mo-spring-security`
- `r2mo-spring-security-email`

Typical anchors:

- `EmailService` — service interface for email login (pre-auth send + login verification)
- `EmailServiceImpl` — implements verification code generation, caching, and validation
- `EmailServicePreAuth` — handles the pre-auth step: sending verification code to email (`/auth/email-send`)
- `EmailCommonController` — REST endpoints for email login flow (`/auth/email-send`, `/auth/email-login`)
- `EmailCaptchaConfig` — `security.email.*` property binding for captcha/code configuration
- `EmailLoginRequest` — request model for email login
- `UserAtBaseEmail` — user identity model for email-authenticated users

Boundary with `r2mo-spring-email`: this module handles **authentication login via email verification code**. The `r2mo-spring-email` module handles **notification delivery via email**. See `spring-delivery-boundary.md`.

### WeCom (Login)

Read:

- `r2mo-spring-security`
- `r2mo-spring-security-weco`

Typical anchors:

- `WeComServiceImpl` — implements WeCom QR-code login and OAuth callback flow
- `WeChatServiceImpl` — implements WeChat (MP/CB) login flow for mobile scenarios
- `WeComRequestUri` — constructs the WeCom OAuth authorization URI
- `WeChatReqPreLogin` — pre-login request model for WeChat flow
- `WeChatCommonController` — REST endpoints for WeCom login (`/auth/wecom-login`, `/auth/wecom-qrcode`)
- `WeChatMPCBController` — REST endpoints for WeChat MP/CB callback flow
- `WeComLoginRequest` — request model for WeCom login
- `UserAtBaseWeCom` — user identity model for WeCom-authenticated users

Boundary with `r2mo-spring-weco`: this module handles **authentication login via WeCom QR code or WeChat OAuth**. The `r2mo-spring-weco` module handles **notification delivery via WeCom**. See `spring-delivery-boundary.md`.

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
