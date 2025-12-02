package io.r2mo.spring.cache;

import java.util.UUID;

/**
 * @author lang : 2025-12-02
 */
class CacheUtil {

    @SuppressWarnings("unchecked")
    static <V> V convertValue(final Object result, final Class<V> targetClass) {
        // 1. 类型完全匹配
        if (targetClass.isInstance(result)) {
            return (V) result;
        }

        // 2. 特殊处理 UUID
        if (targetClass == UUID.class && result instanceof String) {
            return (V) UUID.fromString((String) result);
        }

        // 3. 特殊处理基础数字类型 (防止 JSON 解析为 String 或错误的数字类型)
        if (result instanceof final String str) {
            if (targetClass == Integer.class) {
                return (V) Integer.valueOf(str);
            }
            if (targetClass == Long.class) {
                return (V) Long.valueOf(str);
            }
            if (targetClass == Boolean.class) {
                return (V) Boolean.valueOf(str);
            }
            if (targetClass == Double.class) {
                return (V) Double.valueOf(str);
            }
        }

        try {
            return (V) result;
        } catch (final ClassCastException e) {
            System.err.println("RedissionCacheAt Type Mismatch: Expected " + targetClass.getName() + " but got " + result.getClass().getName());
            return null;
        }
    }
}
