package io.r2mo.spring.cache;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Setter;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 修改 RedissonSpringCacheManager 的源码，重写 cacheName 处理方法，支持多参数模式
 *
 * @author lang : 2025-12-02
 */
@SuppressWarnings("all")
public class SpringCacheManager implements CacheManager {
    private static final RedissonClient CLIENT = SpringUtil.getBean(RedissonClient.class);
    Map<String, CacheConfig> configMap = new ConcurrentHashMap<>();
    ConcurrentMap<String, Cache> instanceMap = new ConcurrentHashMap<>();
    private boolean dynamic = true;
    @Setter
    private boolean allowNullValues = true;
    @Setter
    private boolean transactionAware = true;

    public SpringCacheManager() {
    }

    @SuppressWarnings("unchecked")
    public void setConfig(final Map<String, ? extends CacheConfig> config) {
        this.configMap = (Map<String, CacheConfig>) config;
    }

    protected CacheConfig createDefault() {
        return new CacheConfig();
    }

    private Cache createCache(final String name, final CacheConfig config, final int local) {
        final RMapCache<Object, Object> map = CLIENT.getMapCache(name);
        Cache cache = new RedissonCache(map, config, this.allowNullValues);
        if (1 == local) {
            cache = new CaffeineCacheDecorator(name, cache);
        }
        if (this.transactionAware) {
            cache = new TransactionAwareCacheDecorator(cache);
        }
        final Cache oldCache = this.instanceMap.putIfAbsent(name, cache);
        if (Objects.nonNull(oldCache)) {
            cache = oldCache;
        } else {
            if (Objects.nonNull(config)) {
                map.setMaxSize(config.getMaxSize());
            }
        }
        return cache;
    }

    @Override
    public Cache getCache(@NonNull final String name) {
        // 重写 cacheName 支持多参数
        final String[] array = StringUtils.delimitedListToStringArray(name, "#");
        final String cacheName = array[0];

        Cache cache = instanceMap.get(cacheName);
        if (Objects.nonNull(cache)) {
            return cache;
        }

        if (!dynamic) {
            return cache;
        }

        CacheConfig config = configMap.get(cacheName);
        if (Objects.isNull(config)) {
            config = createDefault();
            configMap.put(cacheName, config);
        }

        if (1 < array.length) {
            config.setTTL(DurationStyle.detectAndParse(array[1]).toMillis());
        }
        if (2 < array.length) {
            config.setMaxIdleTime(DurationStyle.detectAndParse(array[2]).toMillis());
        }
        if (3 < array.length) {
            config.setMaxSize(Integer.parseInt(array[3]));
        }
        int local = 1;
        if (4 < array.length) {
            local = Integer.parseInt(array[4]);
        }

        if (config.getMaxIdleTime() == 0 && config.getTTL() == 0 && config.getMaxSize() == 0) {
            return this.createCache(cacheName, null, local);
        }
        return this.createCache(cacheName, config, local);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.configMap.keySet());
    }

    public void setCacheNames(final Collection<String> names) {
        if (names != null) {
            for (final String name : names) {
                this.getCache(name);
            }
            this.dynamic = false;
        } else {
            this.dynamic = true;
        }
    }
}
