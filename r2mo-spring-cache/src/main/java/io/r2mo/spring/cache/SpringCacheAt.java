package io.r2mo.spring.cache;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.typed.cc.CacheAtBase;
import io.r2mo.typed.cc.Cc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Spring Cache 适配实现
 * <p>
 * 该实现作为一个适配器，将操作委托给 Spring 的 {@link CacheManager}。
 * 它会根据 {@link CacheAtBase} 中的配置（TTL、Size），
 * 按照 {@link SpringCacheManager} 约定的格式（name#ttl#idle#size#local）生成缓存名称，
 * 从而实现动态配置底层缓存（如 Redisson）。
 * </p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 *
 * @author lang : 2025-12-02
 */
@SuppressWarnings("all")
class SpringCacheAt<K, V> extends CacheAtBase<K, V> {

    /**
     * CacheManager 容器
     */
    private static final Cc<String, CacheManager> CC_MANAGER = Cc.open();
    private static final String DEFAULT_MANAGER = "r2mo-default-cache-manager";

    /**
     * 格式化后的缓存名称（包含配置信息）
     */
    private String formattedName;

    SpringCacheAt(final String name, final Class<K> clazzK, final Class<V> clazzV) {
        super(name, clazzK, clazzV);
    }

    @Override
    protected boolean build() {
        // 1. 预先构建格式化名称，避免每次操作都拼接字符串
        this.formattedName = this.formatCacheName();
        // 2. 尝试初始化 Manager
        this.managerOf();
        return true;
    }

    /**
     * 获取 Spring CacheManager
     */
    private CacheManager managerOf() {
        return CC_MANAGER.pick(() -> SpringUtil.getBean(CacheManager.class), DEFAULT_MANAGER);
    }

    /**
     * 获取具体的 Spring Cache 实例
     */
    private Cache cacheOf() {
        // 使用格式化后的名称获取 Cache，触发 SpringCacheManager 的解析逻辑
        return this.managerOf().getCache(this.formattedName);
    }

    /**
     * 根据 SpringCacheManager 的规则构造名称
     * 格式：name#ttl#maxIdle#maxSize#local
     */
    private String formatCacheName() {
        final StringBuilder sb = new StringBuilder(this.name());

        // 1. TTL (Duration) -> Array[1]
        // 转换为毫秒字符串，例如 "10000ms"
        if (this.duration != null && !this.duration.isZero()) {
            sb.append("#").append(this.duration.toMillis()).append("ms");
        } else {
            sb.append("#0"); // 0 表示使用默认或不设置
        }

        // 2. MaxIdle -> Array[2]
        // CacheAtBase 暂无 MaxIdle 属性，默认传 0
        sb.append("#0");

        // 3. MaxSize -> Array[3]
        if (this.size > 0) {
            sb.append("#").append(this.size);
        } else {
            sb.append("#0");
        }

        // 4. Local (是否开启本地缓存/Caffeine) -> Array[4]
        // 这里可以根据业务扩展 CacheAtBase 增加 local 属性，目前默认 0 (不强制开启，除非 CacheManager 默认策略)
        // 如果希望默认开启一级缓存，可以改为 1
        sb.append("#0");

        return sb.toString();
    }

    @Override
    public void doPut(final K key, final V value) {
        this.cacheOf().put(key, value);
    }

    @Override
    public boolean doRemove(final K key) {
        this.cacheOf().evict(key);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V doFind(final K key) {
        final Cache cache = this.cacheOf();

        // 利用 Spring Cache 的 get(key, type) 进行类型安全转换
        // 如果 CacheManager 底层配置了 JSON 序列化，这里会自动反序列化
        try {
            return cache.get(key, this.classValue());
        } catch (Exception e) {
            // 如果底层反序列化失败或类型不匹配，降级处理
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper == null) {
                return null;
            }
            return (V) wrapper.get();
        }
    }

    @Override
    public void doClear() {
        this.cacheOf().clear();
    }
}
