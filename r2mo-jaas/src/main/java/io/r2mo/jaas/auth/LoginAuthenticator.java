package io.r2mo.jaas.auth;

/**
 * 可扩展的登录认证器
 *
 * @author lang : 2025-11-10
 */
public interface LoginAuthenticator {

    /**
     * 执行登录认证逻辑，根据登录逻辑构造返回结果信息
     *
     * @param request 包含登录凭据信息
     *
     * @return 登录结果
     */
    LoginResponse execute(LoginRequest request);
}
