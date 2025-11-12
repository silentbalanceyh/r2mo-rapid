package io.r2mo.spring.security.extension.cache;

import io.r2mo.typed.cc.CacheAtBase;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.util.Objects;

/**
 * @author lang : 2025-11-12
 */
@SuppressWarnings("all")
public class CacheAtEhcache<K, V> extends CacheAtBase<K, V> {
    private static CacheManager MANAGER;

    CacheAtEhcache(final String name) {
        super(name);
    }

    @Override
    protected boolean build() {
        // 创建缓存
        this.managerOf();

        return false;
    }

    private CacheManager managerOf() {
        if (Objects.isNull(MANAGER)) {
            final Class<K> clazzK = this.classKey();
            final Class<V> clazzV = this.classValue();
            // 转换成分钟
            MANAGER = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(this.name(), CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(
                        clazzK, clazzV,
                        ResourcePoolsBuilder.heap(this.size)
                    )
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(this.duration))
                )
                .build(true);
        }
        return MANAGER;
    }

    private Cache<K, V> cacheOf() {
        return this.managerOf().getCache(this.name(), this.classKey(), this.classValue());
    }

    @Override
    public void put(final K key, final V value) {
        this.cacheOf().put(key, value);
    }

    @Override
    public boolean remove(final K key) {
        this.cacheOf().remove(key);
        return false;
    }

    @Override
    public V find(final K key) {
        return this.cacheOf().get(key);
    }

    @Override
    public void clear() {
        this.cacheOf().clear();
    }
}
