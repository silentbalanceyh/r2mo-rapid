package io.r2mo.spring.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author lang : 2025-12-02
 */
@Data
@ConfigurationProperties(prefix = "cache.caffeine")
@Configuration
public class ConfigCacheCaffeine implements Serializable {

    private long expired = 30;  // 30s
    private int capacity = 1000; // 最大缓存数量
    private long maximumSize = 10 * 1024; // 最大缓存大小
}
