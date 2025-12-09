package io.r2mo.typed.enums;

/**
 * 登录类型的枚举，不同于ID类型
 * <pre>
 *     Pre 流程：
 *     - 验证码（图片验证码归入这一类） / 验证链接
 *     BuiltIn 流程：
 *     - PASSWORD
 *     ID_前缀一定会有一个 {@link TypeID} 和它对应，表示 ID 的类型
 * </pre>
 *
 * @author lang : 2025-11-13
 */
public enum TypeLogin {
    ID_WECHAT,          // 微信号
    ID_WECOM,           // 企微
    /**
     * 下边枚举值和 {@link TypeID} 无关，主要是从技术上区分不同的登录方式而已
     */
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
