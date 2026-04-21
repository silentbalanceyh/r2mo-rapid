# Frontend Admin Design System

> Final design-system guide for enterprise admin frontends in the R2MO ecosystem.

## 1. Scope

Use this file for:

- admin visual tokens,
- form and card patterns,
- standard control classes,
- interaction-state styling.

Do not use this file for:

- React architecture,
- Rust module/routing structure,
- or app-shell/router wiring.

## 2. Brand Colors

### Amber admin theme

- primary background: `bg-amber-600` / `bg-amber-700`
- focus border: `focus:border-amber-500`
- active background: `bg-amber-100 dark:bg-amber-500/15`
- primary text: `text-amber-700 dark:text-amber-500`

### Orange-red login theme

- gradient button/title: `from-orange-500 to-red-500`
- hover: `hover:from-orange-600 hover:to-red-600`

## 3. Neutral Palette

- page background: `bg-gray-50 dark:bg-gray-950`
- card background: `bg-white dark:bg-gray-900`
- input background: `bg-white dark:bg-gray-800`
- border: `border-gray-200 dark:border-gray-800`
- text primary: `text-gray-900 dark:text-gray-100`
- helper text: `text-gray-500 dark:text-gray-400`

## 4. Typography

| Usage | Class |
|---|---|
| input / placeholder | `text-[14px] placeholder:text-[14px]` |
| label | `text-sm text-gray-600 dark:text-gray-400` |
| page title | `text-lg font-bold text-gray-800 dark:text-gray-100` |
| login title | `text-3xl font-bold` |
| helper text | `text-xs text-gray-500 dark:text-gray-400` |

## 5. Spacing and Sizing

| Usage | Class |
|---|---|
| page padding | `p-6` |
| card padding | `p-8` |
| row spacing | `space-y-4` |
| form gap | `gap-8` |
| input/button height | `h-[42px]` |
| header height | `h-16` |
| sidebar width | `w-64` expanded / `w-16` collapsed |

## 6. Standard Form Layout

- standard form grid: `grid grid-cols-2 gap-8`
- single-line row: `flex items-center`
- multi-line row: `flex items-start`
- button row: `flex justify-center gap-4`
- two-column label: `w-1/4 text-right pr-4 shrink-0`

## 7. Standard Control Classes

### Standard input

```text
h-[42px] px-3 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 rounded text-[14px] text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-0 focus:shadow-none focus:border-amber-500 dark:focus:border-amber-500 transition-colors shadow-none appearance-none placeholder:text-[14px]
```

### Standard textarea

```text
px-3 py-2 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-700 rounded text-[14px] text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-0 focus:shadow-none focus:border-amber-500 dark:focus:border-amber-500 transition-colors shadow-none appearance-none resize-none placeholder:text-[14px]
```

### Inline verify/bind button

```text
h-[42px] px-4 bg-white hover:bg-amber-50 dark:bg-gray-800 dark:hover:bg-amber-900/20 border border-amber-600 dark:border-amber-500 text-amber-700 dark:text-amber-500 text-sm rounded transition-colors whitespace-nowrap flex items-center
```

### Primary action button

```text
px-8 py-2 bg-amber-600 hover:bg-amber-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white text-sm rounded transition-colors
```

### Secondary button

```text
px-8 py-2 bg-white hover:bg-gray-50 text-gray-700 border border-gray-300 dark:bg-gray-800 dark:hover:bg-gray-700 dark:text-gray-300 dark:border-gray-600 text-sm rounded transition-colors
```

### Login button

```text
w-full bg-gradient-to-r from-orange-500 to-red-500 hover:from-orange-600 hover:to-red-600 text-white font-semibold py-3.5 px-4 rounded-md transition-all shadow-lg shadow-orange-500/30
```

## 8. Container Patterns

- standard content card:
  `bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-800 p-8`
- login card:
  `bg-white/80 dark:bg-gray-800/80 backdrop-blur-xl rounded-lg shadow-2xl border border-orange-100 dark:border-gray-700`

## 9. Active States

- sidebar active item:
  `bg-amber-100 text-amber-700 font-semibold shadow-sm dark:bg-amber-500/15 dark:text-amber-500`
- submenu active item:
  `text-amber-700 bg-amber-100 font-medium dark:text-amber-500 dark:bg-amber-500/15`

## 10. Enterprise Admin Visual Rules

### Shared design baseline

- clean enterprise UI
- compact admin density
- blue-first interaction palette where local app rules do not override it
- standard body/form text: `14px`
- default radius: `6px`
- spacing rhythm: `4 / 8 / 12 / 16 / 20 / 24 / 32 / 40`

### Admin list page blueprint

- outer page: `bg-gray-50`
- content sections in white cards
- top rhythm: `header -> filter card -> stats cards -> table card`
- filter card uses a bordered white card with compact controls
- table card contains bulk toolbar, body, and pagination in one surface
- status/type fields use tinted chips, not plain text
- row actions are compact inline text actions, not large filled buttons

### Dialog form blueprint

- left-label / right-control rows are the default
- do not stack label above control unless explicitly required
- dialog body needs meaningful horizontal inset
- section titles are taller and more prominent than normal controls
- `备注信息` replaces raw `description` label in Chinese admin UI
- textarea in notes fields should be `resize-none`
- insert a neutral divider below notes before the next section

## 11. Final Rule

For enterprise admin UI, use:

```text
neutral structural surfaces -> strong focus color -> compact density -> consistent form rhythm -> chip-based status emphasis
```
