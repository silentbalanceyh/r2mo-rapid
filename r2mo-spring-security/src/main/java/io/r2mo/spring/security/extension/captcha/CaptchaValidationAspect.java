package io.r2mo.spring.security.extension.captcha;

import io.r2mo.jaas.auth.CaptchaRequest;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityDev;
import io.r2mo.spring.security.exception._80222Exception401CaptchaWrong;
import io.r2mo.spring.security.exception._80242Exception400CaptchaRequired;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
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
import java.util.Objects;

/**
 * 图形验证码校验切面
 * 拦截所有标注了 @CaptchaOn 的方法，在执行前校验 captchaId 与 captcha。
 * <p>
 * 优先从控制器方法的 JObject 参数中读取验证码字段，避免与
 * SecurityScopeResolver 争抢 request.getInputStream()。
 * 仅在找不到 JObject 参数时降级为 raw body 读取。
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
     * 在执行被 @CaptchaOn 注解的方法前进行验证码校验。
     * 优先从 JObject 参数读取，降级时从 raw body 读取。
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

        // 追加 development 的特殊验证，Apifox工具开发过程中直接跳过专用
        final ConfigSecurityDev dev = this.configSecurity.getDevelopment();
        if (Objects.nonNull(dev)) {
            final String name = dev.getHeaderName();
            final String value = dev.getHeaderValue();
            if (Objects.nonNull(name)) {
                final String valueInput = request.getHeader(name);
                if (Objects.nonNull(valueInput) && valueInput.equals(value)) {
                    // 跳过图片验证码
                    return;
                }
            }
        }

        // 仅支持 POST + JSON
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new _80242Exception400CaptchaRequired("[ R2MO ] 验证码校验仅支持 POST 请求");
        }
        if (!this.isJsonRequest(request)) {
            throw new _80242Exception400CaptchaRequired("[ R2MO ] 请求体必须为 application/json 格式");
        }

        // 优先从已解析的 JObject 参数中提取 captchaId / captcha，
        // 避免与 SecurityScopeResolver 争抢 request.getInputStream()。
        final String captchaId;
        final String captcha;
        final JObject requestJ = this.findJObject(joinPoint);
        if (requestJ != null) {
            captchaId = requestJ.getString(CaptchaRequest.ID);      // "captchaId"
            captcha = requestJ.getString(CaptchaRequest.CODE);      // "captcha"
        } else {
            // 降级：原逻辑，兼容不支持 JObject 的老 Controller（同时打印 WARN）
            log.warn("[ R2MO ] @CaptchaOn 方法未找到 JObject 参数，降级为 body 读取");
            final CaptchaRequest payload = this.readCaptcha(request, joinPoint);
            captchaId = payload.getCaptchaId();
            captcha = payload.getCaptcha();
        }

        // 非空校验
        if (captchaId == null || captchaId.trim().isEmpty()) {
            log.warn("[ R2MO ] 方法 {} 缺少 captchaId 参数", joinPoint.getSignature());
            throw new _80242Exception400CaptchaRequired("captchaId");
        }
        if (captcha == null || captcha.trim().isEmpty()) {
            log.warn("[ R2MO ] 方法 {} 缺少 captcha 参数", joinPoint.getSignature());
            throw new _80242Exception400CaptchaRequired("captcha");
        }

        final boolean valid = this.captchaService.validate(captchaId.trim(), captcha.trim());
        if (!valid) {
            log.warn("[ R2MO ] 验证码校验失败，captchaId: {}", captchaId);
            throw new _80222Exception401CaptchaWrong(captcha);
        }

        log.debug("[ R2MO ] 验证码校验成功，captchaId: {}", captchaId);
    }

    /**
     * 从 JoinPoint 方法参数中查找 JObject 实例。
     * 遍历所有参数，返回第一个 instanceof JObject 的值。
     *
     * @return JObject 实例，若方法参数中无则返回 null
     */
    private JObject findJObject(final JoinPoint joinPoint) {
        for (final Object arg : joinPoint.getArgs()) {
            if (arg instanceof JObject jObject) {
                return jObject;
            }
        }
        return null;
    }

    /**
     * 【降级路径】从请求体 raw InputStream 读取 captcha 数据。
     * 仅在 Controller 方法参数中找不到 JObject 时使用。
     * 注意：此方法依赖 request.getInputStream()，可能与下游 body 读取冲突。
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