#!/usr/bin/env python3
"""
Form Layout Calculator for Tailwind CSS Grid Forms.

Calculates label width values for pixel-perfect alignment across
multi-column grid forms with varying row spans.

Formula: label_width = calc((100% - gap × (C-1)) / C × F)
- C = number of grid columns
- F = label fraction inside one column (e.g., 1/4, 1/3)
- gap = horizontal gap between columns (rem units)
"""

import argparse
import json
from dataclasses import dataclass
from typing import Optional


@dataclass
class GapConfig:
    """Tailwind gap class to rem value mapping."""

    gap_4: float = 1.0  # gap-4 = 1rem = 16px
    gap_6: float = 1.5  # gap-6 = 1.5rem = 24px
    gap_8: float = 2.0  # gap-8 = 2rem = 32px


def parse_fraction(fraction: str) -> tuple[int, int]:
    """Parse fraction string like '1/4' to (numerator, denominator)."""
    parts = fraction.split("/")
    if len(parts) != 2:
        raise ValueError(f"Invalid fraction format: {fraction}")
    return int(parts[0]), int(parts[1])


def calc_label_width(
    grid_cols: int, gap_rem: float, label_fraction: str, row_span: int = 0
) -> str:
    """
    Calculate label width for Tailwind CSS arbitrary value.

    Args:
        grid_cols: Number of columns in the grid (1-4)
        gap_rem: Gap value in rem units
        label_fraction: Fraction of column for label (e.g., '1/4', '1/3')
        row_span: Columns spanned by the row (0 = full width)

    Returns:
        Tailwind CSS arbitrary value class: w-[calc(...)]
    """
    num, den = parse_fraction(label_fraction)

    # Full-width row (row_span = 0 or row_span >= grid_cols)
    if row_span == 0 or row_span >= grid_cols:
        if grid_cols == 1:
            # Single column: simple fraction
            return f"w-{num}/{den}"
        else:
            # Multi-column grid: calculate effective width
            # Formula: calc((100% - gap × (C-1)) / C × F)
            total_gap = gap_rem * (grid_cols - 1)
            divisor = grid_cols * den // num
            return f"w-[calc((100%-{total_gap}rem)/{divisor})]"
    else:
        # Partial span row (K columns out of C)
        # Formula: calc((100% - gap × (C-1)) / C × K × F)
        total_gap = gap_rem * (grid_cols - 1)
        divisor = (grid_cols * den) // (num * row_span)
        return f"w-[calc((100%-{total_gap}rem)/{divisor})]"


def generate_cheat_sheet() -> dict:
    """Generate quick reference table for common combinations."""
    gaps = {"gap-4": 1.0, "gap-6": 1.5, "gap-8": 2.0}
    fractions = ["1/4", "1/3", "1/5"]

    result = {}
    for gap_name, gap_val in gaps.items():
        result[gap_name] = {}
        for frac in fractions:
            result[gap_name][frac] = {
                f"grid-cols-{c}": calc_label_width(c, gap_val, frac)
                for c in range(1, 5)
            }
    return result


def print_cheat_sheet():
    """Print formatted cheat sheet."""
    print("=" * 60)
    print("FORM LABEL ALIGNMENT CHEAT SHEET")
    print("=" * 60)
    print()

    for frac in ["1/4", "1/3"]:
        print(f"\nLabel fraction: w-{frac}")
        print("-" * 40)
        print(f"{'Grid':<15} {'gap-8 (2rem)':<25}")
        print("-" * 40)

        for cols in range(1, 5):
            width = calc_label_width(cols, 2.0, frac)
            print(f"grid-cols-{cols:<10} {width}")

    print("\n" + "=" * 60)


def main():
    parser = argparse.ArgumentParser(
        description="Calculate form label widths for Tailwind CSS grid alignment"
    )
    parser.add_argument(
        "--cols",
        "-c",
        type=int,
        default=2,
        choices=[1, 2, 3, 4],
        help="Number of grid columns (default: 2)",
    )
    parser.add_argument(
        "--gap",
        "-g",
        type=int,
        default=8,
        choices=[4, 6, 8],
        help="Gap class number: 4, 6, or 8 (default: 8)",
    )
    parser.add_argument(
        "--fraction",
        "-f",
        type=str,
        default="1/4",
        help="Label fraction: 1/4, 1/3, 1/5 (default: 1/4)",
    )
    parser.add_argument(
        "--span", "-s", type=int, default=0, help="Row span columns (0 = full width)"
    )
    parser.add_argument(
        "--cheat-sheet", action="store_true", help="Print quick reference cheat sheet"
    )
    parser.add_argument("--json", action="store_true", help="Output as JSON")

    args = parser.parse_args()

    if args.cheat_sheet:
        print_cheat_sheet()
        return

    gap_rem = {4: 1.0, 6: 1.5, 8: 2.0}[args.gap]
    result = calc_label_width(args.cols, gap_rem, args.fraction, args.span)

    if args.json:
        output = {
            "grid_columns": args.cols,
            "gap": f"gap-{args.gap}",
            "gap_rem": gap_rem,
            "label_fraction": f"w-{args.fraction}",
            "row_span": args.span if args.span > 0 else "full",
            "tailwind_class": result,
        }
        print(json.dumps(output, indent=2))
    else:
        print(f"Grid: grid-cols-{args.cols}, Gap: gap-{args.gap}")
        print(f"Label fraction: w-{args.fraction}")
        print(f"Row span: {'full width' if args.span == 0 else f'{args.span} columns'}")
        print(f"\nTailwind class: {result}")


if __name__ == "__main__":
    main()
