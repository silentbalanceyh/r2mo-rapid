package io.r2mo.typed.cc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-08-27
 */
class CcSingle<K, V> implements Cc<K, V> {
    private final ConcurrentMap<K, V> store = new ConcurrentHashMap<>();

    @Override
    public ConcurrentMap<K, V> get() {
        return this.store;
    }

    @Override
    public V get(final K key) {
        return this.store.getOrDefault(key, null);
    }

    @Override
    public V pick(final Supplier<V> supplier) {
        throw new UnsupportedOperationException("[ R2MO ] 该方法调用不支持！");
    }

    @Override
    public V pick(final Supplier<V> supplier, final K key) {
        return CcUtil.pool(this.store, key, supplier);
    }

    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    @Override
    public boolean remove(final K key) {
        this.store.remove(key);
        return true;
    }
}
