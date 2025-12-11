package io.r2mo.spring.security.extension.captcha;

import io.r2mo.jaas.auth.CaptchaRequest;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.exception._80222Exception401CaptchaWrong;
import io.r2mo.spring.security.exception._80242Exception400CaptchaRequired;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JBase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.InputStream;

/**
 * 图形验证码校验切面
 * 拦截所有标注了 @CaptchaOn 的方法，在执行前从请求体中校验 captchaId 与 captcha
 *
 * @author lang : 2025-11-13
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CaptchaValidationAspect {

    private final CaptchaService captchaService;
    private final ConfigSecurity configSecurity;

    /**
     * 在执行被 @CaptchaOn 注解的方法前进行验证码校验（从 JSON Body 读取）
     */
    @Before("@annotation(io.r2mo.spring.security.extension.captcha.CaptchaOn)")
    public void validateCaptcha(final JoinPoint joinPoint) {
        if (!this.configSecurity.isCaptcha()) {
            return;
        }
        final HttpServletRequest request = this.getCurrentHttpRequest();
        if (request == null) {
            throw new IllegalStateException("[ R2MO ] 当前上下文非 Web 请求环境");
        }

        // 仅支持 POST + JSON
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new _80242Exception400CaptchaRequired("[ R2MO ] 验证码校验仅支持 POST 请求");
        }
        if (!this.isJsonRequest(request)) {
            throw new _80242Exception400CaptchaRequired("[ R2MO ] 请求体必须为 application/json 格式");
        }

        // 全部逻辑封装进 readCaptcha
        final CaptchaRequest payload = this.readCaptcha(request, joinPoint);

        final String captchaId = payload.getCaptchaId().trim();
        final String captcha = payload.getCaptcha().trim();

        final boolean valid = this.captchaService.validate(captchaId, captcha);
        if (!valid) {
            log.warn("[ R2MO ] 验证码校验失败，captchaId: {}", captchaId);
            throw new _80222Exception401CaptchaWrong(captcha);
        }

        log.debug("[ R2MO ] 验证码校验成功，captchaId: {}", captchaId);
    }

    /**
     * 从请求体读取 JSON，解析为 LoginCaptcha，并校验 captchaId 与 captcha 非空。
     * 若任一环节失败，抛出对应的业务异常。
     */
    private CaptchaRequest readCaptcha(final HttpServletRequest request, final JoinPoint joinPoint) {
        // 1. 解析 JSON
        final CaptchaRequest payload;
        try (final InputStream inputStream = request.getInputStream()) {
            payload = JBase.jackson().readValue(inputStream, CaptchaRequest.class);
        } catch (final IOException e) {
            log.warn("[ R2MO ] 无法解析请求体中的 JSON 数据", e);
            throw new _400BadRequestException("[ R2MO ] 请求体格式无效，无法读取验证码信息");
        }

        // 2. 校验 captchaId
        final String captchaId = payload.getCaptchaId();
        if (captchaId == null || captchaId.trim().isEmpty()) {
            log.warn("[ R2MO ] 方法 {} 缺少 captchaId 参数", joinPoint.getSignature());
            throw new _80242Exception400CaptchaRequired("captchaId");
        }

        // 3. 校验 captcha
        final String captcha = payload.getCaptcha();
        if (captcha == null || captcha.trim().isEmpty()) {
            log.warn("[ R2MO ] 方法 {} 缺少 captcha 参数", joinPoint.getSignature());
            throw new _80242Exception400CaptchaRequired("captcha");
        }

        return payload;
    }

    private HttpServletRequest getCurrentHttpRequest() {
        final ServletRequestAttributes attrs =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private boolean isJsonRequest(final HttpServletRequest request) {
        final String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }
}