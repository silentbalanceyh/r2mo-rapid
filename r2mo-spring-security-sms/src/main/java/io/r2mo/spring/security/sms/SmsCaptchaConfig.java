package io.r2mo.spring.security.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * <pre>
 *     security:
 *       captcha-sms:
 *         length:    6
 *         expiredAt: 60
 * </pre>
 *
 * @author lang : 2025-12-08
 */
@Configuration
@ConfigurationProperties(prefix = "security.captcha-sms")
@Data
public class SmsCaptchaConfig {
    private int length = 6;
    private int expiredAt = 60;
    private String template;

    public Duration expiredAt() {
        return Duration.ofSeconds(this.expiredAt);
    }
}
