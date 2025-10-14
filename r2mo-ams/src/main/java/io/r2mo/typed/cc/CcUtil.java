package io.r2mo.typed.cc;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-08-27
 */
class CcUtil {

    private static final ConcurrentMap<String, Set<String>> KEY_MAP = new ConcurrentHashMap<>();

    static <V> V poolThread(final ConcurrentMap<String, V> pool, final Supplier<V> poolFn) {
        return poolThread(pool, poolFn, null);
    }

    static <V> V poolThread(final ConcurrentMap<String, V> pool, final Supplier<V> poolFn, final String marker) {
        final String keyOf = keyOf(marker);
        return pool(pool, keyOf, poolFn);
    }

    /**
     * 提取当前线程所有的 key set 集合
     *
     * @return key set 集合
     */
    static Set<String> keySet() {
        final String threadName = Thread.currentThread().getName();
        return KEY_MAP.getOrDefault(threadName, Set.of());
    }

    /**
     * 生成线程级的 key 值
     * <pre>
     *      1. marker = null or blank -> key       = thread "name"
     *      2. marker = not blank -> key           = thread "name@marker"
     *      说明：当 marker 不为空时，表示当前线程下有多个不同
     * </pre>
     *
     * @param marker 标记
     *
     * @return 线程级 key
     */
    static String keyOf(final String marker) {
        final String threadName = Thread.currentThread().getName();
        final String keyOf;
        if (marker == null || marker.isBlank()) {
            keyOf = threadName;
        } else {
            keyOf = threadName.concat("@").concat(marker);
            // 在线程的 Map 中追加 thread name = Set<String> 的记录
            KEY_MAP.computeIfAbsent(threadName, k -> ConcurrentHashMap.newKeySet()).add(marker);
        }
        return keyOf;
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
