package io.r2mo.spring.security.extension.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * @author lang : 2025-11-12
 */
@RequiredArgsConstructor
@Component
public class SecurityHandler {

    private final AccessDeniedHandler deniedHandler;
    private final AuthenticationEntryPoint entryPoint;
    // 🟢 新增：登录失败处理器 (账号密码错误/自定义校验失败)
    // Spring 会自动注入我们之前定义的 SecurityLoginFailureHandler
    private final AuthenticationFailureHandler failureHandler;

    public Customizer<ExceptionHandlingConfigurer<HttpSecurity>> handlerException() {
        return (config) -> {
            config.accessDeniedHandler(this.deniedHandler);
            config.authenticationEntryPoint(this.entryPoint);
        };
    }

    /**
     * 🟢 新增：获取登录失败处理器 (401 Login)
     * 用于 http.formLogin().failureHandler()
     */
    public AuthenticationFailureHandler handlerUnauthorized() {
        return this.failureHandler;
    }
}
