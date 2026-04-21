# Integration Runtime Contract Guide

> Final runtime contract and configuration guide for the app-takeout / R2MO integration pattern.

## 1. Scope

Use this file for:

- environment-specific configuration rules,
- runtime-secret handling,
- typed configuration binding,
- integration-safe runtime deployment assumptions.

## 2. Runtime Configuration Rules

- environment-specific configuration must be externalized
- use profile split such as:
  - `application.yml`
  - `application-dev.yml`
  - `application-test.yml`
  - `application-prod.yml`

## 3. Secret And Dependency Rules

- sensitive values must come from environment variables
- do not hardcode MySQL, Redis, JWT, or similar runtime dependencies
- runtime dependency configuration is part of the integration contract, not an afterthought

## 4. Configuration Binding Rules

- use typed `@ConfigurationProperties` classes
- apply validation to configuration binding where appropriate
- treat invalid configuration as a startup defect, not a business-runtime branch

## 5. Reusable Runtime Rules

1. start from `.r2mo` contracts, not implementation guesses
2. keep DPA boundaries strict so generated integrations remain composable
3. generate frontend models and API clients from the same contract source as backend
4. externalize runtime dependencies and secrets
5. use `metadata` for controlled extension instead of forking common schemas too early

## 6. Final Rule

For runtime integration, use:

```text
profiled configuration -> externalized secrets -> typed binding -> fail-fast runtime contract
```
