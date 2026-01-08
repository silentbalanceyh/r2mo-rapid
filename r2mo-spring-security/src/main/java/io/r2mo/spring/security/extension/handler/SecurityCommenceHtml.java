package io.r2mo.spring.security.extension.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityUri;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.*;
import org.springframework.stereotype.Component;

/**
 * 策略二：标准浏览器 HTML 重定向
 * <p>
 * 职责：识别标准的浏览器请求（Accept HTML && !AJAX），执行 302 跳转。
 */
@Component
class SecurityCommenceHtml implements SecurityCommence {

    private final RequestMatcher htmlMatcher;
    private final AuthenticationEntryPoint delegate;

    SecurityCommenceHtml() {
        final ConfigSecurity security = SpringUtil.getBean(ConfigSecurity.class);
        final String loginPath = security.getUri().getLogin();

        // 1. 初始化匹配逻辑 (是HTML && !Ajax && !LoginUrl)
        this.htmlMatcher = this.matchRedirect(security);

        // 2. 初始化跳转委托 (处理 ContextPath 等)
        this.delegate = new LoginUrlAuthenticationEntryPoint(loginPath);
    }

    /**
     * 🔥 核心修复：重构智能匹配逻辑 🔥
     * 定义【什么情况下】才允许重定向到登录页。
     * 必须同时满足：是HTML请求 && 不是AJAX && 不是JSON && 不是登录页本身
     */
    private RequestMatcher matchRedirect(final ConfigSecurity security) {
        final ConfigSecurityUri configuration = security.getUri();
        final String loginPath = configuration.getLogin();

        // 条件 1: 客户端显式接受 HTML (浏览器导航行为)
        final RequestMatcher isHtmlAccept = new MediaTypeRequestMatcher(MediaType.TEXT_HTML);

        // 条件 2 (排除): 客户端是 AJAX 请求 (XHR) -> 必须回 JSON
        // 即使 Accept 包含 text/html，如果是 Ajax 也不应该重定向
        final RequestMatcher isAjax = new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest");

        // 条件 3 (排除): 客户端显式要求 JSON -> 必须回 JSON
        final RequestMatcher isJsonAccept = new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON);

        // 条件 4 (排除): 当前请求【已经】是登录页了 -> 必须回 JSON
        // 防止：POST /login 认证失败 -> 重定向 /login -> GET /login -> 死循环
        final RequestMatcher isLoginUrl = new AntPathRequestMatcher(loginPath);

        // 组合逻辑：(是HTML) AND (不是AJAX) AND (不是JSON) AND (不是登录URL)
        return new AndRequestMatcher(
            isHtmlAccept,
            new NegatedRequestMatcher(isAjax),
            new NegatedRequestMatcher(isJsonAccept),
            new NegatedRequestMatcher(isLoginUrl)
        );
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        return this.htmlMatcher.matches(request);
    }

    @Override
    public boolean commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException) {
        try {
            this.delegate.commence(request, response, authException);
            return true; // 已跳转，流程结束
        } catch (final Throwable e) {
            return false;
        }
    }
}