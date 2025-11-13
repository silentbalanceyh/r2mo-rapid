package io.r2mo.spring.security.basic;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.enums.TypeLogin;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * 基础请求，主要负责
 * <pre>
 *     {
 *         "username":"admin",
 *         "password":"admin123",
 *         "captcha":"1234"
 *     }
 * </pre>
 *
 * @author lang : 2025-11-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasicLoginRequest extends LoginRequest {
    private String username;
    private String password;
    private String captcha;

    public void setUsername(final String username) {
        this.username = username;
        this.setId(username);
    }

    public void setPassword(final String password) {
        this.password = password;
        this.setCredential(password);
    }

    @Override
    public TypeLogin type() {
        return TYPE;
    }

    public static final TypeLogin TYPE = TypeLogin.PASSWORD;

    public BasicLoginRequest() {
    }

    public BasicLoginRequest(final JObject request) {
        this.setUsername(request.getString("username"));
        this.setPassword(request.getString("password"));
        this.setCaptcha(request.getString("captcha"));
        this.app(request.getString("appId"));
        this.tenant(request.getString("tenantId"));
        // 如果使用 JObject 构造，构造完成后验证！
        this.requestValidated();
    }

    // 特殊认证流程，必须保证 username 和 password 都不为空
    public void requestValidated() {
        if (Objects.isNull(this.username)) {
            throw new _400BadRequestException("[ R2MO ] 账号不可为空！");
        }
        if (Objects.isNull(this.password)) {
            throw new _400BadRequestException("[ R2MO ] 密码不可为空！");
        }
    }
}
