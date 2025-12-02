package io.r2mo.spring.cache;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.r2mo.typed.cc.CacheAtBase;
import io.r2mo.typed.cc.Cc;

/**
 * Caffeine 本地缓存实现
 * 不需要序列化，直接存储对象引用，性能极高
 *
 * @param <K> 键类型
 * @param <V> 值类型
 *
 * @author lang : 2025-12-02
 */
@SuppressWarnings("all")
class CaffeineCacheAt<K, V> extends CacheAtBase<K, V> {

    /**
     * 静态容器，用于持有所有名称的 Caffeine Cache 实例
     * 避免重复创建 Cache 实例
     * Key: Cache Name, Value: Cache Instance
     */
    private static final Cc<String, Cache> CC_CACHE = Cc.open();

    CaffeineCacheAt(final String name, final Class<K> clazzK, final Class<V> clazzV) {
        super(name, clazzK, clazzV);
    }

    @Override
    protected boolean build() {
        // 初始化时构建 Cache 实例
        this.cacheOf();
        return false;
    }

    /**
     * 获取或构建 Caffeine Cache 实例
     */
    @SuppressWarnings("unchecked")
    private Cache<K, V> cacheOf() {
        // 使用 this.name() 作为 Key，确保同名缓存共享同一个 Caffeine 实例
        return (Cache<K, V>) CC_CACHE.pick(() -> {
            Caffeine<Object, Object> builder = Caffeine.newBuilder();

            // 1. 配置最大容量 (Size)
            if (this.size > 0) {
                builder.maximumSize(this.size);
            }

            // 2. 配置过期时间 (TTL)
            if (this.duration != null && !this.duration.isZero()) {
                builder.expireAfterWrite(this.duration);
            }

            // 构建 Cache
            return builder.build();
        }, this.name());
    }

    @Override
    public void doPut(final K key, final V value) {
        this.cacheOf().put(key, value);
    }

    @Override
    public boolean doRemove(final K key) {
        this.cacheOf().invalidate(key);
        return true;
    }

    @Override
    public V doFind(final K key) {
        // 本地缓存直接返回对象，无需类型转换
        return this.cacheOf().getIfPresent(key);
    }

    @Override
    public void doClear() {
        this.cacheOf().invalidateAll();
    }
}
