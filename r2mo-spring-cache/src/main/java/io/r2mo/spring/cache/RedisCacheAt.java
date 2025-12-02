package io.r2mo.spring.cache;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.typed.cc.CacheAtBase;
import io.r2mo.typed.cc.Cc;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存实现 (Raw RedisTemplate 版本)
 * 直接使用原始 RedisTemplate，不指定泛型，以获得最大兼容性
 *
 * @param <K> 键类型
 * @param <V> 值类型
 *
 * @author lang : 2025-12-02
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
class RedisCacheAt<K, V> extends CacheAtBase<K, V> {

    /**
     * 缓存管理器容器
     * 使用原始 RedisTemplate 类型
     */
    private static final Cc<String, RedisTemplate> CC_MANAGER = Cc.open();

    /**
     * Redis Key 分隔符
     */
    private static final String KEY_SEPARATOR = ":";

    RedisCacheAt(final String name, final Class<K> clazzK, final Class<V> clazzV) {
        super(name, clazzK, clazzV);
    }

    @Override
    protected boolean build() {
        this.managerOf();
        return true;
    }

    /**
     * 获取 RedisTemplate
     * 直接按类型获取，不关心泛型参数
     */
    private RedisTemplate managerOf() {
        return CC_MANAGER.pick(() -> {
            try {
                // 优先按名称获取，通常 Spring Boot 默认名称为 "redisTemplate"
                return SpringUtil.getBean("redisTemplate", RedisTemplate.class);
            } catch (final Exception e) {
                // 回退：按类型获取
                return SpringUtil.getBean(RedisTemplate.class);
            }
        }, this.manager());
    }

    /**
     * 构造 Redis Key
     */
    private String keyOf(final K key) {
        return this.name() + KEY_SEPARATOR + key.toString();
    }

    @Override
    public void doPut(final K key, final V value) {
        final String redisKey = this.keyOf(key);
        final RedisTemplate template = this.managerOf();

        if (this.duration != null && !this.duration.isZero()) {
            template.opsForValue().set(redisKey, value, this.duration.getSeconds(), TimeUnit.SECONDS);
        } else {
            template.opsForValue().set(redisKey, value);
        }
    }

    @Override
    public boolean doRemove(final K key) {
        final String redisKey = this.keyOf(key);
        return Boolean.TRUE.equals(this.managerOf().delete(redisKey));
    }

    @Override
    public V doFind(final K key) {
        final String redisKey = this.keyOf(key);
        final RedisTemplate template = this.managerOf();

        // 原始类型的 get 返回 Object
        final Object result = template.opsForValue().get(redisKey);

        if (result == null) {
            return null;
        }

        // 使用 this.classValue() 进行安全的类型转换
        // 解决 Redis 序列化 (如 JSON) 存的是 String 但 Java 需要 UUID/Integer 的情况
        return CacheUtil.convertValue(result, this.classValue());
    }

    @Override
    public void doClear() {
        final String pattern = this.name() + KEY_SEPARATOR + "*";
        final RedisTemplate template = this.managerOf();

        final Set keys = template.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            template.delete(keys);
        }
    }
}