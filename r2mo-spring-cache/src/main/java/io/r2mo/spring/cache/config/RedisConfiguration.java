package io.r2mo.spring.cache.config;

import cn.hutool.extra.spring.SpringUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.r2mo.spring.cache.SpringCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author lang : 2025-12-02
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ConfigCache.class)
public class RedisConfiguration {

    private final RedisConnectionFactory factory;
    @Autowired
    private ConfigCache configuration;

    public RedisConfiguration() {
        this.factory = SpringUtil.getBean(RedisConnectionFactory.class);
    }

    @Bean
    @SuppressWarnings("all")
    public Cache<String, Object> cacheCaffeine() {
        if (Objects.isNull(this.configuration) || Objects.isNull(this.configuration.getCaffeine())) {
            return null;
        }
        final ConfigCacheCaffeine caffeineConfig = this.configuration.getCaffeine();
        log.info("[ R2MO ] 初始化 Caffeine Cache , Expired : {} s , Capacity : {} , Maximum Size : {} ",
            caffeineConfig.getExpired(), caffeineConfig.getCapacity(), caffeineConfig.getMaximumSize());
        return Caffeine.newBuilder()
            // 设置过期时间
            .expireAfterWrite(caffeineConfig.getExpired(), TimeUnit.SECONDS)
            // 初始化缓存空间大小
            .initialCapacity(caffeineConfig.getCapacity())
            // 最大缓存数量
            .maximumSize(caffeineConfig.getMaximumSize())
            // 权重设置：.maximumWeight()
            .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(this.factory);

        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());

        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 自定义缓存管理器，整合 spring-cache
     *
     * @return 自定义管理器
     */
    @Bean
    public CacheManager cacheManager() {
        return new SpringCacheManager();
    }
}
