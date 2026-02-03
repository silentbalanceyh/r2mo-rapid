package io.r2mo.base.util;

import io.r2mo.typed.json.JObject;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Ams 工具类 Tool
 *
 * @author lang : 2025-09-20
 */
public class R2MO extends _UtilProp {

    public static String uiDate(final long value, final TimeUnit unit) {
        return UTDisplay.uiDate(value, unit);
    }

    public static <T> T valueT(final T value, final Supplier<T> constructorFn) {
        return UTJson.valueT(value, constructorFn);
    }

    public static <T> T valueT(final JObject jsonJ, final String field, final Supplier<T> constructorFn) {
        return UTJson.valueT(jsonJ, field, constructorFn);
    }

    public static <T> T valueT(final JObject jsonJ, final String field, final T defaultValue) {
        return UTJson.valueT(jsonJ, field, () -> defaultValue);
    }

    public static <T> T valueT(final JObject jsonJ, final String field) {
        return UTJson.valueT(jsonJ, field, () -> null);
    }

    public static byte[] serialize(final Object object) {
        return UTJvm.serialize(object);
    }

    public static <T> T deserialize(final byte[] bytes) {
        return UTJvm.deserialize(bytes);
    }

    public static Collection<?> toCollection(final Object obj) {
        return UTType.toCollection(obj);
    }

    public static boolean isCollection(final Object obj) {
        return UTType.isCollection(obj);
    }

    /**
     * 扩展参数模式，isBoolean 可检查 Object 类型，去空
     *
     * @param input 输入对象
     * @return 是否合法Boolean值
     */
    public static boolean isBoolean(final Object input) {
        return Objects.nonNull(input)
            && isBoolean(input.toString().trim().intern());
    }


    /**
     * （默认非宽松模式）检查传入字符串是否合法Boolean值
     *
     * @param literal 字符串
     * @return 是否合法Boolean值
     */
    public static boolean isBoolean(final String literal) {
        return UTType.isBoolean(literal, false);
    }

    /**
     * 检查传入字符串是否合法Boolean值
     * 1. widely = true 时，支持 "true" / "false" / "1" / "0" / "yes" / "no" / "y" / "n"
     * 2. widely = false 时，支持 "true" / "false"
     *
     * @param literal 字符串
     * @param widely  是否宽松模式
     * @return 是否合法Boolean值
     */
    public static boolean isBoolean(final String literal, final boolean widely) {
        return UTType.isBoolean(literal, widely);
    }

    /**
     * 是否是合法 Java 标识符
     *
     * @param name 名称
     * @return 是否合法
     */
    public static boolean isNamedJava(final String name) {
        return UTName.isNamedJava(name);
    }

    public static boolean isNamedSQL(final String name) {
        return UTName.isNamedSQL(name);
    }

    /**
     * 简单验证电子邮件地址格式
     *
     * @param email 待验证的字符串
     * @return 如果字符串符合基本电子邮件格式，则返回 true；否则返回 false。
     */
    public static boolean isEmail(final String email) {
        return UTIs.isEmail(email);
    }

    /**
     * 验证中国大陆手机号码格式
     *
     * @param mobile 待验证的字符串
     * @return 如果字符串符合中国大陆手机号码格式，则返回 true；否则返回 false。
     */
    public static boolean isMobile(final String mobile) {
        return UTIs.isMobile(mobile);
    }

    /**
     * 扩展参数模式，isInvalid 可检查是否非法字符串，广域检查
     *
     * @param literal 字符串
     * @return 是否非法字符串
     */
    public static boolean isInvalid(final String literal) {
        return UTIs.isInvalid(literal);
    }
}
