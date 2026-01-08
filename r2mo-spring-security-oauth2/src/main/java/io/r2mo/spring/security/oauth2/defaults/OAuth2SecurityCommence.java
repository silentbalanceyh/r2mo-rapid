package io.r2mo.spring.security.oauth2.defaults;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.handler.SecurityCommence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author lang
 */
@Slf4j
public class OAuth2SecurityCommence implements SecurityCommence {

    private final RequestMatcher matcher;
    private final AuthenticationEntryPoint entryPoint;

    public OAuth2SecurityCommence() {
        final ConfigSecurity security = SpringUtil.getBean(ConfigSecurity.class);
        final String loginFormUrl = security.getUri().getLogin();

        this.entryPoint = new LoginUrlAuthenticationEntryPoint(loginFormUrl);

        this.matcher = new OrRequestMatcher(
            new AntPathRequestMatcher("/oauth2/authorize"),
            new AntPathRequestMatcher("/oauth2/consent"),
            new AntPathRequestMatcher("/oauth2/device_verification")
        );
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        return this.matcher.matches(request);
    }

    @Override
    public boolean commence(final HttpServletRequest request, final HttpServletResponse response,
                            final AuthenticationException authException) {
        try {
            // 尝试执行跳转
            this.entryPoint.commence(request, response, authException);
            // 成功跳转，返回 true
            return true;
        } catch (final Exception e) {
            // 发生异常（如 IO 错误），仅记录日志，不抛出异常炸毁流程
            log.error("[ R2MO ] OAuth2 重定向异常 Failed，请求 URI: {}", request.getRequestURI(), e);
            // 返回 false，表示本策略处理失败，允许后续策略（如 JSON 兜底）继续尝试
            return false;
        }
    }
}