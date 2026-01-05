package io.r2mo.vertx.common.cache;

import io.r2mo.typed.cc.CacheAt;
import io.r2mo.typed.common.Kv;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 异步缓存，等同于 {@link CacheAt} 的实现的异步版本，缓存构造时自带初始化参数机制，所以 ttl 这种操作就不用体现在方法中了，
 * 整体参考 {@link io.r2mo.vertx.common.cache} 中的设计说明，Vertx 中只能使用 MemoAt 来实现缓存功能而不能直接使用
 * {@link CacheAt}，原因在于 {@link CacheAt} 是同步接口，而 Vertx 框架中所有的操作都应该是异步的。
 *
 * @author lang : 2026-01-01
 */
public interface MemoAt<K, V> {

    String name();

    Future<Kv<K, V>> put(K key, V value);

    default Future<Kv<K, V>> put(final Kv<K, V> kv) {
        return this.put(kv.key(), kv.value());
    }

    Future<Kv<K, V>> remove(K key);

    default Future<Boolean> remove(final Set<K> keys) {
        final Set<Future<Kv<K, V>>> futures = new HashSet<>();
        keys.forEach(key -> futures.add(this.remove(key)));
        return FnVertx.combineB(futures);
    }

    Future<V> find(K key);

    default Future<ConcurrentMap<K, V>> find(final Set<K> keys) {
        final ConcurrentMap<K, Future<V>> futureMap = new ConcurrentHashMap<>();
        keys.forEach(key -> futureMap.put(key, this.find(key)));
        return FnVertx.combineM(futureMap);
    }

    Future<Boolean> clear();

    Future<Set<K>> keySet();

    Future<Integer> size();
}
