---
name: mxt-ui-form
description: |
  Enterprise form generation skill for B-side applications.
  
  **This skill is a thinking guide, not a template generator.**
  Read the design philosophy, understand the context, then create with intention.
  
  Capabilities:
  - Generate 1-4 column grid forms with thoughtful spacing
  - Infer control types from field metadata (35 control types)
  - Support complex controls (tree, cascader, table editor, map)
  - Floating error messages (no layout shift)
  - Attribute marker integration
  
  Triggers: "form design", "form layout", "generate form", "表单设计", "表单布局", "生成表单"
---

# mxt-ui-form — Design with Intention

## Core Philosophy

**A form is a conversation between the user and the system.**

Good form design is not about filling space—it's about creating a natural flow that guides users through their task with minimal friction. Every spacing decision, every alignment, every width choice sends a message.

---

## Design Principles (The Soul of Form Design)

### 1. The Principle of Comfortable Tension

**What it means**: Elements should be close enough to show relationship, but not so close they feel cramped. Not too far apart that they seem disconnected.

**The Feeling**:
- ❌ **Too tight**: Feels claustrophobic, stressful, hard to scan
- ✅ **Just right**: Feels breathable, organized, easy to process
- ❌ **Too loose**: Feels disconnected, wasteful, hard to see relationships

**Apply this to**:
- Label-to-input gap: Should feel like they "belong together" (8-16px)
- Row spacing: Should clearly separate distinct fields (16-24px)
- Section spacing: Should create visual breathing room (32-48px)

```
┌─────────────────────────────────────────────────────────────────┐
│  TOO TIGHT          JUST RIGHT              TOO LOOSE           │
│                                                                   │
│  Label:[___]        Label: [_______]        Label:        [___] │
│  Label:[___]        Label: [_______]        Label:        [___] │
│  Label:[___]        Label: [_______]        Label:        [___] │
│                                                                   │
│  (cramped)          (comfortable)          (disconnected)       │
└─────────────────────────────────────────────────────────────────┘
```

### 2. The Principle of Visual Weight

**What it means**: Labels and inputs should feel visually balanced. The input area should feel substantial enough for the expected content.

**The Feeling**:
- Label too narrow → feels squeezed, hard to read
- Label too wide → wastes space, input feels cramped
- Input too narrow → empty, unsatisfied
- Input too wide → intimidating, overwhelming

**Balance Rules**:
- Short labels (2-4 chars): ~15-20% width
- Medium labels (5-8 chars): ~20-25% width
- Long labels (9+ chars): Consider top-aligned labels

```
┌─────────────────────────────────────────────────────────────────┐
│  Label Width Balance:                                            │
│                                                                   │
│  ✗ Name:      [__________]     ← too much gap, feels loose      │
│  ✗ Description: [____]         ← label longer than input!        │
│  ✓ 姓名:     [_______________] ← balanced, feels right           │
│  ✓ 部门:     [_______________] ← comfortable ratio               │
└─────────────────────────────────────────────────────────────────┘
```

### 3. The Principle of Vertical Alignment

**What it means**: The baseline of label text and input text should align naturally. Labels shouldn't float above or sink below inputs.

**The Problem**: When label is `text-right` and input has padding, they often misalign visually.

**The Fix**: Center-align vertically within the row height.

```
┌─────────────────────────────────────────────────────────────────┐
│  WRONG                          RIGHT                           │
│                                                                   │
│  登录账号:                     登录账号:                         │
│  [____________]  ← label       [____________]  ← aligned         │
│           ↑                                ↑                     │
│  floating above                  same baseline                   │
└─────────────────────────────────────────────────────────────────┘
```

**Implementation**:
- Use `items-center` (not `items-start`) for single-line fields
- Use `items-start` only for textareas and tall controls
- Account for input padding in visual calculation

### 4. The Principle of Generous Inputs

**What it means**: Inputs should feel inviting, not cramped. A narrow input feels like the system doesn't trust the user's input.

**The Feeling**:
- Narrow input: "I don't expect you'll write much"
- Wide input: "Tell me what you need to say"

**Practical Rules**:
- Minimum comfortable input width: 200px (or 60% of available space)
- For grid cells: Input should take remaining space after label (`flex-1`)
- Never let input feel "squeezed" by the label

### 5. The Principle of Consistent Rhythm

**What it means**: Spacing should follow a deliberate pattern that creates visual rhythm.

**The Pattern** (8px base):
```
┌─────────────────────────────────────────────────────────────────┐
│  Level      | Spacing | Usage                                   │
│  ─────────────────────────────────────────────────────────────  │
│  Tight      | 8px     | Within control (icon to text)           │
│  Close      | 12px    | Label to input gap                      │
│  Standard   | 16px    | Between fields in a group               │
│  Breathing  | 24px    | Between groups/sections                 │
│  Spacious   | 32px    | Major section breaks                    │
│  Generous   | 48px    | Page-level margins                      │
└─────────────────────────────────────────────────────────────────┘
```

**Why it matters**: Consistent rhythm reduces cognitive load. Users subconsciously learn the pattern and navigate faster.

### 6. The Principle of Responsive Generosity

**What it means**: More screen space = more generous spacing. Don't keep the same cramped layout on large screens.

**The Adaptation**:
```
Mobile (< 640px):  Single column, minimal padding, dense
Tablet (640-1024): 2 columns, comfortable padding
Desktop (> 1024):  2-3 columns, generous spacing
Large (> 1440):     Consider max-width to prevent over-stretching
```

---

## Common Problems & How to Think About Them

### Problem: Labels and inputs don't align horizontally

**Think**: What's causing the misalignment?
- Is the label text baseline different from input text?
- Does the input have padding that shifts its visual center?
- Are they in the same flex container?

