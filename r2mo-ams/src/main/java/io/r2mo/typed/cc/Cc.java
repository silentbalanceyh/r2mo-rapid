package io.r2mo.typed.cc;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-08-27
 */
public interface Cc<K, V> {

    static <K, V> V pool(final ConcurrentMap<K, V> input, final K key, final Supplier<V> supplier) {
        return CcUtil.pool(input, key, supplier);
    }

    static <V> V poolThread(final ConcurrentMap<String, V> input, final Supplier<V> supplier) {
        return CcUtil.poolThread(input, supplier);
    }

    static <K, V> Cc<K, V> open() {
        return new CcSingle<>();
    }

    static <V> Cc<String, V> openThread() {
        return new CcThread<>();
    }

    ConcurrentMap<K, V> get();

    V get(K key);

    V pick(Supplier<V> supplier);

    V pick(Supplier<V> supplier, K key);

    boolean isEmpty();
}
