package io.r2mo.spring.cache;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * Cache 装饰器模式（扩展 Caffeine 一级缓存）
 *
 * @author lang : 2025-12-02
 */
@SuppressWarnings("all")
public class CaffeineCacheDecorator implements Cache {
    private static final com.github.benmanes.caffeine.cache.Cache<Object, Object> CAFFEINE = SpringUtil.getBean("caffeine");

    private final String name;
    private final Cache cache;

    public CaffeineCacheDecorator(final String name, final Cache cache) {
        this.name = name;
        this.cache = cache;
    }

    public String key(final Object key) {
        return this.name + ":" + key;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.cache.getNativeCache();
    }

    @Override
    public ValueWrapper get(final Object key) {
        final Object o = CAFFEINE.get(this.key(key), k -> this.cache.get(key));
        return (ValueWrapper) o;
    }

    @Override
    public <T> T get(final Object key, final Class<T> type) {
        final Object o = CAFFEINE.get(this.key(key), k -> this.cache.get(key, type));
        return (T) o;
    }

    @Override
    public <T> T get(final Object key, final Callable<T> valueLoader) {
        final Object o = CAFFEINE.get(this.key(key), k -> this.cache.get(key, valueLoader));
        return (T) o;
    }

    @Override
    public void put(final Object key, final Object value) {
        CAFFEINE.invalidate(this.key(key));
        this.cache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(final Object key, final Object value) {
        CAFFEINE.invalidate(this.key(key));
        return this.cache.putIfAbsent(key, value);
    }

    @Override
    public void evict(final Object key) {
        this.evictIfPresent(key);
    }

    @Override
    public boolean evictIfPresent(final Object key) {
        final boolean b = this.cache.evictIfPresent(key);
        if (b) {
            CAFFEINE.invalidate(this.key(key));
        }
        return b;
    }

    @Override
    public boolean invalidate() {
        return this.cache.invalidate();
    }

    @Override
    public void clear() {
        CAFFEINE.invalidateAll();
        this.cache.clear();
    }
}
