package io.r2mo.spring.cache.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * Redisson 配置类
 * <pre>
 *   cache:
 *     caffeine:
 *       expired:          // SECONDS
 *       capacity:         // 最大缓存数量
 *       maximum-size:     // 最大缓存大小
 *     redisson:
 *       prefix:
 *       threads: 16
 *       threads-netty: 32
 *       config-server:
 *         client-name: r2mo-redis-client
 *         connection-idle-size: 10
 *         connection-pool-size: 64
 *         timeout-idle: 10000
 *         timeout: 3000
 *         subscription-pool-size: 50
 *       config-cluster:
 *         master-idle-size: 10
 *         master-pool-size: 64
 *         slave-idle-size: 10
 *         slave-pool-size: 64
 *         client-name: r2mo-redis-cluster-client
 *         timeout-idle: 10000
 *         timeout: 3000
 *         subscription-pool-size: 50
 *         mode-read: SLAVE
 *         mode-subscription: SLAVE
 * </pre>
 *
 * @author lang : 2025-12-02
 */
@Data
@ConfigurationProperties(prefix = "cache")
@Configuration
public class ConfigCache implements Serializable {

    private ConfigCacheRedission redisson;

    private ConfigCacheCaffeine caffeine;
}
