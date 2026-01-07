package io.r2mo.spring.security.extension.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spi.SPI;
import io.r2mo.spring.common.exception.SpringAbortExecutor;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityUri;
import io.r2mo.spring.security.extension.RequestUri;
import io.r2mo.typed.exception.WebException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 智能安全入口点
 * <p>
 * 负责处理认证失败（401）时的跳转逻辑：
 * 1. 优先处理 SPI 黑名单（强制 JSON）
 * 2. 智能识别浏览器行为（重定向到登录页）
 * 3. 默认兜底（API/AJAX 返回 JSON）
 *
 * @author lang : 2025-11-11
 */
@Component
public class SecurityEntryPoint implements AuthenticationEntryPoint {

    private final List<Consumer<HttpServletResponse>> waitFor = new ArrayList<>();

    // 智能重定向匹配器
    private final RequestMatcher htmlMatcher;

    // SPI 强制黑名单匹配器
    private final RequestMatcher blackMatcher;

    // Spring 默认的登录页跳转处理器
    private final AuthenticationEntryPoint htmlDelegate;

    public SecurityEntryPoint() {
        final ConfigSecurity security = SpringUtil.getBean(ConfigSecurity.class);

        // 初始化委托对象，负责具体的重定向动作
        this.htmlDelegate = new LoginUrlAuthenticationEntryPoint(security.getUri().getLogin());

        // 1. 初始化智能重定向逻辑 (核心修改点)
        this.htmlMatcher = this.matchRedirect(security);

        // 2. 初始化黑名单逻辑 (保留原扩展)
        this.blackMatcher = this.matchBlack(security);
    }

    @SafeVarargs
    public static SecurityEntryPoint of(final Consumer<HttpServletResponse>... consumers) {
        final SecurityEntryPoint instance = new SecurityEntryPoint();
        instance.waitFor.addAll(Arrays.asList(consumers));
        return instance;
    }

    /**
     * SPI 扩展：强制不重定向的黑名单规则
     * (保留原逻辑不变)
     */
    private RequestMatcher matchBlack(final ConfigSecurity security) {
        final List<RequestUri> found = SPI.findMany(RequestUri.class);
        final List<RequestMatcher> matchers = found.stream()
            .flatMap(it -> it.noRedirect(security).stream())
            .map(AntPathRequestMatcher::new)
            .collect(Collectors.toUnmodifiableList());

        if (matchers.isEmpty()) {
            return null;
        }
        return new OrRequestMatcher(matchers);
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
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException)
        throws IOException, ServletException {

        // 1. 黑名单优先级最高 (SPI 扩展)
        // 如果命中 SPI 定义的规则，强制走 JSON，不再判断是否是浏览器
        if (Objects.nonNull(this.blackMatcher) && this.blackMatcher.matches(request)) {
            this.commenceJson(request, response, authException);
            return;
        }

        // 2. 智能判定是否需要重定向
        // 只有纯浏览器访问受保护资源（且非登录页本身）时，才重定向
        if (this.htmlMatcher.matches(request)) {
            // OAuth2 流程会走到这里，因为它符合 HTML && !AJAX && !LoginURL
            this.commenceHtml(request, response, authException);
            return;
        }

        // 3. 默认兜底：返回 JSON 响应
        // 适用于 API、AJAX、登录失败、Token 无效等场景
        this.commenceJson(request, response, authException);
    }

    private void commenceHtml(final HttpServletRequest request, final HttpServletResponse response,
                              final AuthenticationException authException)
        throws IOException, ServletException {
        // 直接交给 Spring Security 默认的重定向处理器 (302 Redirect)
        this.htmlDelegate.commence(request, response, authException);
    }

    private void commenceJson(final HttpServletRequest request, final HttpServletResponse response,
                              final AuthenticationException authException) {
        // 执行额外注入的回调 (如 Basic Auth 的 WWW-Authenticate 头)
        this.waitFor.forEach(consumer -> consumer.accept(response));

        // 转换异常
        final Throwable cause = SecurityFailure.findExceptionAt(authException);
        final WebException transform = SecurityFailure.of().transform(cause, request, response);

        // 输出 JSON
        SpringAbortExecutor.handleFailure(transform, response);
    }
}