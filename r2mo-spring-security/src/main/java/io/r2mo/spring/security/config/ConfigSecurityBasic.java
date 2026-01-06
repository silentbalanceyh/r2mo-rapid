package io.r2mo.spring.security.config;

import io.r2mo.base.util.R2MO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author lang : 2025-11-11
 */
@Configuration
@ConfigurationProperties(prefix = "security.basic")
@Data
@RefreshScope
public class ConfigSecurityBasic implements Serializable {
    private String realm = "Realm Default R2MO";
    private boolean enabled = true;
    private String aesSecret = "S9VJJCYWYZLJGZNGKKQN32UVMCJUU8KZ";
    private String aesExpiredAt = "2h";

    public Duration expiredAesAt() {
        return R2MO.toDuration(this.aesExpiredAt);
    }
}
