package io.r2mo.spring.security.extension.cache;

import io.r2mo.typed.cc.CacheAtBase;
import io.r2mo.typed.cc.Cc;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 * @author lang : 2025-11-12
 */
@SuppressWarnings("all")
public class CacheAtEhcache<K, V> extends CacheAtBase<K, V> {
    private final Class<K> clazzK;
    private final Class<V> clazzV;
    /**
     * 缓存管理器，此处必须是静态的，否则无法达到共享的目的
     */
    private static Cc<String, CacheManager> CC_MANAGER = Cc.open();

    CacheAtEhcache(final String name, final Class<K> clazzK, final Class<V> clazzV) {
        super(name);
        this.clazzK = clazzK;
        this.clazzV = clazzV;
    }

    private Class<K> classKey() {
        return this.clazzK;
    }

    private Class<V> classValue() {
        return this.clazzV;
    }

    @Override
    protected boolean build() {
        // 创建缓存
        this.managerOf();

        return false;
    }

    private CacheManager managerOf() {
        return CC_MANAGER.pick(() -> {
            final Class<K> clazzK = this.classKey();
            final Class<V> clazzV = this.classValue();
            return CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(this.name(), CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(
                        clazzK, clazzV,
                        ResourcePoolsBuilder.heap(this.size)
                    )
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(this.duration))
                )
                .build(true);
        }, this.name());
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
