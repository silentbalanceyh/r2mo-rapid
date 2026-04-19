#!/usr/bin/env python3
"""
Enterprise Form Generator for Tailwind CSS.

Generates production-ready HTML forms from data model definitions with:
- Dynamic 1-4 column grid layouts
- Pixel-perfect label alignment
- Floating error messages (no form shake)
- Complex control support (tree, modal, table editor)
- Golden ratio spacing
- Attribute marker integration (REQ, NUL, LEN, etc.)
"""

import argparse
import json
from dataclasses import dataclass, field
from typing import Optional, List
from pathlib import Path

# Import from sibling modules
import sys

sys.path.insert(0, str(Path(__file__).parent))

from control_infer import ControlType, infer_control_type, FieldMeta
from form_calc import calc_label_width


# Golden ratio spacing constants (base: 8px)
SPACING = {
    "xs": "0.5rem",  # 8px
    "sm": "0.75rem",  # 12px
    "md": "1rem",  # 16px
    "lg": "1.5rem",  # 24px
    "xl": "2rem",  # 32px
    "2xl": "3rem",  # 48px
}

GAP_DEFAULT = 8  # gap-8 = 2rem
LABEL_FRACTION = "1/4"


@dataclass
class FormField:
    """Form field configuration."""

    name: str
    label: str
    control_type: ControlType
    required: bool = False
    nullable: bool = True
    max_length: Optional[int] = None
    placeholder: Optional[str] = None
    help_text: Optional[str] = None
    column_span: int = 1  # 1-4 columns
    options: list = field(default_factory=list)  # For SEL, RAD, CHK
    validation: dict = field(default_factory=dict)
    default_value: Optional[str] = None
    disabled: bool = False
    readonly: bool = False


@dataclass
class FormConfig:
    """Form configuration."""

    title: str = ""
    columns: int = 2  # 1-4 columns
    gap: int = 8  # Tailwind gap-4/6/8
    label_fraction: str = "1/4"
    submit_text: str = "Submit"
    cancel_text: str = "Cancel"
    method: str = "POST"
    action: str = ""
    floating_errors: bool = True  # Error messages float outside form


def generate_label(
    field: FormField, config: FormConfig, is_full_width: bool = False
) -> str:
    """Generate label HTML with proper width class.

    Key insight:
    - Grid cells use simple fraction (w-1/4) - 25% of cell width
    - Full-width rows use calc() to match effective pixel width of grid labels
    """
    label_text = field.label or field.name.replace("_", " ").title()
    required_mark = '<span class="text-red-500 ml-1">*</span>' if field.required else ""

    if is_full_width:
        # Full-width row: use calc() to match grid cell label's effective pixel width
        gap_rem = {4: 1.0, 6: 1.5, 8: 2.0}[config.gap]
        width_class = calc_label_width(
            config.columns, gap_rem, config.label_fraction, row_span=0
        )
    else:
        # Grid cell: use simple fraction (Tailwind handles this correctly)
        num, den = config.label_fraction.split("/")
        width_class = f"w-{num}/{den}"

    return f'''<label class="{width_class} text-right pr-4 shrink-0 text-sm font-medium text-gray-700">
        {label_text}{required_mark}
    </label>'''


