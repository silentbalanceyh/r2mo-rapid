package io.r2mo.spring.security.exchange;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.spring.security.exception._80241Exception400PasswordRequired;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 第三方集成 Token 交换请求
 * <pre>
 *     {
 *         "clientId": "???",
 *         "clientSecret": "???"
 *     }
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExchangeLoginRequest extends LoginRequest {
    private String clientId;
    private String clientSecret;

    public ExchangeLoginRequest() {
    }

    public ExchangeLoginRequest(final JObject request) {
        this.setClientId(request.getString("clientId"));
        this.setClientSecret(request.getString("clientSecret"));
        this.requestValidated();
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
        this.setId(clientId);
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
        this.setCredential(clientSecret);
    }

    @Override
    public TypeLogin type() {
        return TypeLogin.EXCHANGE;
    }

    public void requestValidated() {
        if (StrUtil.isEmpty(this.clientId)) {
            throw new _80241Exception400PasswordRequired("clientId");
        }
        if (StrUtil.isEmpty(this.clientSecret)) {
            throw new _80241Exception400PasswordRequired("clientSecret");
        }
    }
}
