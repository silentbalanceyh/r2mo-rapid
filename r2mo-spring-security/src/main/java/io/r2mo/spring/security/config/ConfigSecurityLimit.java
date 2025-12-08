package io.r2mo.spring.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author lang : 2025-11-12
 */
@Configuration
@ConfigurationProperties(prefix = "security.limit")
@Data
@RefreshScope
public class ConfigSecurityLimit implements Serializable {
    private long session = 8192;
    private long token = 4096;
    private long timeout = 120;
    private long authorize = 2048;
}
