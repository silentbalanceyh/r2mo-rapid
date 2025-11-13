package io.r2mo.spring.security.oauth2.auth;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import lombok.Data;

import java.util.Map;

/**
 * OAuth2 登录响应
 *
 * @author lang : 2025-11-13
 */
@Data
public class OAuth2Response {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 令牌类型（通常是 Bearer）
     */
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 授权范围
     */
    private String scope;

    /**
     * ID Token（OIDC 模式）
     */
    private String idToken;

    /**
     * 额外信息
     */
    private Map<String, Object> additionalInfo;

    public OAuth2Response() {
    }

    public OAuth2Response(final UserAt userAt) {
        // 这里会由 OAuth2TokenBuilder 填充具体的 Token 信息
        // 目前仅作为占位符
    }

    public JObject toJson() {
        final JObject result = SPI.J();
        result.put("access_token", this.accessToken);
        result.put("token_type", this.tokenType);
        if (this.expiresIn != null) {
            result.put("expires_in", this.expiresIn);
        }
        if (this.refreshToken != null) {
            result.put("refresh_token", this.refreshToken);
        }
        if (this.scope != null) {
            result.put("scope", this.scope);
        }
        if (this.idToken != null) {
            result.put("id_token", this.idToken);
        }
        if (this.additionalInfo != null && !this.additionalInfo.isEmpty()) {
            this.additionalInfo.forEach(result::put);
        }
        return result;
    }
}

