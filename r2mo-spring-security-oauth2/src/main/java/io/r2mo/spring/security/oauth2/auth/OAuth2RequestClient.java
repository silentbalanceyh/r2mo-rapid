package io.r2mo.spring.security.oauth2.auth;

import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OAuth2 客户端凭证模式请求
 *
 * <pre>
 * {
 *   "grant_type": "client_credentials",
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
public class OAuth2RequestClient extends OAuth2Request {

    public OAuth2RequestClient() {
        super();
    }

    public OAuth2RequestClient(final JObject request) {
        super(request);
        this.setId(this.clientId);
        this.setCredential(this.clientSecret);
    }

    @Override
    public String getGrantType() {
        return "client_credentials";
    }
}

