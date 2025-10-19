package io.r2mo.base.util;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author lang : 2025-10-19
 */
class _UtilNum extends _UtilJson {

    /**
     * 检查一个字符串是否正整数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否正整数
     */
    public static boolean isPositive(final String literal) {
        return UTNumeric.isPositive(literal);
    }

    /**
     * （传函数）函数模式专用，检查一个数值是否正整数
     *
     * @param number 数值
     *
     * @return 是否正整数
     */
    public static boolean isPositive(final int number) {
        return UTNumeric.isPositive(number);
    }

    /**
     * 检查一个对象是否正整数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否正整数
     */
    public static boolean isPositive(final Object input) {
        return Objects.nonNull(input)
            && UTNumeric.isPositive(input.toString().trim().intern());
    }

    /**
     * 检查一个字符串是否负整数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否负整数
     */
    public static boolean isNegative(final String literal) {
        return UTNumeric.isNegative(literal);
    }

    /**
     * （传函数）函数模式专用，检查一个数值是否负整数
     *
     * @param number 数值
     *
     * @return 是否负整数
     */
    public static boolean isNegative(final int number) {
        return UTNumeric.isNegative(number);
    }

    /**
     * 检查一个对象是否负整数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否负整数
     */
    public static boolean isNegative(final Object input) {
        return Objects.nonNull(input)
            && UTNumeric.isNegative(input.toString().trim().intern());
    }

    /**
     * 检查一个字符串是否整数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否整数
     */
    public static boolean isInteger(final String literal) {
        return UTNumeric.isInteger(literal);
    }

    /**
     * 检查一个对象是否整数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否整数
     */
    public static boolean isInteger(final Object input) {
        return Objects.nonNull(input)
            && UTNumeric.isInteger(input.toString().trim().intern());
    }


    /**
     * 检查一个字符串是否实数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否数值
     */
    public static boolean isReal(final String literal) {
        return UTNumeric.isReal(literal);
    }

    /**
     * 检查一个对象是否实数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否数值
     */
    public static boolean isReal(final Object input) {
        return Objects.nonNull(input)
            && UTNumeric.isReal(input.toString().trim().intern());
    }

    /**
     * 检查一个字符串是否浮点数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否浮点数
     */
    public static boolean isDecimal(final String literal) {
        return UTNumeric.isDecimal(literal);
    }

    /**
     * 检查一个对象是否浮点数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否浮点数
     */
    public static boolean isDecimal(final Object input) {
        return Objects.nonNull(input)
            && UTNumeric.isDecimal(input.toString().trim().intern());
    }

    /**
     * 检查一个字符串是否正浮点数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否正浮点数
     */
    public static boolean isDecimalPositive(final String literal) {
        return UTNumeric.isDecimalPositive(literal);
    }

    /**
     * 检查一个对象是否正浮点数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否正浮点数
     */
    public static boolean isDecimalPositive(final Object input) {
        return Objects.nonNull(input)
            && UTNumeric.isDecimalPositive(input.toString().trim().intern());
    }

    /**
     * 检查一个字符串是否负浮点数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否负浮点数
     */
    public static boolean isDecimalNegative(final String literal) {
        return UTNumeric.isDecimalNegative(literal);
    }

    /**
     * 检查一个对象是否负浮点数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否负浮点数
     */
    public static boolean isDecimalNegative(final Object input) {
        return Objects.nonNull(input)
            && UTNumeric.isDecimalNegative(input.toString().trim().intern());
    }

    /**
     * 「动参版本」
     * 检查传入的对象是否全部是正整数
     *
     * @param numbers 对象数组（基础类型）
     *
     * @return 是否全部是正整数
     */
    public static boolean isPositive(final int... numbers) {
        return Arrays.stream(numbers)
            .allMatch(UTNumeric::isPositive);
    }

    /**
     * 「动参版本」
     * 检查传入的对象是否全部是正整数
     *
     * @param numbers 对象数组
     *
     * @return 是否全部是正整数
     */
    public static boolean isPositive(final Integer... numbers) {
        return Arrays.stream(numbers)
            .allMatch(item -> Objects.nonNull(item) && UTNumeric.isPositive(item));
    }

    /**
     * 检查 get 是否位于 min 和 max 之间的闭区间
     *
     * @param value 待检查的值
     * @param min   最小值
     * @param max   最大值
     *
     * @return 是否位于 min 和 max 之间的闭区间
     */
    public static boolean isIn(final Integer value, final Integer min, final Integer max) {
        return UTNumeric.isIn(value, min, max);
    }
}
