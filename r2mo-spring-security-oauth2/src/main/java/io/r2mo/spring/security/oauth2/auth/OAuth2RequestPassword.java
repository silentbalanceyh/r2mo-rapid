package io.r2mo.spring.security.oauth2.auth;

import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

/**
 * OAuth2 密码模式请求
 *
 * <pre>
 * {
 *   "grant_type": "password",
 *   "username": "用户名",
 *   "password": "密码",
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
public class OAuth2RequestPassword extends OAuth2Request {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    public OAuth2RequestPassword() {
        super();
    }

    public OAuth2RequestPassword(final JObject request) {
        super(request);
        this.username = request.getString(OAuth2ParameterNames.USERNAME);
        this.password = request.getString(OAuth2ParameterNames.PASSWORD);
        this.setId(this.username);
        this.setCredential(this.password);
    }

    @Override
    public AuthorizationGrantType typeGrant() {
        return new AuthorizationGrantType("password");
    }
}

