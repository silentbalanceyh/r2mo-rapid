package io.r2mo.jaas.enums;

/**
 * 登录类型的枚举，不同于ID类型
 * <pre>
 *     Pre 流程：
 *     - 验证码（图片验证码归入这一类） / 验证链接
 *     BuiltIn 流程：
 *     - PASSWORD
 * </pre>
 *
 * @author lang : 2025-11-13
 */
public enum TypeLogin {
    // -- Pre
    CAPTCHA,         // 验证码
    LINK,            // 验证链接
    // -- BuiltIn
    PASSWORD,        // 默认账号、密码
    // -- 其他方式
    SMS,             // 短信验证码
    EMAIL,           // 邮箱验证码
    LDAP,            // LDAP
    // -- 认证特殊方式
    OAUTH2,          // OAuth2 令牌
    JWT,             // JWT 令牌
    OTP,             // 一次性密码 (One-Time Password)
    SSO,             // 单点登录 (Single Sign-On)
}
