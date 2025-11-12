package io.r2mo.typed.cc;

import io.r2mo.typed.common.Kv;

import java.time.Duration;

/**
 * 设置可超时的简易缓存接口
 *
 * @author lang : 2025-11-12
 */
public interface CacheAt<K, V> {

    String name();

    void configure(Duration duration, long size);

    void put(K key, V value);

    default void put(final Kv<K, V> kv) {
        this.put(kv.key(), kv.value());
    }

    boolean remove(K key);

    V find(K key);

    void clear();

    boolean isOk();
}
