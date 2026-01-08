package io.r2mo.spring.security.extension.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spi.SPI;
import io.r2mo.spring.common.exception.SpringAbortExecutor;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestUri;
import io.r2mo.typed.exception.WebException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 策略一：API 黑名单 (优先级最高)
 * <p>
 * 职责：检查 SPI 定义的 {@link RequestUri}，如果命中，强制返回 JSON。
 * 这通常用于保护 API 接口，防止误跳转到登录页。
 */
@Order(Integer.MIN_VALUE) // 确保在 List 中排在最前（虽然主类会手动编排，但加注解是好习惯）
@Component
class SecurityCommenceBlacklist implements SecurityCommence {

    private final RequestMatcher blackMatcher;

    SecurityCommenceBlacklist() {
        final ConfigSecurity security = SpringUtil.getBean(ConfigSecurity.class);
        // 初始化黑名单匹配器
        final List<RequestUri> found = SPI.findMany(RequestUri.class);
        final List<RequestMatcher> matchers = found.stream()
            .flatMap(it -> it.noRedirect(security).stream())
            .map(AntPathRequestMatcher::new)
            .collect(Collectors.toUnmodifiableList());

        this.blackMatcher = matchers.isEmpty() ? null : new OrRequestMatcher(matchers);
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        return this.blackMatcher != null && this.blackMatcher.matches(request);
    }

    @Override
    public boolean commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) {
        // 命中黑名单 -> 强制输出 JSON
        final Throwable cause = SecurityFailure.findExceptionAt(authException);
        final WebException transform = SecurityFailure.of().transform(cause, request, response);
        SpringAbortExecutor.handleFailure(transform, response);

        // 返回 true，告知 Orchestrator 流程已结束
        return true;
    }
}