# MCP Token-Saving Rules

> Single-purpose rules for reducing token usage when AI agents read `r2mo-rapid` through MCP.

## 1. Primary Principle

Every extra document must justify its token cost.

## 2. Read Order

Use the smallest valid entry:

1. `ai-agent-fast-start.md` when starting cold
2. one `mcp-route-*.md` file when the trigger family is known
3. one boundary or guide file when the module family is known
4. exact source

## 3. Documents To Defer

Defer these unless needed:

- `README.md`
- `mxt-r2mo-ai-agent-guide.md`
- `code-review-graph-r2mo-analysis.md`
- broad architecture maps

## 4. One-Hop Preference Rules

- specific module name -> module guide first
- specific route family -> route file first
- only vague wording -> fast start first

## 5. Stop Conditions

Stop retrieval when:

- module ownership is confirmed,
- runtime vs contract boundary is confirmed,
- one execution path is confirmed,
- or one correct source file set is identified.

## 6. Final Rule

MCP consumption should optimize for correct first answer, not maximum early coverage.
