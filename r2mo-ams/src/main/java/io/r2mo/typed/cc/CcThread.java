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
class CcThread<V> implements Cc<String, V> {

    private final ConcurrentMap<String, V> store = new ConcurrentHashMap<>();

    @Override
    public ConcurrentMap<String, V> get() {
        return this.store;
    }

    @Override
    public V get(final String key) {
        return this.getOrDefault(key, null);
    }

    @Override
    public V getOrDefault(final String key, final V defaultValue) {
        final String keyOf = CcUtil.keyOf(key);
        return this.store.getOrDefault(keyOf, defaultValue);
    }

    @Override
    public Cc<String, V> put(final String key, final V value) {
        final String keyOf = CcUtil.keyOf(key);
        this.store.put(keyOf, value);
        return this;
    }

    @Override
    public Cc<String, V> putAll(final Map<String, V> map) {
        map.forEach(this::put);
        return this;
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
    public Set<String> keySet() {
        return CcUtil.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.store.values();
    }

    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    @Override
    public boolean containsKey(final String key) {
        final String keyOf = CcUtil.keyOf(key);
        return this.store.containsKey(keyOf);
    }

    @Override
    public boolean containsValue(final V value) {
        return this.store.containsValue(value);
    }

    @Override
    public boolean remove(final String key) {
        this.store.remove(CcUtil.keyOf(key));
        return true;
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    @Override
    public void forEach(final BiConsumer<String, V> consumer) {
        // 此处的 key, value 处理中的 key 应该是当前线程关联的 key
        this.keySet().forEach(key -> consumer.accept(key, this.get(key)));
    }

    @Override
    public int size() {
        return this.keySet().size();
    }
}
