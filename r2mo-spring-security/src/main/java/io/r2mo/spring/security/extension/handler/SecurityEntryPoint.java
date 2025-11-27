package io.r2mo.spring.security.extension.handler;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.common.exception.SpringAbortExecutor;
import io.r2mo.spring.security.config.ConfigSecurityUri;
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
import java.util.function.Consumer;

/**
 * @author lang : 2025-11-11
 */
@Component
public class SecurityEntryPoint implements AuthenticationEntryPoint {

    private final List<Consumer<HttpServletResponse>> waitFor = new ArrayList<>();

    private final RequestMatcher htmlMatcher;

    private final AuthenticationEntryPoint htmlDelegate;

    public SecurityEntryPoint() {
        final ConfigSecurityUri configuration = SpringUtil.getBean(ConfigSecurityUri.class);
        this.htmlDelegate = new LoginUrlAuthenticationEntryPoint(configuration.getLogin());
        this.htmlMatcher = new OrRequestMatcher(
            // 如果 Accept 头包含 text/html (浏览器)
            new MediaTypeRequestMatcher(MediaType.TEXT_HTML),
            // 或者 显式访问 OAuth2 授权端点
            new AntPathRequestMatcher(configuration.getLogin())
        );
    }

    @SafeVarargs
    public static SecurityEntryPoint of(final Consumer<HttpServletResponse>... consumers) {
        final SecurityEntryPoint instance = new SecurityEntryPoint();
        instance.waitFor.addAll(Arrays.asList(consumers));
        return instance;
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException)
        throws IOException, ServletException {
        // 3. 【核心修改】判断是否需要跳转
        if (this.htmlMatcher.matches(request)) {
            // 如果是网页或OAuth流程，转交标准处理器（执行 302 重定向）
            this.htmlDelegate.commence(request, response, authException);
            return;
        }

        // 执行特定操作
        this.waitFor.forEach(consumer -> consumer.accept(response));


        // 转换成统一的 WebException
        final Throwable cause = SecurityFailure.findExceptionAt(authException);
        final WebException transform = SecurityFailure.of().transform(cause, request, response);


        // 直接执行异常处理器
        SpringAbortExecutor.handleFailure(transform, response);
    }
}
