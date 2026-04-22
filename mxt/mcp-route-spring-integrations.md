# MCP Route — Spring Integrations

> Final MCP route for non-security Spring integration modules in `r2mo-rapid`.

## 1. Activation

Activate this route when the request is clearly about:

- `r2mo-spring-cache`,
- `r2mo-spring-json`,
- `r2mo-spring-doc`,
- `r2mo-spring-email`,
- `r2mo-spring-sms`,
- `r2mo-spring-template`,
- `r2mo-spring-excel`,
- `r2mo-spring-weco`,
- `r2mo-spring-mybatisplus`,
- `r2mo-xync-email`,
- `r2mo-xync-sms`,
- `r2mo-xync-weco`,
- or Spring-side integration modules that are not primarily Spring Security plugins.

Preferred regex:

```regex
(?i)\b(r2mo-spring-(cache|json|doc|email|sms|template|excel|weco|mybatisplus)|r2mo-xync-(email|sms|weco)|spring cache|knife4j|springdoc|thymeleaf|email client|email provider|sms provider|wecom|wechat|weco|excel processor|multipart factory|jackson configuration)\b
```

## 2. Mandatory Reading Set

- `spring-layer-map.md`
- `extension-points.md`
- `framework-map.md`
- `mxt-r2mo-mcp-rules.md`

## 3. Execution Contract

When this route matches, the agent should:

1. confirm that the request is Spring integration work rather than shared abstraction work,
2. read the exact `r2mo-spring-*` module before touching boot defaults,
3. check whether the behavior is implemented through SPI, auto-configuration, or both,
4. keep security plugin routing on the dedicated Spring Security route.

Primary sub-families:

- cache and state: `r2mo-spring-cache`
- integration and delivery: `r2mo-spring-email`, `r2mo-spring-sms`, `r2mo-spring-weco`, `r2mo-spring-template`
- delivery-provider foundation: `r2mo-xync-email`, `r2mo-xync-sms`, `r2mo-xync-weco`
- docs and import/export: `r2mo-spring-doc`, `r2mo-spring-excel`
- adapters: `r2mo-spring-json`, `r2mo-spring-mybatisplus`

## 4. Allowed Combinations

This route can combine with:

- `mcp-route-code-review-graph.md`
- `mcp-route-shared-capability-modules.md`
- `mcp-route-code-generator.md`

Examples:

- `which module owns Spring cache provider selection` -> Spring integrations route + shared capability modules route
- `how should email and sms integrations be read under MCP` -> Spring integrations route
- `does this SMS or email provider issue belong to xync or spring` -> Spring integrations route + shared capability modules route
- `does Excel generation belong to generator or spring integration` -> Spring integrations route + code generator route

## 5. Priority Rule

This route should beat generic cache/provider wording when the concrete target is already an `r2mo-spring-*` integration module.

It should lose to `mcp-route-spring-security.md` when the target is clearly a security login/plugin flow such as JWT, OAuth2, LDAP, SMS login, email login, or WeCom login.

## 6. Do Not Do

- Do not merge security plugin behavior into this route.
- Do not read `r2mo-boot-spring-default` first unless the question is explicitly about default assembly.
- Do not treat vendor integration words such as `email`, `sms`, `wechat`, or `weco` as business-project-only terms before checking the Spring and `xync-*` integration modules.

## 7. Final Rule

Use this reading order:

```text
exact spring or xync integration module -> SPI/auto-configuration -> shared dependency module -> boot default only if needed
```
