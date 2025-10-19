package io.r2mo.base.util;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.util.List;
import java.util.Optional;

/**
 * @author lang : 2025-10-19
 */
@SuppressWarnings("unchecked")
class _UtilJson extends _UtilDate {
    private static final JUtil UT = SPI.V_UTIL;

    // 内容序列化
    public static <T, R> R serializeJ(final T input) {
        return Optional.ofNullable(UT.<T, JObject>serializeJson(input))
            .map(dataJ -> (R) (dataJ.data()))
            .orElse(null);
    }

    public static <T, R> R serializeA(final List<T> input) {
        return Optional.ofNullable(UT.<List<T>, JArray>serializeJson(input))
            .map(dataJ -> (R) (dataJ.data()))
            .orElse(null);
    }

    // 内容反序列化
    public static <T, R> T deserializeJ(final R input, final Class<T> targetCls) {
        final JObject dataJ = SPI.J(input);
        return UT.deserializeJson(dataJ, targetCls);
    }

    public static <T, R> List<T> deserializeA(final R input, final Class<T> elementCls) {
        final JArray dataA = SPI.A(input);
        return UT.deserializeJson(dataA, elementCls);
    }
}
