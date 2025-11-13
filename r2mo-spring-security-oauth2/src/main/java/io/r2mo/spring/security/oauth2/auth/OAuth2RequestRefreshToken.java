package io.r2mo.spring.security.oauth2.auth;

import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OAuth2 刷新令牌模式请求
 *
 * <pre>
 * {
 *   "grant_type": "refresh_token",
 *   "refresh_token": "刷新令牌",
 *   "client_id": "客户端ID",
 *   "client_secret": "客户端密钥",
 *   "scope": "权限范围"
 * }
 * </pre>
 *
 * @author lang : 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2RequestRefreshToken extends OAuth2Request {

    /**
     * 刷新令牌
     */
    private String refreshToken;

    public OAuth2RequestRefreshToken() {
        super();
    }

    public OAuth2RequestRefreshToken(final JObject request) {
        super(request);
        this.refreshToken = request.getString("refresh_token");
        this.setId(this.refreshToken);
        this.setCredential(this.refreshToken);
    }

    @Override
    public String getGrantType() {
        return "refresh_token";
    }
}

