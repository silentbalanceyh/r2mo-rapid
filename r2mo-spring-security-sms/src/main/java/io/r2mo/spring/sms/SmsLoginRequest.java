package io.r2mo.spring.sms;

import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.spring.security.exception._80241Exception400PasswordRequired;
import io.r2mo.spring.sms.exception._80381Exception400MobileRequired;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * SMS请求
 * <pre>
 *     {
 *         "mobile": "???",
 *         "captcha": "1234"
 *     }
 * </pre>
 * mobile = captcha
 *
 * @author lang : 2025-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SmsLoginRequest extends LoginRequest {
    private String mobile;
    private String captcha;

    public void setMobile(final String mobile) {
        this.mobile = mobile;
        this.setId(mobile);
    }

    public void setCaptcha(final String captcha) {
        this.captcha = captcha;
        this.setCredential(captcha);
    }

    @Override
    public TypeLogin type() {
        return TypeLogin.SMS;
    }

    public SmsLoginRequest() {
    }

    public SmsLoginRequest(final JObject request) {
        this.setMobile(request.getString(LoginID.MOBILE));
        this.setCaptcha(request.getString("captcha"));
        // 如果使用 JObject 构造，构造完成后验证！
        this.requestValidated();
    }


    public void requestValidated() {
        if (Objects.isNull(this.mobile)) {
            throw new _80381Exception400MobileRequired();
        }
        if (Objects.isNull(this.captcha)) {
            throw new _80241Exception400PasswordRequired("captcha");
        }
    }
}
