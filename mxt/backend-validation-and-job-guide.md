# Backend Validation And Job Guide

> Final guide for validation, exception, pagination, and scheduled job boundaries in the app-takeout / R2MO backend pattern.

## 1. Scope

Use this file for:

- validation placement,
- exception placement,
- pagination boundary rules,
- scheduled job ownership.

## 2. Validation Rules

- validate at the API boundary with JSR-303 / Bean Validation
- business validation that depends on database state belongs in provider services
- keep validation concerns out of transport-unaware domain objects unless they are true shared invariants

## 3. Exception Rules

- throw typed exceptions in provider or business code
- format exceptions uniformly through API global handlers
- do not bury transport-specific exception formatting in provider logic

## 4. Pagination Rules

- use QQuery/QR-style pagination and sorting for list queries
- do not introduce ad-hoc paging contracts when framework pagination types already exist

## 5. Scheduled Job Rules

- scheduling and job wiring belong in the API layer
- business execution triggered by jobs belongs in the provider layer
- runtime context may be passed through `JobDataMap`
- jobs should be autowired through a custom `SpringBeanJobFactory` when that is the project pattern

## 6. Final Rule

For validation and jobs, use:

```text
api validates and schedules -> provider executes and throws typed business exceptions
```
