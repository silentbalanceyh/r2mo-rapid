package io.r2mo.spring.security.email;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * <pre>
 *   security:
 *     captcha-email:
 *       length:    6
 *       expiredAt: 300
 *       subject:   验证码专用邮件
 *       template:  验证码专用模板名
 * </pre>
 *
 * @author lang : 2025-12-06
 */
@Configuration
@ConfigurationProperties(prefix = "security.captcha-email")
@Data
public class EmailCaptchaConfig {
    private int length = 6;
    private int expiredAt = 300;
    private String subject;
    private String template = "captcha-email";

    public Duration expiredAt() {
        return Duration.ofSeconds(this.expiredAt);
    }
}
