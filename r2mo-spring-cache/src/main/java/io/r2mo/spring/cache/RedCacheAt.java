package io.r2mo.spring.cache;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.typed.cc.CacheAtBase;
import io.r2mo.typed.cc.Cc;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * Redisson 缓存实现
 * 利用 Redisson 的 RMapCache 实现分组缓存和过期控制
 *
 * @param <K> 键类型
 * @param <V> 值类型
 *
 * @author lang : 2025-12-02
 */
@SuppressWarnings({"all", "unchecked"})
class RedCacheAt<K, V> extends CacheAtBase<K, V> {

    /**
     * Redisson 客户端容器
     * 静态持有，确保只查找一次 Bean
     */
    private static final Cc<String, RedissonClient> CC_MANAGER = Cc.open();

    RedCacheAt(final String name, final Class<K> clazzK, final Class<V> clazzV) {
        super(name, clazzK, clazzV);
    }

    @Override
    protected boolean build() {
        // 初始化时预加载 Manager
        this.managerOf();
        return false;
    }

    /**
     * 获取 RedissonClient Bean
     */
    private RedissonClient managerOf() {
        return CC_MANAGER.pick(() -> {
            try {
                // 优先尝试按类型获取
                return SpringUtil.getBean(RedissonClient.class);
            } catch (Exception e) {
                // 如果失败，尝试按默认 Bean 名称获取
                return SpringUtil.getBean("redissonClient", RedissonClient.class);
            }
        }, this.manager());
    }

    /**
     * 获取带过期支持的 Map (RMapCache)
     * Redisson 使用 Name 作为 Redis Key (Hash结构)，内部 Key 作为 HashKey
     */
    private RMapCache<K, V> cacheOf() {
        return this.managerOf().getMapCache(this.name());
    }

    @Override
    public void doPut(final K key, final V value) {
        final RMapCache<K, V> cache = this.cacheOf();

        // RMapCache 支持对单个元素设置 TTL
        if (this.duration != null && !this.duration.isZero()) {
            cache.put(key, value, this.duration.getSeconds(), TimeUnit.SECONDS);
        } else {
            cache.put(key, value);
        }
    }

    @Override
    public boolean doRemove(final K key) {
        // fastRemove 返回删除的数量，不返回旧值，性能稍好
        return this.cacheOf().fastRemove(key) > 0;
    }

    @Override
    public V doFind(final K key) {
        // Redisson 的 get 操作
        Object result = this.cacheOf().get(key);

        if (result == null) {
            return null;
        }

        // 使用 convertValue 确保类型安全 (如处理 UUID/Integer 序列化丢失问题)
        return CacheUtil.convertValue(result, this.classValue());
    }

    @Override
    public void doClear() {
        // 直接删除整个 Map (对应的 Redis Key)
        // 相比 RedisTemplate 的 keys + delete，这里是 O(1) 操作 (unlink)
        this.cacheOf().delete();
    }
}