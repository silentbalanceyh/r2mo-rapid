# Control Type Mapping Reference

Complete mapping from field metadata to UI control types.

## Control Type Index (35 Types)

| Code | Name | HTML Element | Use Case |
|------|------|--------------|----------|
| TEX | Text | `<input type="text">` | Short text, names, codes |
| TXA | Textarea | `<textarea>` | Long text, descriptions |
| PAS | Password | `<input type="password">` | Passwords, secrets |
| EMA | Email | `<input type="email">` | Email addresses |
| NUM | Number | `<input type="number">` | Quantities, ages, prices |
| DAT | Date | `<input type="date">` | Birthdays, deadlines |
| DTM | DateTime | `<input type="datetime-local">` | Timestamps, schedules |
| TIM | Time | `<input type="time">` | Hours, time ranges |
| SEL | Select | `<select>` | Single choice dropdown |
| MUL | Multi-select | `<select multiple>` | Multiple choices |
| RAD | Radio | `<input type="radio">` | Exclusive options (2-5) |
| CHK | Checkbox | `<input type="checkbox">` | Boolean yes/no |
| CHG | Checkbox Group | Multiple checkboxes | Multiple booleans |
| SWI | Switch | Custom toggle | Enable/disable states |
| SLI | Slider | `<input type="range">` | Numeric ranges |
| MAP | Map | Custom map widget | Geographic coordinates |
| IMG | Image | `<input type="file" accept="image/*">` | Avatar, logo, photo |
| FIL | File | `<input type="file">` | Documents, attachments |
| VID | Video | `<input type="file" accept="video/*">` | Video files |
| AUD | Audio | `<input type="file" accept="audio/*">` | Audio files |
| RTE | Rich Text | Custom WYSIWYG | Article content |
| MDE | Markdown | Custom editor | Documentation |
| COD | Code | Custom editor | Scripts, code snippets |
| CLR | Color | `<input type="color">` | Theme colors |
| RAT | Rating | Custom stars/icons | Scores, reviews |
| TAG | Tags | Custom component | Keywords, labels |
| CAS | Cascader | Custom cascader | Hierarchical selection |
| TRE | Tree | Custom tree selector | Organizations, folders |
| TAB | Table | Custom grid | Detail lines, schedules |
| JSN | JSON | Custom editor | Configuration objects |
| ICO | Icon | Custom picker | Icon selection |
| SGN | Signature | Custom canvas | Digital signatures |
| QRC | QR Code | Custom component | QR generation/scanning |
| BAR | Barcode | Custom component | Barcode scanning |

---

## Inference Rules

### Priority Order
1. Explicit `TYP()` marker
2. Field name pattern
3. Comment annotation
4. Data type mapping
5. Default: TEX

### Field Name Patterns

#### Authentication
```python
r'password|pwd|passwd' → PAS
r'confirm_password|confirm_pwd' → PAS
```

#### Contact
```python
r'email|mail|e_mail' → EMA
r'phone|mobile|tel|telephone' → TEX
```

#### Text Content
```python
r'description|desc|remark|note|comment|content|body' → TXA
r'bio|introduction|summary|abstract' → TXA
r'address|addr' → TXA
```

#### Names
```python
r'username|user_name|login_name' → TEX
r'realname|real_name|full_name|display_name' → TEX
r'nickname|nick_name|alias' → TEX
```

#### Numeric
```python
r'age' → NUM
r'count|quantity|qty|amount|number|num' → NUM
r'price|cost|fee|total|subtotal' → NUM
r'discount|rate|percent|percentage' → NUM
r'weight|height|width|length|size' → NUM
```

#### Date/Time
```python
r'birthday|birth_date|birthdate' → DAT
r'create_time|created_at|create_date' → DTM
r'update_time|updated_at|update_date' → DTM
r'start_time|end_time|begin_time|finish_time' → DTM
r'date|datetime|timestamp' → DTM
r'time$' → TIM
```

#### Boolean
```python
r'is_|has_|can_|should_' → CHK
r'enabled|disabled|active|inactive|visible|hidden' → SWI
r'published|approved|verified' → SWI
r'agree|accept|consent' → CHK
```

#### Selection
```python
r'gender|sex' → RAD
r'status|state' → SEL
r'type|category|kind' → SEL
r'level|grade|rank|priority' → SEL
r'country|province|city|region|district' → CAS
```

#### Multi-value
```python
r'tags|keywords|labels' → TAG
r'roles|permissions|features' → CHG
```

#### Media
```python
r'avatar|photo|image|picture|logo|icon|thumbnail' → IMG
r'video|video_url|clip' → VID
r'audio|voice|sound|recording' → AUD
r'file|attachment|document' → FIL
```

