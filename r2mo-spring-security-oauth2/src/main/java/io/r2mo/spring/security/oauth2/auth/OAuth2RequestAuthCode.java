package io.r2mo.spring.security.oauth2.auth;

import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OAuth2 授权码模式请求
 *
 * <pre>
 * {
 *   "grant_type": "authorization_code",
 *   "code": "授权码",
 *   "redirect_uri": "回调地址",
 *   "client_id": "客户端ID",
 *   "client_secret": "客户端密钥"
 * }
 * </pre>
 *
 * @author lang : 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2RequestAuthCode extends OAuth2Request {

    /**
     * 授权码
     */
    private String code;

    /**
     * 回调地址
     */
    private String redirectUri;

    public OAuth2RequestAuthCode() {
        super();
    }

    public OAuth2RequestAuthCode(final JObject request) {
        super(request);
        this.code = request.getString("code");
        this.redirectUri = request.getString("redirect_uri");
        this.setId(this.code);
        this.setCredential(this.code);
    }

    @Override
    public String getGrantType() {
        return "authorization_code";
    }
}

