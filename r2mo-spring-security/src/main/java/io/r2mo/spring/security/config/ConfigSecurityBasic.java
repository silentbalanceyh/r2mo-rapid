package io.r2mo.spring.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author lang : 2025-11-11
 */
@Configuration
@ConfigurationProperties(prefix = "security.basic")
@Data
@RefreshScope
public class ConfigSecurityBasic implements Serializable {
    private String realm;
}
