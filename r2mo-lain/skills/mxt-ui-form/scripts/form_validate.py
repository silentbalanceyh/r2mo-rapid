#!/usr/bin/env python3
"""
Form Layout Validator for Tailwind CSS Forms.

Detects common layout issues:
- Overlapping elements
- Squeezed/compressed inputs
- Misaligned labels
- Broken responsive behavior
- Missing accessibility attributes

Usage:
    python form_validate.py --input form.html
    python form_validate.py --input form.html --fix-suggestions
"""

import argparse
import re
from dataclasses import dataclass
from typing import List, Optional
from pathlib import Path
from html.parser import HTMLParser


@dataclass
class ValidationIssue:
    """Represents a form validation issue."""

    severity: str  # ERROR, WARNING, INFO
    category: str  # LAYOUT, ACCESSIBILITY, RESPONSIVE, STYLE
    element: str
    line: Optional[int]
    message: str
    suggestion: Optional[str] = None


class FormValidator(HTMLParser):
    """Parse and validate HTML form structure."""

    def __init__(self):
        super().__init__()
        self.issues: List[ValidationIssue] = []
        self.current_element = None
        self.line_number = 1
        self.label_widths = {}  # Track label widths by section
        self.input_elements = []
        self.grid_contexts = []  # Track grid column counts

    def handle_starttag(self, tag, attrs):
        self.current_element = tag
        attrs_dict = dict(attrs)

        # Track line number (approximate)
        self.line_number += 1

        # Check for grid containers
        if tag == "div":
            class_attr = attrs_dict.get("class", "")
            if "grid-cols-" in class_attr:
                # Extract column count
                match = re.search(r"grid-cols-(\d+)", class_attr)
                if match:
                    cols = int(match.group(1))
                    self.grid_contexts.append(cols)

        # Check labels
        if tag == "label":
            class_attr = attrs_dict.get("class", "")

            # Check for calc() in grid context (potential issue)
            if "calc(" in class_attr and self.grid_contexts:
                # If we're inside a grid and using calc, that's OK for full-width
                # but NOT OK for grid cells
                if not self._is_full_width_context():
                    self.issues.append(
                        ValidationIssue(
                            severity="WARNING",
                            category="LAYOUT",
                            element="label",
                            line=self.line_number,
                            message="Label using calc() inside grid cell may cause width issues",
                            suggestion="Use simple fraction like w-1/4 for grid cells, calc() only for full-width rows",
                        )
                    )

            # Check label width consistency
            width_match = re.search(r"w-(\d+)/(\d+)", class_attr)
            if width_match:
                fraction = f"{width_match.group(1)}/{width_match.group(2)}"
                self.label_widths[self.line_number] = fraction

        # Check inputs
        if tag in ["input", "select", "textarea"]:
            class_attr = attrs_dict.get("class", "")

            # Check for missing width on input
            if "w-full" not in class_attr and "flex-1" not in class_attr:
                self.issues.append(
                    ValidationIssue(
                        severity="INFO",
                        category="LAYOUT",
                        element=tag,
                        line=self.line_number,
                        message=f"Input element may have inconsistent width",
                        suggestion="Consider adding w-full or flex-1 class",
                    )
                )

            # Check select dropdown styling
            if tag == "select":
                if "appearance-none" not in class_attr:
                    self.issues.append(
                        ValidationIssue(
                            severity="WARNING",
                            category="STYLE",
                            element="select",
                            line=self.line_number,
                            message="Select missing custom dropdown styling",
                            suggestion="Add appearance-none pr-10 for custom dropdown arrow",
                        )
                    )

            # Check textarea resize
            if tag == "textarea":
                if "resize" not in class_attr:
                    self.issues.append(
                        ValidationIssue(
                            severity="INFO",
                            category="STYLE",
                            element="textarea",
                            line=self.line_number,
                            message="Textarea has default resize behavior",
                            suggestion="Consider resize-none for fixed height forms",
                        )
                    )

            # Check for accessibility
            if not attrs_dict.get("id"):
                self.issues.append(
                    ValidationIssue(
                        severity="ERROR",
                        category="ACCESSIBILITY",
                        element=tag,
                        line=self.line_number,
                        message=f"{tag} missing id attribute for label association",
                        suggestion="Add id attribute to associate with label",
                    )
                )

            self.input_elements.append(
                {"tag": tag, "line": self.line_number, "classes": class_attr}
            )

    def handle_endtag(self, tag):
        if tag == "div" and self.grid_contexts:
            self.grid_contexts.pop()

    def _is_full_width_context(self) -> bool:
        """Check if current context is a full-width row (outside grid)."""
        return len(self.grid_contexts) == 0


