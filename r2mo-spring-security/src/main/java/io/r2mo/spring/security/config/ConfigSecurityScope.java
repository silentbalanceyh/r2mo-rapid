package io.r2mo.spring.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lang : 2025-11-11
 */
@Configuration
@ConfigurationProperties(prefix = "security.scope")
@Data
public class ConfigSecurityScope {

    private boolean app = false;
    private boolean tenant = false;
}
