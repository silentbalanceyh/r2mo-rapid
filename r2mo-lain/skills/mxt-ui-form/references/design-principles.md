# Form Design Principles for Enterprise B-Side Applications

This document summarizes design principles, development experiences, and best practices for building professional enterprise forms.

## Core Design Philosophy

### 1. Container Layer Design

**Spacing Hierarchy (Golden Ratio Base: 8px)**

| Level | Value | Usage |
|-------|-------|-------|
| xs | 8px | Tight spacing within controls |
| sm | 12px | Label-to-input gap |
| md | 16px | Standard component gap |
| lg | 24px | Section separator |
| xl | 32px | Container padding |
| 2xl | 48px | Page-level margin |

**Container Rules**
- Form container: `padding: 24px-32px` (inner form area)
- Section container: `padding: 16px-24px` + `margin-bottom: 24px`
- Never allow overlapping elements
- Use `border-box` for all containers: `box-sizing: border-box`

```css
.form-container {
  padding: 1.5rem;  /* 24px */
  background: white;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-section {
  padding: 1rem;
  margin-bottom: 1.5rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.375rem;
}
```

---

### 2. Grid Layout & Label Alignment

**The Core Problem**
In multi-column grids, full-width rows have different widths than grid cells. Using the same `w-1/4` class produces different pixel widths.

**Solution: Calc Formula**
```
label_width = calc((100% - gap × (C-1)) / C × F)
```
- C = grid columns (1-4)
- F = label fraction (e.g., 1/4 = 0.25)
- gap = horizontal gap in rem

**Implementation**

| Grid | Gap | Fraction | Full-Width Label Class |
|------|-----|----------|------------------------|
| 1-col | 2rem | 1/4 | `w-1/4` |
| 2-col | 2rem | 1/4 | `w-[calc((100%-2rem)/8)]` |
| 3-col | 2rem | 1/4 | `w-[calc((100%-4rem)/12)]` |
| 4-col | 2rem | 1/4 | `w-[calc((100%-6rem)/16)]` |

**Anti-Patterns**
- ❌ Same `w-1/4` on all rows regardless of span
- ❌ Hardcoded pixel widths (`w-32`, `w-28`)
- ❌ Nested grids to fake alignment

---

### 3. Error Message Display

**Floating Error Pattern (Recommended)**
- Error messages float **outside** the form flow
- No layout shift when errors appear/disappear
- Uses absolute positioning relative to field container

```html
<div class="relative">
  <input class="w-full">
  <div class="absolute -bottom-5 left-0 text-xs text-red-500">
    Error message here
  </div>
</div>
```

**Why Not Inline Errors?**
- Inline errors push content down
- Form "jumps" when errors appear
- Poor user experience on validation
- Breaks visual rhythm

**Error Positioning Rules**
- Position: `absolute` with `bottom: -20px`
- Color: `text-red-500` or semantic error token
- Font size: `text-xs` (12px)
- Do NOT add margin to error container

---

### 4. Complex Control Support

**Tree Selector**
```
Field markers: TYP(TRE), REL(parent.id), MTO(category)
Trigger: field name contains 'tree', 'org', 'department'
```
- Opens modal with hierarchical tree
- Single or multi-select modes
- Search within tree structure

**Cascading Selector**
```
Field markers: TYP(CAS), GEO(PLY)
Trigger: field name contains 'region', 'category_path'
```
- Multi-level dropdown (e.g., Country > Province > City)
- Lazy loading for large datasets

**Modal List Picker**
```
Field markers: TYP(SEL) + large dataset indicator
Trigger: field comment contains '弹框', '选择'
```
- Opens modal with searchable list
- Pagination support
- Multi-select with checkboxes

**Address Picker**
```
Field markers: TYP(MAP), GEO(PNT)
Trigger: field name contains 'address', 'location'
```
- Map integration for coordinate selection
- Address autocomplete
- Reverse geocoding display

