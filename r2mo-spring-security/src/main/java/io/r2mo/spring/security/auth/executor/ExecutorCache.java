package io.r2mo.spring.security.auth.executor;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.enums.UserIDType;
import io.r2mo.spring.security.config.ConfigSecurity;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-11-11
 */
public class ExecutorCache {
    private static final String NAME_AUTHORIZE = "CACHE_AUTHORIZE";
    private static ExecutorCache MANAGER;
    private final ConfigSecurity configuration;
    private static final ConcurrentMap<UserIDType, CacheManager> CACHE_MANAGER = new ConcurrentHashMap<>();

    private ExecutorCache() {
        this.configuration = SpringUtil.getBean(ConfigSecurity.class);
    }

    public static ExecutorCache of() {
        if (MANAGER == null) {
            MANAGER = new ExecutorCache();
        }
        return MANAGER;
    }

    public Cache<String, String> getOrCreate(final UserIDType type) {
        final String name = NAME_AUTHORIZE + "@" + type.name();
        final CacheManager manager = CACHE_MANAGER.computeIfAbsent(type, idType -> configure(name));
        return manager.getCache(name, String.class, String.class);
    }

    @SuppressWarnings("all")
    // ehcache
    private static CacheManager configure(final String name) {
        return CacheManagerBuilder.newCacheManagerBuilder()
            .withCache(name, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                    String.class, String.class,
                    ResourcePoolsBuilder.heap(20000)   // 默认缓存 20000 个对象
                )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                    Duration.of(60, ChronoUnit.SECONDS)
                ))
            ).build(true);
    }
}