def validate_form(html_content: str) -> List[ValidationIssue]:
    """Validate form HTML and return list of issues."""
    validator = FormValidator()
    validator.feed(html_content)

    # Additional validation checks

    # Check for Reset/Clear buttons (anti-pattern)
    if re.search(
        r"<button[^>]*>(\s*Clear\s*|\s*Reset\s*)</button>", html_content, re.I
    ):
        validator.issues.append(
            ValidationIssue(
                severity="WARNING",
                category="UX",
                element="button",
                line=None,
                message="Form contains Reset/Clear button (anti-pattern)",
                suggestion="Remove Reset button to prevent accidental data loss",
            )
        )

    # Check for placeholder-only labels (anti-pattern)
    if re.search(r'<input[^>]*placeholder="[^"]*"[^>]*>', html_content):
        if not re.search(r"<label", html_content):
            validator.issues.append(
                ValidationIssue(
                    severity="ERROR",
                    category="ACCESSIBILITY",
                    element="input",
                    line=None,
                    message="Inputs have placeholders but no labels",
                    suggestion="Add visible labels for all form fields",
                )
            )

    # Check for inline error containers
    if re.search(r"error-message[^>]*>(?!</div>)", html_content):
        # Check if error is positioned
        if not re.search(r"error-message[^>]*absolute", html_content):
            validator.issues.append(
                ValidationIssue(
                    severity="WARNING",
                    category="LAYOUT",
                    element="div",
                    line=None,
                    message="Error messages may cause form layout shifts",
                    suggestion="Use absolute positioning for floating errors",
                )
            )

    return validator.issues


def print_report(issues: List[ValidationIssue], show_suggestions: bool = True):
    """Print validation report."""
    if not issues:
        print("✅ No issues found!")
        return

    # Group by severity
    errors = [i for i in issues if i.severity == "ERROR"]
    warnings = [i for i in issues if i.severity == "WARNING"]
    infos = [i for i in issues if i.severity == "INFO"]

    print("\n" + "=" * 60)
    print("FORM VALIDATION REPORT")
    print("=" * 60)

    if errors:
        print(f"\n❌ ERRORS ({len(errors)})")
        print("-" * 40)
        for issue in errors:
            print(f"  [{issue.category}] {issue.element}")
            print(f"    Line {issue.line or '?'}: {issue.message}")
            if show_suggestions and issue.suggestion:
                print(f"    💡 {issue.suggestion}")

    if warnings:
        print(f"\n⚠️  WARNINGS ({len(warnings)})")
        print("-" * 40)
        for issue in warnings:
            print(f"  [{issue.category}] {issue.element}")
            print(f"    Line {issue.line or '?'}: {issue.message}")
            if show_suggestions and issue.suggestion:
                print(f"    💡 {issue.suggestion}")

    if infos:
        print(f"\nℹ️  INFO ({len(infos)})")
        print("-" * 40)
        for issue in infos:
            print(f"  [{issue.category}] {issue.element}")
            print(f"    Line {issue.line or '?'}: {issue.message}")
            if show_suggestions and issue.suggestion:
                print(f"    💡 {issue.suggestion}")

    print("\n" + "=" * 60)
    print(
        f"Total: {len(issues)} issues ({len(errors)} errors, {len(warnings)} warnings, {len(infos)} info)"
    )
    print("=" * 60)


def main():
    parser = argparse.ArgumentParser(
        description="Validate HTML form for layout and accessibility issues"
    )
    parser.add_argument(
        "--input", "-i", type=str, required=True, help="Input HTML file to validate"
    )
    parser.add_argument(
        "--fix-suggestions",
        action="store_true",
        help="Show fix suggestions for each issue",
    )
    parser.add_argument("--json", action="store_true", help="Output as JSON")

    args = parser.parse_args()

    # Read file
    with open(args.input, "r", encoding="utf-8") as f:
        html_content = f.read()

    # Validate
    issues = validate_form(html_content)

    # Output
    if args.json:
        import json

        output = [
            {
                "severity": i.severity,
                "category": i.category,
                "element": i.element,
                "line": i.line,
                "message": i.message,
                "suggestion": i.suggestion,
            }
            for i in issues
        ]
        print(json.dumps(output, indent=2, ensure_ascii=False))
    else:
        print_report(issues, show_suggestions=args.fix_suggestions)


if __name__ == "__main__":
    main()
