package io.r2mo.jaas.auth;

import lombok.Data;

import java.io.Serializable;

/**
 * 验证码对象
 *
 * @author lang : 2025-11-10
 */
@Data
public class LoginCaptcha implements Serializable {

    public static final String ID = "captchaId";
    public static final String CODE = "captcha";
    /**
     * 验证码唯一标识
     */
    private String captchaId;
    /**
     * 验证码图片 Base64 字符串
     */
    private String captcha;
}
