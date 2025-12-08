package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import org.springframework.security.core.AuthenticationException;

import java.time.Duration;

/**
 * @author lang : 2025-11-11
 */
public interface AuthService {
    /**
     * 前置处理
     * <pre>
     *     认证方式              前置接口
     *     sms                  /auth/sms-send
     *     email                /auth/email-send
     *     ldap                 无
     *     wechat               /auth/wechat-qrcode
     *     password             /auth/captcha           开启图片验证码后使用
     * </pre>
     *
     * @param loginRequest 登录请求
     *
     * @return 是否允许登录
     */
    default String authorize(final LoginRequest loginRequest, final Duration duration) {
        return null;
    }

    /**
     * 执行认证并返回登录响应
     * <pre>
     *     认证方式              执行接口
     *     sms                  /auth/sms-login
     *     email                /auth/email-login
     *     ldap                 /auth/ldap-login
     *     wechat               /auth/wechat-login
     *     password             /auth/login
     * </pre>
     */
    UserAt login(LoginRequest loginRequest) throws AuthenticationException;
}
