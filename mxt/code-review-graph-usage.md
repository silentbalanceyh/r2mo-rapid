# Code Review Graph Usage for MXT-R2MO

> Operational guide for using `code-review-graph` against `r2mo-rapid`.
> This file complements repository-reading rules by providing executable usage patterns.

## 1. Why This Exists

`mxt-r2mo` is primarily a filesystem-style MCP view of the framework repository.
That is good for reading source and docs, but weak for quickly answering:

- which module most likely owns a requirement,
- which capability family a vague requirement belongs to,
- what symbols and hotspots deserve first attention,
- what changed between two revisions.

`code-review-graph` provides a local graph index to narrow the reading set before manual source inspection.

## 2. Local Artifacts

Graph artifacts live under:

```text
.code-review-graph/
  graph.db
```

This directory is intentionally ignored by git.

## 3. Recommended Wrapper

Use the repository wrapper first:

```bash
bin/mxt-r2mo-graph build
bin/mxt-r2mo-graph update
bin/mxt-r2mo-graph status
bin/mxt-r2mo-graph visualize community html
bin/mxt-r2mo-graph wiki
bin/mxt-r2mo-graph serve
bin/mxt-r2mo-graph install-codex
```

The wrapper always binds the graph command to the current repository root and avoids repeated `--repo` typing.

## 4. Core Commands

### 4.1 Full build

Use when:

- the graph does not exist,
- many files changed,
- branch rebases or large merges occurred,
- graph quality is suspicious.

Command:

```bash
bin/mxt-r2mo-graph build
```

### 4.2 Incremental update

Use when:

- the graph already exists,
- only recent commits need to be reflected.

Command:

```bash
bin/mxt-r2mo-graph update
bin/mxt-r2mo-graph update HEAD~3
```

### 4.3 Status

Use when:

- you need a quick health check,
- you want to verify branch/commit freshness,
- you want to confirm the graph exists before using graph-backed reading rules.

Command:

```bash
bin/mxt-r2mo-graph status
```

### 4.4 Visualize

Use when:

- a human needs a visual cluster overview,
- ownership is unclear,
- community structure is more useful than raw code search.

Examples:

```bash
bin/mxt-r2mo-graph visualize
bin/mxt-r2mo-graph visualize community html
bin/mxt-r2mo-graph visualize full svg
```

### 4.5 Wiki generation

Use when:

- you want graph-derived markdown pages,
- you need a more navigable community-based summary.

Command:

```bash
bin/mxt-r2mo-graph wiki
```

### 4.6 MCP serve mode

Use when:

- you want the graph itself exposed as an MCP server,
- a platform or workflow wants graph-aware MCP access rather than shell commands.

Command:

```bash
bin/mxt-r2mo-graph serve
```

## 5. Suggested Maintenance Rhythm

Recommended lightweight rhythm:

1. after first checkout or major rebase: `build`
2. during ordinary local work: `update`
3. before deep framework analysis: `status`
4. when architecture ownership is unclear: `visualize`

## 6. Practical Rules

### Rule 1 — Do not run parallel graph commands against the same repo

The graph database is SQLite-backed.
Running `build`, `update`, `status`, or other graph commands in parallel can produce `database is locked`.

### Rule 2 — Prefer `update` over repeated `build`

For day-to-day use, incremental update is cheaper and usually sufficient.
Only rebuild when you suspect the graph is stale or structurally inconsistent.

### Rule 3 — Use graph output to narrow, not to decide alone

Graph results should determine:

- where to look first,
- what to ignore for now,
- which capability family is most likely relevant.

Graph results should not override actual source behavior.

## 7. When Agents Should Use This Tooling

Use graph support when:

- the user asks framework-level questions,
- the requirement could hit multiple modules,
- the task mentions general words such as `login`, `query`, `license`, `upload`, `provider`, `cache`, `generator`,
- the agent needs likely execution logic or ownership.

Skip graph and go straight to source only when:

- the exact file is already known,
- the symbol is unique and direct,
- the question is only about one markdown document.

## 8. Relationship to Other MXT Docs

Recommended order:

```text
mxt/README.md
  -> mxt/code-review-graph-r2mo-analysis.md
  -> mxt/mxt-r2mo-mcp-rules.md
  -> source code
```

Use this file when the missing piece is not “what does the repo mean?” but “how do I operate the graph tooling effectively?”
