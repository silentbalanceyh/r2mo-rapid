package io.r2mo.jaas.auth;

import io.r2mo.typed.domain.extension.AbstractScope;
import io.r2mo.typed.enums.TypeLogin;
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
public abstract class LoginRequest extends AbstractScope implements Serializable {
    // username, email, mobile 或其他唯一标识
    private String id;

    // password, sms code 等等
    private String credential;

    public abstract TypeLogin type();
}
