package io.r2mo.spring.security.extension.captcha;

/**
 * 验证码类型
 *
 * @author lang : 2025-11-10
 */
public enum CaptchaType {
    /**
     * 圆圈干扰验证码
     */
    CIRCLE,
    /**
     * GIF 验证码
     */
    GIF,
    /**
     * 线干扰验证码
     */
    LINE,
    /**
     * 扭曲干扰验证码
     */
    SHEAR,
}
