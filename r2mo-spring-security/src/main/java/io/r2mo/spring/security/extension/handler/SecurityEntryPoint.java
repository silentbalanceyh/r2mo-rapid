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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-11-11
 */
@Component
public class SecurityEntryPoint implements AuthenticationEntryPoint {

    private final List<Consumer<HttpServletResponse>> waitFor = new ArrayList<>();

    private final RequestMatcher htmlMatcher;

    private final RequestMatcher blackMatcher;

    private final AuthenticationEntryPoint htmlDelegate;

    public SecurityEntryPoint() {
        final ConfigSecurity security = SpringUtil.getBean(ConfigSecurity.class);
        // 重定向配置
        this.htmlDelegate = new LoginUrlAuthenticationEntryPoint(security.getUri().getLogin());
        this.htmlMatcher = this.matchRedirect(security);
        // 黑名单配置
        this.blackMatcher = this.matchBlack(security);
    }

    @SafeVarargs
    public static SecurityEntryPoint of(final Consumer<HttpServletResponse>... consumers) {
        final SecurityEntryPoint instance = new SecurityEntryPoint();
        instance.waitFor.addAll(Arrays.asList(consumers));
        return instance;
    }

    private RequestMatcher matchBlack(final ConfigSecurity security) {
        final List<RequestUri> found = SPI.findMany(RequestUri.class);
        final List<RequestMatcher> matchers = found.stream()
            .flatMap(it -> it.noRedirect(security).stream())
            .map(AntPathRequestMatcher::new)
            .collect(Collectors.toUnmodifiableList());
        if (matchers.isEmpty()) {
            // 可能未配置
            return null;
        }
        return new OrRequestMatcher(matchers);
    }

    private RequestMatcher matchRedirect(final ConfigSecurity security) {
        final ConfigSecurityUri configuration = security.getUri();
        final List<RequestMatcher> matchers = new ArrayList<>();
        // 如果 Accept 头包含 text/html (浏览器)
        matchers.add(new MediaTypeRequestMatcher(MediaType.TEXT_HTML));
        // 或者 显式访问 OAuth2 授权端点
        matchers.add(new AntPathRequestMatcher(configuration.getLogin()));
        return new OrRequestMatcher(matchers);
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException)
        throws IOException, ServletException {


        // 1. 黑名单优先级最高
        if (Objects.nonNull(this.blackMatcher) && this.blackMatcher.matches(request)) {
            // 直接执行异常处理器
            this.commenceJson(request, response, authException);
            return;
        }


        // 2. 白名单优先级其次
        if (this.htmlMatcher.matches(request)) {
            // 如果是网页或OAuth流程，转交标准处理器（执行 302 重定向）
            this.commenceHtml(request, response, authException);
            return;
        }

        // 3. 其他情况，直接返回 JSON 响应
        this.commenceJson(request, response, authException);
    }

    private void commenceHtml(final HttpServletRequest request, final HttpServletResponse response,
                              final AuthenticationException authException)
        throws IOException, ServletException {
        // 直接交给 Spring Security 默认的重定向处理器
        this.htmlDelegate.commence(request, response, authException);
    }

    private void commenceJson(final HttpServletRequest request, final HttpServletResponse response,
                              final AuthenticationException authException) {

        // 执行特定操作
        this.waitFor.forEach(consumer -> consumer.accept(response));


        // 转换成统一的 WebException
        final Throwable cause = SecurityFailure.findExceptionAt(authException);
        final WebException transform = SecurityFailure.of().transform(cause, request, response);


        // 直接执行异常处理器
        SpringAbortExecutor.handleFailure(transform, response);
    }
}
