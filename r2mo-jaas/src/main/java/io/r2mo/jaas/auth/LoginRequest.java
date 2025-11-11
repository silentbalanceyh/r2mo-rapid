package io.r2mo.jaas.auth;

import io.r2mo.jaas.enums.UserIDType;
import io.r2mo.typed.domain.extension.AbstractScope;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 登录请求专用类信息，可用于不同模式的认证
 *
 * @author lang : 2025-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginRequest extends AbstractScope implements Serializable {
    // username, email, mobile 或其他唯一标识
    private String id;

    // password, sms code 等等
    private String credential;

    // 图片验证码等
    private LoginCaptcha captcha;

    // 账号密码的方式登录
    private UserIDType idType = UserIDType.PASSWORD;
}
