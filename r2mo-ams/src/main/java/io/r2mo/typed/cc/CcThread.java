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
        return this.store.getOrDefault(key, null);
    }

    @Override
    public V getOrDefault(final String key, final V defaultValue) {
        throw new UnsupportedOperationException("[ R2MO ] 线程级不支持 getOrDefault(String, V) 方法调用！");
    }

    @Override
    public Cc<String, V> put(final String key, final V value) {
        throw new UnsupportedOperationException("[ R2MO ] 线程级不支持 put(String, V) 方法调用！");
    }

    @Override
    public Cc<String, V> putAll(final Map<String, V> map) {
        throw new UnsupportedOperationException("[ R2MO ] 线程级不支持 putAll(Map<String, V>) 方法调用！");
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
        throw new UnsupportedOperationException("[ R2MO ] 线程级不支持 keySet() 方法调用！");
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException("[ R2MO ] 线程级不支持 values() 方法调用！");
    }

    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    @Override
    public boolean containsKey(final String key) {
        final String keyOf = CcUtil.poolKey(key);
        return this.store.containsKey(keyOf);
    }

    @Override
    public boolean containsValue(final V value) {
        return this.store.containsValue(value);
    }

    @Override
    public boolean remove(final String key) {
        this.store.remove(key);
        return true;
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    @Override
    public void forEach(final BiConsumer<String, V> consumer) {
        throw new UnsupportedOperationException("[ R2MO ] 线程级不支持 forEach(BiConsumer<String, V>) 方法调用！");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("[ R2MO ] 线程级不支持 size() 方法调用！");
    }
}
