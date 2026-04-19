#!/usr/bin/env python3
"""
Control Type Inference Engine for Form Generation.

Infers UI control types from field metadata including:
- Field name patterns
- SQL/data types
- Attribute markers (TYP, REQ, NUL, LEN, etc.)
- Comment annotations

Maps to 35 control types: TEX, SEL, RAD, CHK, CAS, TRE, TAB, etc.
"""

import argparse
import json
import re
from dataclasses import dataclass, field
from typing import Optional
from enum import Enum


class ControlType(Enum):
    """UI control types matching marker.md TYP scope."""

    TEX = "TEX"  # Text input (single line)
    TXA = "TXA"  # Textarea (multi-line)
    PAS = "PAS"  # Password input (masked)
    EMA = "EMA"  # Email input with validation
    NUM = "NUM"  # Numeric input
    DAT = "DAT"  # Date picker
    DTM = "DTM"  # DateTime picker
    TIM = "TIM"  # Time picker
    SEL = "SEL"  # Select dropdown (single choice)
    MUL = "MUL"  # Multi-select dropdown
    RAD = "RAD"  # Radio buttons
    CHK = "CHK"  # Checkbox (single boolean)
    CHG = "CHG"  # Checkbox group (multiple choices)
    SWI = "SWI"  # Toggle switch
    SLI = "SLI"  # Slider/range input
    MAP = "MAP"  # Map selector for geographic coordinates
    IMG = "IMG"  # Image upload/picker
    FIL = "FIL"  # File upload
    VID = "VID"  # Video upload/picker
    AUD = "AUD"  # Audio upload/picker
    RTE = "RTE"  # Rich text editor (WYSIWYG)
    MDE = "MDE"  # Markdown editor
    COD = "COD"  # Code editor with syntax highlighting
    CLR = "CLR"  # Color picker
    RAT = "RAT"  # Rating stars/icons
    TAG = "TAG"  # Tag input (multiple keywords)
    CAS = "CAS"  # Cascading selector (hierarchical choices)
    TRE = "TRE"  # Tree selector (hierarchical tree structure)
    TAB = "TAB"  # Table editor (inline grid)
    JSN = "JSN"  # JSON editor
    ICO = "ICO"  # Icon picker
    SGN = "SGN"  # Signature pad (drawing)
    QRC = "QRC"  # QR code scanner/generator
    BAR = "BAR"  # Barcode scanner/generator


@dataclass
class FieldMeta:
    """Field metadata for control type inference."""

    name: str
    data_type: str = "VARCHAR"
    comment: str = ""
    markers: list = field(default_factory=list)
    required: bool = False
    nullable: bool = True
    max_length: Optional[int] = None
    control_type: Optional[str] = None  # Explicit TYP marker


@dataclass
class InferenceResult:
    """Result of control type inference."""

    field_name: str
    control_type: ControlType
    confidence: float  # 0.0 - 1.0
    reason: str
    markers_applied: list = field(default_factory=list)