**Solution Pattern**:
```html
<div class="flex items-center gap-3">
  <label class="w-24 text-right text-sm shrink-0">姓名:</label>
  <input class="flex-1 px-3 py-2 border rounded">
</div>
```

Key: `items-center` aligns the centers, not the tops.

### Problem: Input feels too narrow

**Think**: What percentage of the row does the input occupy?
- If label is 25%, input should be ~70% (with 5% gap)
- Does the input width match the expected content length?
- Is there wasted space in the row?

**Solution Pattern**:
- Use `flex-1` on input to take remaining space
- Avoid fixed widths on inputs
- Consider reducing label width if input needs more space

### Problem: Form feels cramped or cluttered

**Think**: Where is the tension too tight?
- Between label and input? → Increase gap from 8px to 12-16px
- Between rows? → Increase from 16px to 24px
- Between sections? → Add explicit section breaks

**Solution Pattern**:
```html
<!-- Each row has breathing room -->
<div class="flex items-center gap-4 mb-4">
  <!-- gap-4 = 16px between label and input -->
  <!-- mb-4 = 16px below each row -->
</div>
```

### Problem: Form feels too empty or sparse

**Think**: Where is the wasted space?
- Is the form container too wide? → Add max-width
- Is the gap between fields too large? → Reduce from 24px to 16px
- Are there too many columns for the content? → Reduce columns

**Solution Pattern**:
```html
<form class="max-w-3xl mx-auto">
  <!-- Constrained width prevents over-stretching -->
</form>
```

---

## Layout Decision Tree

```
START
  │
  ├─ How many fields? (< 6) ──────────────► 1-2 columns
  │                                      
  ├─ How many fields? (6-12) ─────────────► 2 columns
  │                                      
  └─ How many fields? (> 12) ─────────────► 2 columns + sections
                                           OR wizard flow
  │
  ▼
What's the content type?
  │
  ├─ Short entries (names, codes) ────────► Narrower inputs OK
  │
  ├─ Medium entries (emails, phones) ─────► Standard width
  │
  └─ Long entries (descriptions) ─────────► Full-width textareas
  │
  ▼
What's the screen context?
  │
  ├─ Mobile ──────────────────────────────► 1 column, compact
  │
  ├─ Tablet ──────────────────────────────► 2 columns, balanced
  │
  └─ Desktop ─────────────────────────────► 2-3 columns, generous
```

---

## Technical Implementation Guide

### Label Width Formula

**For grid cells**: Use simple fractions
```html
<!-- 2-column grid, each cell is independent -->
<label class="w-1/4">Name:</label>  <!-- 25% of cell width -->
<input class="flex-1">              <!-- 75% of cell width -->
```

**For full-width rows**: Use calc to match grid label pixels
```html
<!-- Full-width row after 2-column grid -->
<label class="w-[calc((100%-2rem)/8)]">Notes:</label>
<!-- This matches the pixel width of w-1/4 in a grid cell -->
```

### Vertical Alignment Fix

```html
<!-- Single-line fields: center align -->
<div class="flex items-center gap-3">
  <label>Field:</label>
  <input>
</div>

<!-- Multi-line fields: top align -->
<div class="flex items-start gap-3">
  <label class="pt-2">Notes:</label>  <!-- pt-2 aligns with first line -->
  <textarea rows="4"></textarea>
</div>
```

### Input Width Optimization

```html
<!-- Bad: Input feels squeezed -->
<div class="flex">
  <label class="w-40">Description:</label>  <!-- too wide -->
  <input class="w-32">                      <!-- too narrow -->
</div>

<!-- Good: Input dominates the space -->
<div class="flex items-center gap-3">
  <label class="w-24 shrink-0">描述:</label>
  <input class="flex-1 min-w-48">           <!-- takes remaining space -->
</div>
```

---

## Anti-Patterns (What NOT to Do)

| Anti-Pattern | Why It Fails | The Fix |
|--------------|--------------|---------|
| `items-start` on all rows | Labels float above inputs | Use `items-center` for single-line fields |
| Label width > input width | Inverted visual hierarchy | Label should be ≤ 25% of row width |
| Fixed input widths | Doesn't adapt to content | Use `flex-1` for fluid sizing |
| Same gap everywhere | No visual hierarchy | Use 8-16-24-32px progression |
| Max-width: 100% on form | Over-stretched on large screens | Use max-w-3xl or max-w-4xl |
| No max-width | Form spreads too thin | Constrain for comfortable reading |

---

## Validation Checklist

After designing a form, ask:

**Visual Balance**:
- [ ] Does the label:input ratio feel balanced (≈25:75)?
- [ ] Is the input width comfortable for expected content?
- [ ] Do labels and inputs align at the same visual level?

**Spacing Rhythm**:
- [ ] Is there consistent gap between label and input?
- [ ] Is there breathing room between rows?
- [ ] Are sections clearly separated?

**Feeling**:
- [ ] Does the form feel cramped anywhere?
- [ ] Does it feel sparse anywhere?
- [ ] Is there a natural visual flow from top to bottom?

**Technical**:
- [ ] Are you using `items-center` for single-line fields?
- [ ] Are you using `flex-1` for inputs?
- [ ] Is there a max-width constraint on the form?

---

## Tools

### form_calc.py
Label width calculator for pixel-perfect alignment in mixed layouts.

### control_infer.py
Control type inference from field metadata.

### form_validate.py
Layout and accessibility validation checker.

### form_gen.py
Starter template generator. **Use as starting point, not final output.**

---

## Remember

> "The details are not the details. They make the design." — Charles Eames

Every pixel of spacing, every alignment decision, every width choice is an opportunity to show users you care about their experience. A form that feels right is a form that users will complete without frustration.

**Design with intention. Question every default. Create with empathy.**