# MCP Route — Code Generator

> Final MCP route for generator-related work in `r2mo-rapid`.

## 1. Activation

Activate this route when the request is about:

- generated code,
- generator customization,
- templates,
- scaffold processors,
- CRUD generation,
- or jOOQ forced-type generation.

Preferred regex:

```regex
(?i)\b(generator|codegen|code generation|sourcegenerator|genprocessor|genconfig|generated code|crudcontroller|servicev1|mybatisplus generator|jooq generator|typeofjooq|forcedtype|ftl template)\b
```

## 2. Mandatory Reading Set

- `code-generator-usage.md`
- `extension-points.md`
- `framework-trigger-matrix.md`

## 3. Execution Contract

When this route matches, the agent should:

1. classify the generator family first,
2. change templates, processors, or SPI registrations instead of patching generated output,
3. open the concrete generator module only after the family is known.

Primary generator families in this repository are:

- `r2mo-boot-spring` scaffold generator
- `r2mo-dbe-mybatisplus` CRUD/SQL generator
- `r2mo-vertx-jooq-generate` jOOQ source/type generator

## 4. Guardrail

Do not activate this route from generic words such as:

- `query`
- `sql`
- `database`

unless generation words also appear.

## 5. Do Not Do

- Do not edit generated files first.
- Do not mix scaffold generation with DBE/jOOQ generation without identifying the family.
- Do not assume all generator behavior lives in one module.

## 6. Final Rule

Use this reading order:

```text
generator family -> processor or SPI entry -> template or config -> generated output
```