# Field name pattern rules
NAME_PATTERNS = {
    # Authentication
    r"password|pwd|passwd": ControlType.PAS,
    r"confirm_password|confirm_pwd": ControlType.PAS,
    # Contact
    r"email|mail|e_mail": ControlType.EMA,
    r"phone|mobile|tel|telephone": ControlType.TEX,
    r"fax": ControlType.TEX,
    # Text content
    r"description|desc|remark|note|comment|content|body": ControlType.TXA,
    r"bio|introduction|summary|abstract": ControlType.TXA,
    r"address|addr": ControlType.TXA,
    # Name fields
    r"username|user_name|login_name": ControlType.TEX,
    r"realname|real_name|full_name|display_name": ControlType.TEX,
    r"nickname|nick_name|alias": ControlType.TEX,
    r"first_name|last_name|name": ControlType.TEX,
    # Numeric
    r"age": ControlType.NUM,
    r"count|quantity|qty|amount|number|num": ControlType.NUM,
    r"price|cost|fee|total|subtotal": ControlType.NUM,
    r"discount|rate|percent|percentage": ControlType.NUM,
    r"weight|height|width|length|size": ControlType.NUM,
    r"price|amount|fee|cost": ControlType.NUM,
    # Date/Time
    r"birthday|birth_date|birthdate": ControlType.DAT,
    r"create_time|created_at|create_date": ControlType.DTM,
    r"update_time|updated_at|update_date|modify_time": ControlType.DTM,
    r"start_time|end_time|begin_time|finish_time": ControlType.DTM,
    r"date|datetime|timestamp": ControlType.DTM,
    r"time$": ControlType.TIM,
    # Boolean
    r"is_|has_|can_|should_": ControlType.CHK,
    r"enabled|disabled|active|inactive|visible|hidden": ControlType.SWI,
    r"published|approved|verified": ControlType.SWI,
    r"agree|accept|consent": ControlType.CHK,
    # Selection
    r"gender|sex": ControlType.RAD,
    r"status|state": ControlType.SEL,
    r"type|category|kind": ControlType.SEL,
    r"level|grade|rank|priority": ControlType.SEL,
    r"country|province|city|region|district": ControlType.CAS,
    # Multi-value
    r"tags|keywords|labels": ControlType.TAG,
    r"roles|permissions|features": ControlType.CHG,
    # Media
    r"avatar|photo|image|picture|logo|icon|thumbnail": ControlType.IMG,
    r"video|video_url|clip": ControlType.VID,
    r"audio|voice|sound|recording": ControlType.AUD,
    r"file|attachment|document": ControlType.FIL,
    # Special
    r"url|link|website|homepage": ControlType.TEX,
    r"slug|code|key": ControlType.TEX,
    r"color|colour|theme_color": ControlType.CLR,
    r"rating|score|stars": ControlType.RAT,
    r"password": ControlType.PAS,
    # Geographic
    r"location|coordinate|latitude|longitude|geo|position": ControlType.MAP,
    r"address": ControlType.TXA,
    # Rich content
    r"content|article|body|html": ControlType.RTE,
    r"markdown|md": ControlType.MDE,
    r"code|script|snippet|source": ControlType.COD,
    r"config|settings|metadata|json": ControlType.JSN,
    # Hierarchical
    r"parent_id|parent": ControlType.TRE,
    r"tree|organization|org|department|dept": ControlType.TRE,
    r"category_tree|category_path": ControlType.CAS,
    # Table/Grid
    r"items|details|rows|grid|matrix|schedule": ControlType.TAB,
    # Signature
    r"signature|sign": ControlType.SGN,
    # QR/Barcode
    r"qr|qrcode": ControlType.QRC,
    r"barcode|sku_code": ControlType.BAR,
}

# Data type mapping
DATA_TYPE_MAP = {
    "VARCHAR": ControlType.TEX,
    "CHAR": ControlType.TEX,
    "TEXT": ControlType.TXA,
    "LONGTEXT": ControlType.RTE,
    "MEDIUMTEXT": ControlType.TXA,
    "INT": ControlType.NUM,
    "INTEGER": ControlType.NUM,
    "BIGINT": ControlType.NUM,
    "SMALLINT": ControlType.NUM,
    "TINYINT": ControlType.NUM,
    "DECIMAL": ControlType.NUM,
    "FLOAT": ControlType.NUM,
    "DOUBLE": ControlType.NUM,
    "BIT": ControlType.CHK,
    "BOOLEAN": ControlType.SWI,
    "DATE": ControlType.DAT,
    "DATETIME": ControlType.DTM,
    "TIMESTAMP": ControlType.DTM,
    "TIME": ControlType.TIM,
}

# Comment annotation patterns
COMMENT_PATTERNS = {
    r"下拉|选择|select|dropdown": ControlType.SEL,
    r"多选|multiple|multi": ControlType.MUL,
    r"单选|radio": ControlType.RAD,
    r"复选|checkbox|勾选": ControlType.CHK,
    r"开关|switch|toggle": ControlType.SWI,
    r"树|tree|组织|部门": ControlType.TRE,
    r"级联|cascad": ControlType.CAS,
    r"富文本|editor|wysiwyg": ControlType.RTE,
    r"图片|图片上传|image": ControlType.IMG,
    r"文件|附件|file": ControlType.FIL,
    r"日期|date|时间": ControlType.DAT,
    r"地址|address": ControlType.TXA,
    r"标签|tag": ControlType.TAG,
}