#### Geographic
```python
r'location|coordinate|latitude|longitude|geo|position' → MAP
```

#### Rich Content
```python
r'content|article|body|html' → RTE
r'markdown|md' → MDE
r'code|script|snippet|source' → COD
r'config|settings|metadata|json' → JSN
```

#### Hierarchical
```python
r'parent_id|parent' → TRE
r'tree|organization|org|department|dept' → TRE
r'category_tree|category_path' → CAS
```

#### Table/Grid
```python
r'items|details|rows|grid|matrix|schedule' → TAB
```

---

## Data Type Mapping

| SQL Type | Control Type | Reasoning |
|----------|--------------|-----------|
| VARCHAR | TEX | Short text |
| CHAR | TEX | Fixed short text |
| TEXT | TXA | Long text |
| LONGTEXT | RTE | Very long text |
| INT | NUM | Integer |
| BIGINT | NUM | Large integer |
| DECIMAL | NUM | Decimal number |
| FLOAT | NUM | Floating point |
| BIT | CHK | Boolean bit |
| BOOLEAN | SWI | Boolean toggle |
| DATE | DAT | Date only |
| DATETIME | DTM | Date and time |
| TIMESTAMP | DTM | Unix timestamp |
| TIME | TIM | Time only |

---

## Marker Integration

### Required Markers
```
markers: [REQ, LEN(1,100)]
→ Input with required validation, maxlength="100"
```

### Control Type Override
```
markers: [TYP(TRE), REQ, REL(org.id)]
→ Tree selector, required, relation to org
```

### Hidden Fields
```
markers: [HID]
→ Skip in form, include as hidden input
```

### Default Values
```
markers: [DEF(true), TYP(SWI)]
→ Toggle switch, default ON
```

### Validation Rules
```
markers: [REQ, LEN(6,20), PAT(^[a-zA-Z0-9_]+$)]
→ Required, 6-20 chars, alphanumeric pattern
```

### Numeric Range
```
markers: [RNG(0,100), TYP(SLI)]
→ Slider with 0-100 range
```

---

## Complex Control Detection

### Tree Selector
- Explicit: `TYP(TRE)`
- Implicit: `parent_id`, `org_id`, `dept_id` + relation marker
- Requires: `REL()` marker for data source

### Cascading Selector
- Explicit: `TYP(CAS)`
- Implicit: Region/address fields
- Requires: Hierarchical data source

### Table Editor
- Explicit: `TYP(TAB)`
- Implicit: Field name contains `items`, `details`
- Requires: `ARR` marker, sub-form definition

### Map Picker
- Explicit: `TYP(MAP)`
- Implicit: `location`, `coordinate`, `latitude`
- Requires: `GEO(PNT)` marker

---

## Control Selection Decision Tree

```
Is TYP marker set?
├─ Yes → Use explicit control
└─ No → Check field name
    ├─ Password field? → PAS
    ├─ Email field? → EMA
    ├─ Date field? → DAT/DTM
    ├─ Boolean field? → CHK/SWI
    ├─ Selection field?
    │   ├─ 2-5 options? → RAD
    │   ├─ 5-20 options? → SEL
    │   └─ 20+ options? → SEL + search/modal
    ├─ Multi-value? → MUL/CHG/TAG
    ├─ Long text? → TXA/RTE
    ├─ File upload? → FIL/IMG/VID/AUD
    └─ Default → TEX
```

---

## Example Mappings

### User Registration Form
| Field | Type | Inferred Control |
|-------|------|------------------|
| username | VARCHAR(50) | TEX |
| email | VARCHAR(100) | EMA |
| password | VARCHAR(255) | PAS |
| gender | ENUM | RAD |
| birth_date | DATE | DAT |
| avatar | VARCHAR(255) | IMG |
| bio | TEXT | TXA |
| agree_terms | BOOLEAN | CHK |

### Product Form
| Field | Type | Inferred Control |
|-------|------|------------------|
| name | VARCHAR(200) | TEX |
| description | TEXT | RTE |
| price | DECIMAL(10,2) | NUM |
| category_id | VARCHAR(36) | TRE |
| tags | JSON | TAG |
| images | JSON | IMG (multiple) |
| status | ENUM | SEL |
| is_featured | BOOLEAN | SWI |

### Order Form
| Field | Type | Inferred Control |
|-------|------|------------------|
| order_no | VARCHAR(50) | TEX (readonly) |
| customer_id | VARCHAR(36) | SEL (search) |
| order_date | DATETIME | DTM |
| status | ENUM | SEL |
| items | JSON | TAB |
| notes | TEXT | TXA |