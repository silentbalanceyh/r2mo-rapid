package io.r2mo.typed.cc;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-08-27
 */
class CcUtil {

    static <V> V poolThread(final ConcurrentMap<String, V> pool, final Supplier<V> poolFn) {
        return poolThread(pool, poolFn, null);
    }

    static <V> V poolThread(final ConcurrentMap<String, V> pool, final Supplier<V> poolFn, final String marker) {
        final String threadName = Thread.currentThread().getName();
        final String keyOf;
        if (marker == null || marker.isBlank()) {
            keyOf = threadName;
        } else {
            keyOf = threadName.concat("@").concat(marker);
        }
        return pool(pool, keyOf, poolFn);
    }

    static <K, V> V pool(final ConcurrentMap<K, V> pool, final K key, final Supplier<V> poolFn) {
        Objects.requireNonNull(pool, "[ R2MO ] pool 参数不可为空！");
        Objects.requireNonNull(key, "[ R2MO ] key 参数不可为空！");
        Objects.requireNonNull(poolFn, "[ R2MO ] poolFn 参数不可为空！");

        V value = pool.get(key);
        if (Objects.isNull(value)) {
            value = poolFn.get();
            if (Objects.nonNull(value)) {
                pool.put(key, value);
            }
        }
        return value;
    }
}