def infer_control_type(field: FieldMeta) -> InferenceResult:
    """
    Infer control type from field metadata.

    Priority:
    1. Explicit TYP marker
    2. Field name patterns
    3. Comment annotations
    4. Data type mapping
    5. Default to TEX
    """
    # 1. Check explicit TYP marker
    if field.control_type:
        try:
            return InferenceResult(
                field_name=field.name,
                control_type=ControlType(field.control_type),
                confidence=1.0,
                reason="Explicit TYP marker",
                markers_applied=["TYP"],
            )
        except ValueError:
            pass

    # Check markers for TYP
    for marker in field.markers:
        if marker.startswith("TYP("):
            match = re.search(r"TYP\((\w+)\)", marker)
            if match:
                try:
                    return InferenceResult(
                        field_name=field.name,
                        control_type=ControlType(match.group(1)),
                        confidence=1.0,
                        reason="Explicit TYP marker",
                        markers_applied=["TYP"],
                    )
                except ValueError:
                    pass

    name_lower = field.name.lower()
    comment_lower = field.comment.lower()

    # 2. Check name patterns
    for pattern, ctrl_type in NAME_PATTERNS.items():
        if re.search(pattern, name_lower, re.IGNORECASE):
            return InferenceResult(
                field_name=field.name,
                control_type=ctrl_type,
                confidence=0.85,
                reason=f"Name pattern match: {pattern}",
                markers_applied=[],
            )

    # 3. Check comment annotations
    for pattern, ctrl_type in COMMENT_PATTERNS.items():
        if re.search(pattern, comment_lower, re.IGNORECASE):
            return InferenceResult(
                field_name=field.name,
                control_type=ctrl_type,
                confidence=0.75,
                reason=f"Comment pattern match: {pattern}",
                markers_applied=[],
            )

    # 4. Check data type
    data_type_upper = field.data_type.upper()
    for db_type, ctrl_type in DATA_TYPE_MAP.items():
        if db_type in data_type_upper:
            return InferenceResult(
                field_name=field.name,
                control_type=ctrl_type,
                confidence=0.6,
                reason=f"Data type mapping: {db_type}",
                markers_applied=[],
            )

    # 5. Default
    return InferenceResult(
        field_name=field.name,
        control_type=ControlType.TEX,
        confidence=0.3,
        reason="Default fallback",
        markers_applied=[],
    )


def infer_from_json(input_data: dict) -> list[InferenceResult]:
    """Infer control types from JSON field definitions."""
    results = []
    fields = input_data.get("fields", [])

    for field_data in fields:
        field = FieldMeta(
            name=field_data.get("name", ""),
            data_type=field_data.get("type", "VARCHAR"),
            comment=field_data.get("comment", ""),
            markers=field_data.get("markers", []),
            required="REQ" in field_data.get("markers", []),
            nullable="NUL" in field_data.get("markers", []),
            max_length=field_data.get("max_length"),
            control_type=field_data.get("control_type"),
        )
        result = infer_control_type(field)
        results.append(result)

    return results


def main():
    parser = argparse.ArgumentParser(
        description="Infer UI control types from field metadata"
    )
    parser.add_argument(
        "--input", "-i", type=str, help="Input JSON file with field definitions"
    )
    parser.add_argument(
        "--output", "-o", type=str, help="Output JSON file for control mappings"
    )
    parser.add_argument(
        "--field", "-f", type=str, help="Single field name to infer (quick test)"
    )
    parser.add_argument(
        "--type",
        "-t",
        type=str,
        default="VARCHAR",
        help="Data type for single field inference",
    )
    parser.add_argument(
        "--comment",
        "-c",
        type=str,
        default="",
        help="Comment for single field inference",
    )
    parser.add_argument("--json", action="store_true", help="Output as JSON")

    args = parser.parse_args()

    if args.field:
        # Single field inference
        field = FieldMeta(name=args.field, data_type=args.type, comment=args.comment)
        result = infer_control_type(field)

        if args.json:
            print(
                json.dumps(
                    {
                        "field": result.field_name,
                        "control_type": result.control_type.value,
                        "confidence": result.confidence,
                        "reason": result.reason,
                    },
                    indent=2,
                )
            )
        else:
            print(f"Field: {result.field_name}")
            print(f"Control Type: {result.control_type.value}")
            print(f"Confidence: {result.confidence:.0%}")
            print(f"Reason: {result.reason}")

    elif args.input:
        # Batch inference from file
        with open(args.input, "r", encoding="utf-8") as f:
            input_data = json.load(f)

        results = infer_from_json(input_data)

        output_data = {
            "fields": [
                {
                    "name": r.field_name,
                    "control_type": r.control_type.value,
                    "confidence": r.confidence,
                    "reason": r.reason,
                    "markers": r.markers_applied,
                }
                for r in results
            ]
        }

        if args.output:
            with open(args.output, "w", encoding="utf-8") as f:
                json.dump(output_data, f, indent=2, ensure_ascii=False)
            print(f"Wrote {len(results)} control mappings to {args.output}")
        else:
            print(json.dumps(output_data, indent=2, ensure_ascii=False))

    else:
        parser.print_help()


if __name__ == "__main__":
    main()
