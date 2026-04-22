# MCP Route — Spring Security

> Final MCP route for the Spring Security subsystem in `r2mo-rapid`.

## 1. Activation

Activate this route when the request is clearly about:

- Spring Security runtime behavior,
- filter chains,
- login modes (JWT, OAuth2, LDAP, SMS, email, WeCom),
- token flow,
- auth plugin modules,
- or security SPI integration.

Preferred regex:

```regex
(?i)\b(spring security|securityfilterchain|securitywebconfiguration|securitywebconfigurer|configsecurity|authservicemanager|login mode|filter chain|jwt|oauth2|oauth2client|ldap|captcha|bearer|token refresh|auth switcher|usercache|security.email|security.sms|security.weco|security.ldap|sms.login|email.login|wecom.login|ldap.login)\b
```

## 2. Mandatory Reading Set

- `spring-security-mcp-guide.md`
- `spring-layer-map.md`
- `mxt-r2mo-mcp-rules.md`

## 3. Execution Contract

When this route matches, the agent should:

1. read `r2mo-spring-security` first,
2. read the concrete `r2mo-spring-security-*` plugin module together with the base module,
3. confirm SPI registrations under `META-INF/services`,
4. verify `security.*` and, when relevant, `spring.security.oauth2.*` assumptions.

## 4. Priority Rule

This route should beat the generic security/auth row in `framework-trigger-matrix.md` when the request is clearly Spring Security specific.

It should also beat `mcp-route-spring-integrations.md` when the target is clearly a **login/authentication** flow rather than a **notification/delivery** flow. Boundary:

- `r2mo-spring-security-email` / `security-sms` / `security-weco` = authentication login modes → **this route**
- `r2mo-spring-email` / `spring-sms` / `spring-weco` = notification delivery integration → `mcp-route-spring-integrations.md`
- `r2mo-xync-email` / `xync-sms` / `xync-weco` = provider-side delivery foundation → `mcp-route-spring-integrations.md`

## 5. Do Not Do

- Do not read plugin classes in isolation.
- Do not infer behavior from YAML alone.
- Do not treat JWT, OAuth2, LDAP, SMS, email, and WeCom as interchangeable modes.

## 6. Final Rule

Use this reading order:

```text
base security module -> plugin module -> SPI resources -> exact methods
```
