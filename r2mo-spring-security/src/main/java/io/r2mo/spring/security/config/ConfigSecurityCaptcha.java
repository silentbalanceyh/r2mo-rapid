package io.r2mo.spring.security.config;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.time.Duration;

/**
 * @author lang : 2025-11-10
 */
@Configuration
@ConfigurationProperties(prefix = "security.captcha")
@RequiredArgsConstructor
@Data
public class ConfigSecurityCaptcha implements Serializable {
    private boolean enabled = false;
    private String type;
    /**
     * 验证码过期时间，单位：秒
     */
    private int expiredAt = 60;
    /**
     * 验证码图片宽度
     */
    private int width = 180;
    /**
     * 验证码图片高度
     */
    private int height = 48;
    /**
     * 验证码文字透明度（1.0 = 不透明，越低越淡）
     */
    private Float textAlpha = 1.0f;
    /**
     * 验证码干扰线数量。Hutool 默认值较高，登录页中会明显影响可读性。
     */
    private int interfereCount = 8;
    /**
     * 验证码视觉样式配置
     */
    private ConfigStyle style = new ConfigStyle();
    /**
     * 验证码字符配置
     */
    private ConfigCode code = new ConfigCode();
    /**
     * 验证码字体配置
     */
    private ConfigFont font = new ConfigFont();

    public CaptchaArgs forArguments() {
        final Duration duration = Duration.ofSeconds(this.expiredAt);
        return CaptchaArgs.of(TypeLogin.CAPTCHA, duration);
    }

    @Data
    @Configuration
    @RequiredArgsConstructor
    public static class ConfigCode implements Serializable {
        /**
         * 验证码字符串类型 MATH-算术 | RANDOM-随机字符
         */
        private String type = "RANDOM";
        /**
         * 验证码字符串长度，type = 算术时表示运算位数
         */
        private int length = 5;
        /**
         * 随机验证码字符池，默认排除 0/O/1/I/L 等易混淆字符。
         */
        private String base = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    }

    @Data
    @Configuration
    @RequiredArgsConstructor
    public static class ConfigFont implements Serializable {
        /**
         * 字体名称
         */
        private String name = "PingFang SC";
        /**
         * 字体样式：0-正常 | 1-粗体 | 2-斜体 | 3-粗斜体
         */
        private int weight = 1;

        /**
         * 字体大小
         */
        private int size = 32;
    }

    @Data
    @Configuration
    @RequiredArgsConstructor
    public static class ConfigStyle implements Serializable {
        /**
         * 背景色
         */
        private String background = "#F4F6F8";
        /**
         * 文字颜色池，使用逗号分隔。默认只使用深色，避免白色登录界面中浅色字符不可读。
         */
        private String text = "#991B1B,#9A3412,#92400E,#166534,#0F766E,#1E40AF,#6B21A8,#9D174D";
        /**
         * 干扰线颜色
         */
        private String interfere = "#7F1D1D,#7C2D12,#713F12,#14532D,#0F766E,#1E3A8A,#4C1D95,#831843";
        /**
         * 干扰线透明度，取值 0~1。
         */
        private Float interfereAlpha = 0.35f;
    }
}
