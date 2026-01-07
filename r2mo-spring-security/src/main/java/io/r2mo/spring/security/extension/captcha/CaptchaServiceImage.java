package io.r2mo.spring.security.extension.captcha;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.codec.Base64;
import io.r2mo.jaas.auth.CaptchaRequest;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.spring.security.config.ConfigSecurityCaptcha;
import io.r2mo.typed.common.Kv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 基于 Hutool + UserCache 的图形验证码服务实现
 *
 * @author lang : 2025-11-13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaServiceImage implements CaptchaService {

    private final CodeGenerator captchaGenerator;
    private final Font captchaFont;
    private final ConfigSecurityCaptcha configCaptcha;

    @Override
    public Map<String, Object> generate() {
        // 1. 生成唯一 key
        final String captchaKey = UUID.randomUUID().toString().replace("-", "");

        // 2. 使用 Hutool 生成图形验证码
        final LineCaptcha captcha = CaptchaUtil.createLineCaptcha(130, 48);
        captcha.setGenerator(this.captchaGenerator);
        captcha.setFont(this.captchaFont);

        // 获取验证码文本（用于存储）
        final String code = captcha.getCode();

        // 3. 存入 UserCache（类型为 CAPTCHA）
        final Kv<String, String> generated = Kv.create(captchaKey, code);
        UserCache.of().authorize(generated, this.configCaptcha.forArguments());

        // 4. 转为 Base64 图片
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            captcha.write(out);
            final String base64Image = Base64.encode(out.toByteArray());
            final Map<String, Object> result = new HashMap<>();
            result.put(CaptchaRequest.ID, captchaKey);
            result.put("image", "data:image/png;base64," + base64Image);
            return result;
        } catch (final Exception e) {
            log.error("Failed to generate captcha image", e);
            throw new RuntimeException("验证码生成失败", e);
        }
    }

    @Override
    public boolean validate(final String captchaId, final String userInput) {
        if (captchaId == null || userInput == null) {
            return false;
        }

        // 从缓存中获取并自动移除（一次性使用）
        final String storedCode = UserCache.of().authorize(captchaId, this.configCaptcha.forArguments());
        if (storedCode == null) {
            return false;
        }

        final boolean valid = storedCode.equalsIgnoreCase(userInput.trim());
        if (valid) {
            // 成功后可选再清除一次（防御性）
            this.invalidate(captchaId);
        }
        return valid;
    }

    @Override
    public void invalidate(final String captchaKey) {
        if (captchaKey == null) {
            return;
        }
        UserCache.of().authorizeKo(captchaKey, this.configCaptcha.forArguments());
    }
}