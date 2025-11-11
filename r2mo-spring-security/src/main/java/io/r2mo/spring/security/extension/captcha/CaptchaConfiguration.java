package io.r2mo.spring.security.extension.captcha;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import io.r2mo.spring.security.config.ConfigSecurityCaptcha;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

/**
 * @author lang : 2025-11-10
 */
@Configuration
@RequiredArgsConstructor
public class CaptchaConfiguration {

    private final ConfigSecurityCaptcha captcha;

    /**
     * 验证码文字生成器
     */
    @Bean
    public CodeGenerator captchaGenerator() {
        final String codeType = this.captcha.getCode().getType();
        final int codeLength = this.captcha.getCode().getLength();
        if (CaptchaCode.MATH.name().equalsIgnoreCase(codeType)) {
            // 数学公式验证码
            return new MathGenerator(codeLength);
        }
        if (CaptchaCode.RANDOM.name().equalsIgnoreCase(codeType)) {
            // 随机字符串验证码
            return new RandomGenerator(codeLength);
        }
        throw new IllegalArgumentException("[ R2MO ] 非法的验证码类型配置：" + codeType);
    }

    /**
     * 验证码字体
     */
    @Bean
    @SuppressWarnings("all")
    public Font captchaFont() {
        final String fontName = this.captcha.getFont().getName();
        final int fontWeight = this.captcha.getFont().getWeight();
        final int fontSize = this.captcha.getFont().getSize();
        return new Font(fontName, fontWeight, fontSize);
    }
}
