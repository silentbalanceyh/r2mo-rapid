package io.r2mo.spring.security.email;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 *     spring:
 *       security:
 *         captcha-email:
 *           length:    6
 *           expiredAt: 300
 *           subject:   验证码专用邮件
 *           template:  验证码专用模板名
 * </pre>
 *
 * @author lang : 2025-12-06
 */
@Configuration
@ConfigurationProperties(prefix = "spring.security.captcha-email")
@Data
public class EmailCaptchaConfig {
    private int length = 6;
    private int expiredAt = 300;
    private String subject;
    private String template = "captcha-email";

    /**
     * 参数中不用包含 template（独立传参）
     *
     * @return 参数信息
     */
    public JObject parameters() {
        final JObject params = SPI.J();
        params.put(EmailConstant.CONFIG_LENGTH, this.length);
        params.put(EmailConstant.CONFIG_EXPIRED, this.expiredAt);   // 单位：秒
        params.put(EmailConstant.SUBJECT, this.subject);            // 邮件主题
        return params;
    }
}
