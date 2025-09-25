package io.r2mo.base.util;

import io.r2mo.typed.json.JObject;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author lang : 2025-09-25
 */
class UTJson {
    @SuppressWarnings("unchecked")
    static <T> T valueT(final JObject jsonJ, final String field, final Supplier<T> constructorFn) {
        if (Objects.isNull(jsonJ)) {
            return constructorFn.get();
        }
        T value = null;
        if (jsonJ.containsKey(field)) {
            // 强转会报错
            value = (T) jsonJ.get(field);
        }
        if (Objects.isNull(value)) {
            value = constructorFn.get();
        }
        return value;
    }

    static <T> T valueT(final T value, final Supplier<T> constructorFn) {
        if (Objects.isNull(value)) {
            return constructorFn.get();
        }
        return value;
    }
}
