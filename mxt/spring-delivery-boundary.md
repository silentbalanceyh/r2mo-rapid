# Spring Delivery Boundary

> Boundary document for the three-layer email/sms/weco delivery architecture in `r2mo-rapid`.

## 1. Purpose

The framework has three distinct layers for email, SMS, and WeCom (WeChat) integration. AI agents frequently confuse them because the same words (`email`, `sms`, `weco`) appear at all three layers. This document draws the boundary explicitly.

## 2. Three-Layer Architecture

```text
Layer 1 — Provider Foundation (container-neutral)
  r2mo-xync-email   — vendor-side email contracts and provider abstractions
  r2mo-xync-sms     — vendor-side SMS contracts and provider abstractions
  r2mo-xync-weco    — vendor-side WeCom/WeChat contracts and provider abstractions

Layer 2 — Spring Notification Integration (delivery)
  r2mo-spring-email  — Spring-side email sending and notification integration
  r2mo-spring-sms    — Spring-side SMS sending and notification integration
  r2mo-spring-weco   — Spring-side WeCom message and notification integration

Layer 3 — Spring Security Login Modes (authentication)
  r2mo-spring-security-email  — email-based login (verification code / magic link)
  r2mo-spring-security-sms    — SMS-based login (verification code)
  r2mo-spring-security-weco   — WeCom-based login (QR code / OAuth)
```

## 3. How To Route

### "I need to send an email/SMS/WeCom message"

→ Layer 2 (`r2mo-spring-email/sms/weco`)

Read: `mcp-route-spring-integrations.md` + `delivery-*-guide.md`

### "I need to add or change a vendor/provider for email/SMS/WeCom"

→ Layer 1 (`r2mo-xync-email/sms/weco`)

Read: `mcp-route-spring-integrations.md` + `delivery-*-guide.md`

### "I need to add or fix an email/SMS/WeCom login mode"

→ Layer 3 (`r2mo-spring-security-email/sms/weco`)

Read: `mcp-route-spring-security.md` + `spring-security-mcp-guide.md`

### "I need to understand the full path from login to notification"

→ Read Layer 3 first (authentication), then Layer 2 (notification), then Layer 1 (provider).

## 4. Dependency Direction

```text
r2mo-spring-security-email/sms/weco
  -> depends on r2mo-spring-security (base auth)
  -> depends on r2mo-jaas (shared auth primitives)

r2mo-spring-email/sms/weco
  -> depends on r2mo-spring (Spring base)
  -> depends on r2mo-xync-email/sms/weco (provider foundation)

r2mo-xync-email/sms/weco
  -> depends on r2mo-ams (shared vocabulary)
  -> no Spring dependency
```

Layer 1 has no Spring dependency. Layer 2 and Layer 3 are Spring-specific.

## 5. Common Confusion Points

| Confusion | Correct routing |
|---|---|
| "SMS login code" | `r2mo-spring-security-sms` (Layer 3) — this is authentication |
| "SMS notification message" | `r2mo-spring-sms` (Layer 2) — this is delivery |
| "SMS vendor API change" | `r2mo-xync-sms` (Layer 1) — this is provider foundation |
| "WeCom QR login" | `r2mo-spring-security-weco` (Layer 3) |
| "WeCom push notification" | `r2mo-spring-weco` (Layer 2) |

## 6. Do Not Do

- Do not put authentication logic into `r2mo-spring-email` or `r2mo-xync-email`.
- Do not put notification delivery logic into `r2mo-spring-security-email`.
- Do not put Spring-specific code into `r2mo-xync-*` modules.
- Do not treat "email" or "sms" as a single module without checking which layer the task targets.

## 7. Final Rule

When the trigger word is `email`, `sms`, or `weco`, first determine whether the task is about **authentication**, **notification**, or **provider foundation**. Then route to the corresponding layer.
