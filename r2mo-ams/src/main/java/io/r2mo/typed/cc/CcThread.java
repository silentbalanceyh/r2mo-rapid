package io.r2mo.typed.cc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-08-27
 */
class CcThread<V> implements Cc<String, V> {

    private final ConcurrentMap<String, V> store = new ConcurrentHashMap<>();

    @Override
    public ConcurrentMap<String, V> get() {
        return this.store;
    }

    @Override
    public V get(final String key) {
        return this.store.getOrDefault(key, null);
    }

    @Override
    public V pick(final Supplier<V> supplier) {
        return CcUtil.poolThread(this.store, supplier);
    }

    @Override
    public V pick(final Supplier<V> supplier, final String key) {
        return CcUtil.poolThread(this.store, supplier, key);
    }

    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    @Override
    public boolean remove(final String key) {
        this.store.remove(key);
        return true;
    }
}
