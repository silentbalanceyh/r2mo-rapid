package io.r2mo.base.util;

import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
class UTNumeric {
    // isPositive
    private static final String POSITIVE = "^\\+{0,1}[0-9]\\d*";
    // isNegative
    private static final String NEGATIVE = "^-[0-9]\\d*";
    // isInteger
    private static final String INTEGER = "[+-]{0,1}0";
    // isDecimal
    private static final String DECIMAL = "[-+]{0,1}\\d+\\.\\d*|[-+]{0,1}\\d*\\.\\d+";
    // isDecimalPositive
    private static final String DECIMAL_POSITIVE = "\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*";
    // isDecimalNegative
    private static final String DECIMAL_NEGATIVE = "^-[0]\\.[1-9]*|^-[1-9]\\d*\\.\\d*";

    static boolean isPositive(final String literal) {
        return Objects.nonNull(literal)
            && UTType.isMatch(literal, POSITIVE);
    }

    static boolean isPositive(final int number) {
        return 0 < number;
    }

    static boolean isNegative(final String literal) {
        return Objects.nonNull(literal)
            && UTType.isMatch(literal, NEGATIVE);
    }

    static boolean isNegative(final int number) {
        return 0 > number;
    }

    static boolean isInteger(final String literal) {
        return (Objects.nonNull(literal)
            && UTType.isMatch(literal, INTEGER))
            || isPositive(literal)
            || isNegative(literal);
    }

    static boolean isDecimal(final String literal) {
        return Objects.nonNull(literal)
            && UTType.isMatch(literal, DECIMAL);
    }

    static boolean isReal(final String literal) {
        return isInteger(literal) || isDecimal(literal);
    }

    static boolean isDecimalPositive(final String literal) {
        return Objects.nonNull(literal)
            && UTType.isMatch(literal, DECIMAL_POSITIVE);
    }

    static boolean isDecimalNegative(final String literal) {
        return Objects.nonNull(literal)
            && UTType.isMatch(literal, DECIMAL_NEGATIVE);
    }

    /**
     * 🔄 检查整数值是否在指定范围内
     * <p>
     * 🔍 支持边界值为 null 的情况：
     * <ul>
     *   <li>当 min 和 max 都为 null 时，返回 true（无限制）</li>
     *   <li>当 min 为 null 时，只检查上界</li>
     *   <li>当 max 为 null 时，只检查下界</li>
     *   <li>当 min 和 max 都不为 null 时，检查是否在范围内</li>
     * </ul>
     *
     * @param value 🎯 要检查的整数值
     * @param min   ⬇️ 最小值（可为 null，表示无下界限制）
     * @param max   ⬆️ 最大值（可为 null，表示无上界限制）
     *
     * @return ✅ 如果值在范围内（或无限制）返回 true，否则返回 false
     * @since 💡 1.0.0
     */
    static boolean isIn(final Integer value, final Integer min, final Integer max) {
        // ✅ 无边界限制
        if (min == null && max == null) {
            return true;
        }

        // 🔢 检查下界（如果存在）
        if (min != null && value < min) {
            return false;
        }

        // 🔢 检查上界（如果存在）
        return max == null || value <= max;
    }
}
