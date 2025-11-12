package io.r2mo.spring.security.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

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
    private int expiredAt;
    /**
     * 验证码图片宽度
     */
    private int width;
    /**
     * 验证码图片高度
     */
    private int height;
    /**
     * 验证码图片透明度
     */
    private Float textAlpha;
    /**
     * 验证码字符配置
     */
    private ConfigCode code;
    /**
     * 验证码字体配置
     */
    private ConfigFont font;

    @Data
    @Configuration
    @RequiredArgsConstructor
    public static class ConfigCode implements Serializable {
        /**
         * 验证码字符串类型 MATH-算术 | RANDOM-随机字符
         */
        private String type;
        /**
         * 验证码字符串长度，type = 算术时表示运算位数
         */
        private int length;
    }

    @Data
    @Configuration
    @RequiredArgsConstructor
    public static class ConfigFont implements Serializable {
        /**
         * 字体名称
         */
        private String name;
        /**
         * 字体样式：0-正常 | 1-粗体 | 2-斜体 | 3-粗斜体
         */
        private int weight;

        /**
         * 字体大小
         */
        private int size;
    }
}
