package io.r2mo.spring.security.config;

import io.r2mo.base.util.R2MO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author lang : 2025-11-10
 */
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class ConfigSecurityJwt implements Serializable {
    // JWT 签发者
    private String issuer;
    // JWT 过期时间，可直接解析
    private String expiredAt;
    // JWT 刷新时间，可直接解析
    private String refreshAt;
    // JWT 专用密钥
    private String secretKey;
    // 是否启用
    private boolean enabled;
    // 访问范围
    private String audience;
    // 算法
    private String algorithm;
    // 前缀
    private String prefix;

    public long msExpiredAt() {
        final Duration duration = R2MO.toDuration(this.expiredAt);
        return duration.toMillis();
    }

    public long msRefreshAt() {
        final Duration duration = R2MO.toDuration(this.refreshAt);
        return duration.toMillis();
    }
}