def generate_input(field: FormField) -> str:
    """Generate input control HTML based on control type."""
    ctrl = field.control_type
    name = field.name
    placeholder = field.placeholder or field.label or ""
    required = "required" if field.required else ""
    disabled = "disabled" if field.disabled else ""
    readonly = "readonly" if field.readonly else ""

    # Base input classes - using w-full for proper width in flex container
    base_classes = "w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500 text-sm"

    # Text inputs
    if ctrl == ControlType.TEX:
        max_len = f'maxlength="{field.max_length}"' if field.max_length else ""
        return f'<input type="text" name="{name}" id="{name}" class="{base_classes}" placeholder="{placeholder}" {max_len} {required} {disabled} {readonly}>'

    elif ctrl == ControlType.TXA:
        # Textarea: more rows, resize disabled, full-width behavior
        rows = min(6, max(3, (field.max_length or 200) // 50))
        textarea_classes = f"{base_classes} resize-none min-h-[80px]"
        return f'<textarea name="{name}" id="{name}" rows="{rows}" class="{textarea_classes}" placeholder="{placeholder}" {required} {disabled} {readonly}></textarea>'

    elif ctrl == ControlType.PAS:
        return f'<input type="password" name="{name}" id="{name}" class="{base_classes}" placeholder="{placeholder}" {required} {disabled}>'

    elif ctrl == ControlType.EMA:
        return f'<input type="email" name="{name}" id="{name}" class="{base_classes}" placeholder="{placeholder}" {required} {disabled}>'

    elif ctrl == ControlType.NUM:
        return f'<input type="number" name="{name}" id="{name}" class="{base_classes}" placeholder="{placeholder}" {required} {disabled}>'

    # Date/Time
    elif ctrl == ControlType.DAT:
        return f'<input type="date" name="{name}" id="{name}" class="{base_classes}" {required} {disabled}>'

    elif ctrl == ControlType.DTM:
        return f'<input type="datetime-local" name="{name}" id="{name}" class="{base_classes}" {required} {disabled}>'

    elif ctrl == ControlType.TIM:
        return f'<input type="time" name="{name}" id="{name}" class="{base_classes}" {required} {disabled}>'

    # Selection - with custom dropdown arrow
    elif ctrl == ControlType.SEL:
        options_html = (
            "\n".join(
                [
                    f'<option value="{opt.get("value", "")}">{opt.get("label", "")}</option>'
                    for opt in field.options
                ]
            )
            if field.options
            else '<option value="">Please select</option>'
        )
        # Custom select with appearance-none and proper padding for dropdown arrow
        select_classes = f"{base_classes} appearance-none bg-white pr-10 cursor-pointer"
        return f'''<div class="relative">
            <select name="{name}" id="{name}" class="{select_classes}" {required} {disabled}>
                {options_html}
            </select>
            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-3">
                <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
                </svg>
            </div>
        </div>'''

    elif ctrl == ControlType.MUL:
        options_html = (
            "\n".join(
                [
                    f'<option value="{opt.get("value", "")}">{opt.get("label", "")}</option>'
                    for opt in field.options
                ]
            )
            if field.options
            else ""
        )
        return f'''<select name="{name}" id="{name}" multiple class="{base_classes} min-h-[100px]" {required} {disabled}>
            {options_html}
        </select>'''

    elif ctrl == ControlType.RAD:
        options_html = (
            "\n".join(
                [
                    f'''<label class="inline-flex items-center mr-4">
                <input type="radio" name="{name}" value="{opt.get("value", "")}" class="form-radio" {required} {disabled}>
                <span class="ml-2 text-sm">{opt.get("label", "")}</span>
            </label>'''
                    for opt in field.options
                ]
            )
            if field.options
            else ""
        )
        return f'<div class="flex flex-wrap">{options_html}</div>'

    elif ctrl == ControlType.CHK:
        checked = "checked" if field.default_value == "true" else ""
        return f'''<label class="inline-flex items-center">
            <input type="checkbox" name="{name}" id="{name}" class="form-checkbox h-4 w-4 text-primary-600" {checked} {disabled}>
            <span class="ml-2 text-sm text-gray-700">{field.label}</span>
        </label>'''

    elif ctrl == ControlType.SWI:
        checked = "checked" if field.default_value == "true" else ""
        return f'''<label class="relative inline-flex items-center cursor-pointer">
            <input type="checkbox" name="{name}" id="{name}" class="sr-only peer" {checked} {disabled}>
            <div class="w-11 h-6 bg-gray-200 peer-focus:ring-4 peer-focus:ring-primary-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
        </label>'''

    # Complex controls (placeholders for framework-specific implementation)
    elif ctrl == ControlType.TRE:
        return f'''<div class="form-control-complex" data-control="tree" data-name="{name}">
            <input type="text" name="{name}" id="{name}" class="{base_classes}" placeholder="Click to select from tree..." readonly {required} {disabled}>
            <div class="tree-selector-modal hidden" data-target="{name}"></div>
        </div>'''

    elif ctrl == ControlType.CAS:
        return f'''<div class="form-control-complex" data-control="cascader" data-name="{name}">
            <input type="text" name="{name}" id="{name}" class="{base_classes}" placeholder="Select category..." readonly {required} {disabled}>
        </div>'''

    elif ctrl == ControlType.MAP:
        return f'''<div class="form-control-complex" data-control="map-picker" data-name="{name}">
            <input type="text" name="{name}_display" id="{name}_display" class="{base_classes}" placeholder="Select location..." readonly {required} {disabled}>
            <input type="hidden" name="{name}" id="{name}">
        </div>'''

    elif ctrl == ControlType.TAB:
        return f'''<div class="form-control-complex border rounded-md p-4" data-control="table-editor" data-name="{name}">
            <div class="table-editor-toolbar mb-2">
                <button type="button" class="btn-add-row px-3 py-1 text-sm bg-primary-500 text-white rounded">Add Row</button>
            </div>
            <div class="table-editor-content min-h-[150px]">
                <!-- Dynamic table rows -->
            </div>
        </div>'''

    elif ctrl == ControlType.IMG:
        return f'''<div class="form-control-complex" data-control="image-upload" data-name="{name}">
            <input type="file" name="{name}" id="{name}" accept="image/*" class="hidden" {required} {disabled}>
            <div class="image-preview w-24 h-24 border-2 border-dashed border-gray-300 rounded-md flex items-center justify-center cursor-pointer hover:border-primary-500">
                <span class="text-gray-400 text-sm">Upload</span>
            </div>
        </div>'''

    elif ctrl == ControlType.FIL:
        return f'''<div class="form-control-complex" data-control="file-upload" data-name="{name}">
            <input type="file" name="{name}" id="{name}" class="{base_classes}" {required} {disabled}>
        </div>'''

    elif ctrl == ControlType.RTE:
        return f'''<div class="form-control-complex" data-control="rich-editor" data-name="{name}">
            <div id="{name}_editor" class="min-h-[200px] border rounded-md p-3"></div>
            <input type="hidden" name="{name}" id="{name}">
        </div>'''

    elif ctrl == ControlType.TAG:
        return f'''<div class="form-control-complex" data-control="tag-input" data-name="{name}">
            <div class="tag-container flex flex-wrap gap-2 p-2 border rounded-md min-h-[42px]">
                <input type="text" class="tag-input flex-1 border-none outline-none text-sm" placeholder="Add tag...">
            </div>
            <input type="hidden" name="{name}" id="{name}">
        </div>'''

    elif ctrl == ControlType.RAT:
        return f'''<div class="form-control-complex" data-control="rating" data-name="{name}">
            <div class="rating-stars flex gap-1">
                {[1, 2, 3, 4, 5].map(lambda i: f'<button type="button" class="star text-2xl text-gray-300 hover:text-yellow-400" data-value="{i}">★</button>')}
            </div>
            <input type="hidden" name="{name}" id="{name}">
        </div>'''

    elif ctrl == ControlType.CLR:
        return f'<input type="color" name="{name}" id="{name}" class="w-16 h-10 rounded cursor-pointer" {required} {disabled}>'

    else:
        # Default to text input
        return f'<input type="text" name="{name}" id="{name}" class="{base_classes}" placeholder="{placeholder}" {required} {disabled}>'


def generate_error_container(field: FormField, config: FormConfig) -> str:
    """Generate floating error message container."""
    if not config.floating_errors:
        return ""

    return f'''<div class="error-message absolute -bottom-5 left-0 text-xs text-red-500 hidden" data-field="{field.name}">
        <!-- Error message injected here -->
    </div>'''


def generate_field_row(
    field: FormField, config: FormConfig, is_full_width: bool = False
) -> str:
    """Generate a single form field row."""
    label = generate_label(field, config, is_full_width)
    input_html = generate_input(field)
    error_html = generate_error_container(field, config)

    row_classes = "relative flex items-start"
    if is_full_width:
        row_classes += " mt-4"

    return f'''<div class="{row_classes}">
        {label}
        <div class="flex-1 relative">
            {input_html}
            {error_html}
        </div>
    </div>'''


def generate_form(fields: List[FormField], config: FormConfig) -> str:
    """
    Generate complete form HTML.

    Layout strategy:
    - Group fields by column span
    - Create grid sections for uniform column spans
    - Handle full-width fields (span >= columns) separately
    """
    gap_class = f"gap-{config.gap}"
    gap_rem = {4: 1.0, 6: 1.5, 8: 2.0}[config.gap]

    html_parts = []

    # Form container with proper padding
    html_parts.append(
        f'''<form class="form-container p-6 bg-white rounded-lg shadow-md" method="{config.method}" action="{config.action}">'''
    )

    # Title
    if config.title:
        html_parts.append(
            f"""<h2 class="text-xl font-semibold text-gray-900 mb-6">{config.title}</h2>"""
        )

    # Group fields by layout
    grid_fields = []  # Fields that fit in grid
    full_width_fields = []  # Fields that span full width

    for f in fields:
        if f.column_span >= config.columns:
            full_width_fields.append(f)
        else:
            grid_fields.append(f)

    # Generate grid section
    if grid_fields:
        html_parts.append(
            f"""<div class="grid grid-cols-{config.columns} {gap_class}">"""
        )

        for f in grid_fields:
            label = generate_label(f, config, is_full_width=False)
            input_html = generate_input(f)
            error_html = generate_error_container(f, config)

            # Determine alignment: center for single-line, start for multi-line
            is_multiline = f.control_type in [
                ControlType.TXA,
                ControlType.TAB,
                ControlType.RTE,
            ]
            align_class = "items-start" if is_multiline else "items-center"

            # Grid cell directly contains flex layout
            col_span_class = f"col-span-{f.column_span}" if f.column_span > 1 else ""
            cell_class = (
                f"{col_span_class} relative flex {align_class} gap-3"
                if col_span_class
                else f"relative flex {align_class} gap-3"
            )

            html_parts.append(f'''<div class="{cell_class}">
        {label}
        <div class="flex-1 relative min-w-0">
            {input_html}
            {error_html}
        </div>
    </div>''')

        html_parts.append("""</div>""")

    # Generate full-width fields (outside grid)
    for f in full_width_fields:
        label = generate_label(f, config, is_full_width=True)
        input_html = generate_input(f)
        error_html = generate_error_container(f, config)

        html_parts.append(f"""<div class="relative flex items-start mt-4">
        {label}
        <div class="flex-1 relative">
            {input_html}
            {error_html}
        </div>
    </div>""")

    # Form actions
    html_parts.append(f"""<div class="form-actions mt-8 flex justify-end gap-4">
        <button type="button" class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50">
            {config.cancel_text}
        </button>
        <button type="submit" class="px-4 py-2 text-sm font-medium text-white bg-primary-600 border border-transparent rounded-md hover:bg-primary-700">
            {config.submit_text}
        </button>
    </div>""")

    html_parts.append("""</form>""")

    form_content = "\n".join(html_parts)

    # Wrap in complete HTML document
    return f"""<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>{config.title or "Form"}</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    /* Form-specific styles */
    .form-container {{
      max-width: 1200px;
      margin: 0 auto;
    }}
    .error-message {{
      transition: opacity 0.2s ease;
    }}
    .form-control-complex {{
      position: relative;
    }}
    /* Custom form-radio for better visibility */
    .form-radio {{
      width: 1.25rem;
      height: 1.25rem;
      accent-color: #3b82f6;
    }}
    /* Select dropdown styling */
    select {{
      background-image: none;
    }}
    /* Focus ring animation */
    .focus-ring {{
      transition: ring-color 0.15s ease;
    }}
    /* Primary color override */
    .bg-primary-600 {{ background-color: #3b82f6; }}
    .hover\\:bg-primary-700:hover {{ background-color: #2563eb; }}
    .focus\\:ring-primary-500:focus {{ --tw-ring-color: #3b82f6; }}
    .peer-focus\\:ring-primary-300:focus-within {{ --tw-ring-color: #93c5fd; }}
    .peer-checked\\:bg-primary-600:checked {{ background-color: #3b82f6; }}
    .hover\\:border-primary-500:hover {{ border-color: #3b82f6; }}
    /* Input hover state */
    input:hover, select:hover, textarea:hover {{
      border-color: #9ca3af;
    }}
    /* Textarea styling */
    textarea {{
      line-height: 1.5;
    }}
  </style>
</head>
<body class="bg-gray-50 min-h-screen p-8">
{form_content}
<script>
  // Floating error display
  document.querySelectorAll('input, select, textarea').forEach(input => {{
    input.addEventListener('invalid', (e) => {{
      e.preventDefault();
      const errorDiv = document.querySelector(`.error-message[data-field="${{input.name}}"]`);
      if (errorDiv) {{
        errorDiv.textContent = input.validationMessage;
        errorDiv.classList.remove('hidden');
      }}
    }});
    
    input.addEventListener('input', () => {{
      const errorDiv = document.querySelector(`.error-message[data-field="${{input.name}}"]`);
      if (errorDiv) {{
        errorDiv.classList.add('hidden');
      }}
    }});
  }});
</script>
</body>
</html>"""


def from_data_model(model_data: dict, config: FormConfig) -> List[FormField]:
    """Convert data model definition to form fields."""
    fields = []

    for field_data in model_data.get("fields", []):
        meta = FieldMeta(
            name=field_data.get("name", ""),
            data_type=field_data.get("type", "VARCHAR"),
            comment=field_data.get("comment", ""),
            markers=field_data.get("markers", []),
        )

        result = infer_control_type(meta)

        # Extract markers
        markers = field_data.get("markers", [])

        # Auto-adjust column_span for large controls
        column_span = field_data.get("column_span", 1)

        # TXA (textarea) fields should span full width by default
        if result.control_type == ControlType.TXA:
            column_span = max(column_span, config.columns)  # Force full-width

        # TAB (table editor) fields should span full width
        if result.control_type == ControlType.TAB:
            column_span = max(column_span, config.columns)

        # RTE (rich text) fields should span full width
        if result.control_type == ControlType.RTE:
            column_span = max(column_span, config.columns)

        field = FormField(
            name=field_data.get("name", ""),
            label=field_data.get("label", field_data.get("comment", "")),
            control_type=result.control_type,
            required="REQ" in markers,
            nullable="NUL" in markers,
            max_length=field_data.get("max_length"),
            column_span=column_span,
            options=field_data.get("options", []),
            default_value=field_data.get("default"),
            disabled="HID" in markers,
        )

        fields.append(field)

    return fields


def main():
    parser = argparse.ArgumentParser(
        description="Generate Tailwind CSS forms from data model definitions"
    )
    parser.add_argument(
        "--model",
        "-m",
        type=str,
        required=True,
        help="Input JSON file with data model definition",
    )
    parser.add_argument("--output", "-o", type=str, help="Output HTML file")
    parser.add_argument(
        "--cols",
        "-c",
        type=int,
        default=2,
        choices=[1, 2, 3, 4],
        help="Number of form columns (default: 2)",
    )
    parser.add_argument(
        "--gap",
        "-g",
        type=int,
        default=8,
        choices=[4, 6, 8],
        help="Grid gap class (default: 8)",
    )
    parser.add_argument("--title", "-t", type=str, default="", help="Form title")

    args = parser.parse_args()

    # Load model
    with open(args.model, "r", encoding="utf-8") as f:
        model_data = json.load(f)

    # Create config
    config = FormConfig(
        title=args.title or model_data.get("name", "Form"),
        columns=args.cols,
        gap=args.gap,
    )

    # Convert model to fields
    fields = from_data_model(model_data, config)

    # Generate form
    html = generate_form(fields, config)

    # Output
    if args.output:
        with open(args.output, "w", encoding="utf-8") as f:
            f.write(html)
        print(f"Generated form: {args.output}")
    else:
        print(html)


if __name__ == "__main__":
    main()
