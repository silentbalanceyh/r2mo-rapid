package io.r2mo.typed.cc;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
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

    V getOrDefault(K key, V defaultValue);

    Cc<K, V> put(K key, V value);

    Cc<K, V> putAll(Map<K, V> map);

    V pick(Supplier<V> supplier);

    V pick(Supplier<V> supplier, K key);

    Set<K> keySet();

    Collection<V> values();

    boolean isEmpty();

    boolean containsKey(K key);

    boolean containsValue(V value);

    boolean remove(K key);

    void clear();

    void forEach(BiConsumer<K, V> consumer);

    int size();

    <J> J mom();

    boolean momThread();
}
