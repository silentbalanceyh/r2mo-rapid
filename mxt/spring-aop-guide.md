# Spring AOP Guide

> Load this file when the task explicitly targets Spring AOP, AspectJ-style annotations in the Spring line, captcha aspect hooks, or deciding whether a cross-cutting concern belongs in `r2mo-spring` or `r2mo-spring-security`.

## 1. Scope

This guide covers AOP ownership in the R2MO Spring stack.

## 2. Verified Anchors

### Spring base layer

`r2mo-spring` is the base runtime owner for:

- shared Bean / Web / AOP support

That ownership is already reflected in:

- `spring-runtime-guide.md`
- `spring-layer-map.md`

### Spring Security AOP

Verified source anchors:

- `r2mo-spring-security/.../captcha/CaptchaValidationAspect.java`
- `CaptchaOn.java`
- `CaptchaConfiguration.java`
- `CaptchaService.java`

This proves at least one concrete reusable Spring AOP seam already lives in the security base.

## 3. Ownership Rules

### Put behavior in `r2mo-spring` when:

- the concern is generic Spring cross-cutting support
- it is not security-specific
- multiple Spring-side feature modules may depend on it

### Put behavior in `r2mo-spring-security` when:

- the aspect is auth/security/captcha/login related
- the annotation or advice changes security behavior
- it participates in Spring Security runtime assembly

### Put behavior in `r2mo-spring-security-*` when:

- the aspect is mode/plugin-specific
- it belongs to JWT/OAuth2/LDAP/SMS/email/WeCom plugin behavior

## 4. What This Is Not

Do not confuse Spring AOP here with:

- Zero overlay AOP in `zero-ecotope`
- Zero CRUD mutation AOP in `zero-extension-crud`
- Momo BOM/version governance

## 5. Fast Decision Rule

1. If the concern is generic Spring cross-cutting, start at `r2mo-spring`.
2. If it is security/captcha/login cross-cutting, start at `r2mo-spring-security`.
3. If it is login-mode/plugin specific, continue into the matching `r2mo-spring-security-*` module.
