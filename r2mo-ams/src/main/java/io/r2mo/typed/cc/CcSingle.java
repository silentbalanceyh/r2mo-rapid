package io.r2mo.typed.cc;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
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
        return this.getOrDefault(key, null);
    }

    @Override
    public V getOrDefault(final K key, final V defaultValue) {
        return this.store.getOrDefault(key, defaultValue);
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
    public Cc<K, V> put(final K key, final V value) {
        this.store.put(key, value);
        return this;
    }

    @Override
    public Cc<K, V> putAll(final Map<K, V> map) {
        this.store.putAll(map);
        return this;
    }

    @Override
    public Set<K> keySet() {
        return this.store.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.store.values();
    }

    @Override
    public boolean containsKey(final K key) {
        return this.store.containsKey(key);
    }

    @Override
    public boolean containsValue(final V value) {
        return this.store.containsValue(value);
    }

    @Override
    public boolean remove(final K key) {
        this.store.remove(key);
        return true;
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    @Override
    public void forEach(final BiConsumer<K, V> consumer) {
        this.store.forEach(consumer);
    }

    @Override
    public int size() {
        return this.store.size();
    }
}