**Table Editor**
```
Field markers: TYP(TAB), ARR, SUB(detailTable)
Trigger: field name contains 'items', 'details', 'grid'
```
- Inline row editing
- Add/remove rows
- Cell validation

---

### 5. Attribute Marker Integration

Markers from `r2mo-spec/marker.md` control form behavior:

| Marker | Form Impact |
|--------|-------------|
| `REQ` | Required field, show asterisk |
| `NUL` | Optional field, nullable |
| `LEN(min,max)` | Input maxlength, validation |
| `TYP(control)` | Explicit control type |
| `VIS` | Visible in list view (field priority) |
| `HID` | Hidden field, excluded from form |
| `DEF(value)` | Default value |
| `RNG(min,max)` | Numeric range validation |
| `PAT(regex)` | Pattern validation |
| `REL(table.id)` | Relation selector |
| `FLO` | Real-time field, live updates |

**Marker Processing Flow**
1. Parse markers from field definition
2. `TYP()` → explicit control type
3. `REQ` → required asterisk + validation
4. `LEN()` → maxlength attribute
5. `HID` → skip field in form
6. `DEF()` → prefill default value

---

### 6. Responsive Behavior

**Breakpoint Strategy**
- Default: Mobile-first single column
- `md` (768px): 2-column grid
- `lg` (1024px): 3-4 column grid

**Responsive Grid**
```html
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
  <!-- Fields -->
</div>
```

**Label Width Adaptation**
- Mobile: Full-width labels above inputs
- Desktop: Inline labels with fixed fraction

---

### 7. Visual Hierarchy

**Typography Scale**
| Element | Size | Weight | Class |
|---------|------|--------|-------|
| Page title | 24px | 600 | `text-2xl font-semibold` |
| Section title | 18px | 500 | `text-lg font-medium` |
| Label | 14px | 500 | `text-sm font-medium` |
| Input text | 14px | 400 | `text-sm` |
| Helper text | 12px | 400 | `text-xs text-gray-500` |
| Error text | 12px | 400 | `text-xs text-red-500` |

**Color Tokens**
```css
--primary-500: /* Brand primary */
--primary-600: /* Hover state */
--gray-300: /* Border */
--gray-500: /* Label */
--gray-700: /* Text */
--red-500: /* Error */
--green-500: /* Success */
```

---

### 8. Accessibility

**Required Practices**
- All inputs have associated `<label>`
- Required fields marked with `aria-required="true"`
- Error messages linked via `aria-describedby`
- Focus visible: `focus:ring-2`
- Keyboard navigation: Tab order follows visual order

**ARIA Example**
```html
<label for="email">Email <span class="text-red-500">*</span></label>
<input 
  type="email" 
  id="email" 
  required
  aria-required="true"
  aria-describedby="email-error"
>
<span id="email-error" class="text-xs text-red-500 hidden">
  Please enter a valid email
</span>
```

---

### 9. Performance Considerations

**Lazy Load Complex Controls**
- Tree/Modal selectors: Load data on open
- Rich text editors: Initialize on focus
- Map pickers: Defer map SDK load

**Field Count Limits**
- Single form: ≤ 30 fields optimal
- > 30 fields: Use tabs or wizard pattern
- Sub-forms for detail tables

---

### 10. Common Pitfalls

| Pitfall | Solution |
|---------|----------|
| Label misalignment | Use calc() formula for full-width rows |
| Form jumps on error | Float errors outside form flow |
| Overlapping containers | Use consistent padding hierarchy |
| Hardcoded widths | Use Tailwind arbitrary values |
| Missing required mark | Parse REQ marker, show asterisk |
| Slow large selects | Use modal picker + pagination |

---

## Quick Reference

**Golden Ratio Spacing**
```
8px → 12px → 16px → 24px → 32px → 48px
```

**Label Width Formula**
```
w-[calc((100%-gap×(cols-1))/cols/fraction)]
```

**Error Positioning**
```
absolute, -bottom-5, left-0
```

**Container Padding**
```
Form: 24px
Section: 16px
Gap: 24px
```