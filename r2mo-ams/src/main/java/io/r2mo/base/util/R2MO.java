package io.r2mo.base.util;

import io.r2mo.typed.json.JObject;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Ams 工具类 Tool
 *
 * @author lang : 2025-09-20
 */
public class R2MO extends _UtilDate {

    public static <T> T valueT(final T value, final Supplier<T> constructorFn) {
        return UTJson.valueT(value, constructorFn);
    }

    public static <T> T valueT(final JObject jsonJ, final String field, final Supplier<T> constructorFn) {
        return UTJson.valueT(jsonJ, field, constructorFn);
    }

    public static byte[] serialize(final Object object) {
        return UTJvm.serialize(object);
    }

    public static <T> T deserialize(final byte[] bytes) {
        return UTJvm.deserialize(bytes);
    }

    public static Collection<?> toCollection(final Object obj) {
        return UTTrans.toCollection(obj);
    }
}
