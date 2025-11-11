package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.auth.LoginResponse;
import org.springframework.security.core.AuthenticationException;

/**
 * @author lang : 2025-11-11
 */
public interface AuthProvider {
    /**
     * 前置处理
     *
     * @param loginRequest 登录请求
     *
     * @return 是否允许登录
     */
    default boolean authorize(final LoginRequest loginRequest) {
        return true;
    }

    /**
     * 执行认证并返回登录响应
     */
    LoginResponse login(LoginRequest loginRequest) throws AuthenticationException;

    /**
     * 判断是否支持该登录请求
     */
    boolean supports(LoginRequest loginRequest);
}
