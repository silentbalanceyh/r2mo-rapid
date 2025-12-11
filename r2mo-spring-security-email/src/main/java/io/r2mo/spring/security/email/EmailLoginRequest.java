package io.r2mo.spring.security.email;

import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.spring.security.email.exception._80301Exception400EmailRequired;
import io.r2mo.spring.security.exception._80241Exception400PasswordRequired;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * Email请求
 * <pre>
 *     {
 *         "email": "????",
 *         "captcha": "1234"
 *     }
 * </pre>
 * email = captchaId
 *
 * @author lang : 2025-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EmailLoginRequest extends LoginRequest {
    private String email;
    private String captcha;

    public EmailLoginRequest() {
    }

    public EmailLoginRequest(final JObject request) {
        this.setEmail(request.getString(LoginID.EMAIL));
        this.setCaptcha(request.getString("captcha"));
        // 如果使用 JObject 构造，构造完成后验证！
        this.requestValidated();
    }

    public void setEmail(final String email) {
        this.email = email;
        this.setId(email);
    }

    public void setCaptcha(final String captcha) {
        this.captcha = captcha;
        this.setCredential(captcha);
    }

    @Override
    public TypeLogin type() {
        return TypeLogin.EMAIL;
    }

    public void requestValidated() {
        if (Objects.isNull(this.email)) {
            throw new _80301Exception400EmailRequired();
        }
        if (Objects.isNull(this.captcha)) {
            throw new _80241Exception400PasswordRequired("captcha");
        }
    }
}
