package io.r2mo.spring.security.oauth2.auth;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.enums.TypeLogin;
import io.r2mo.typed.domain.BaseScope;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

/**
 * OAuth2 登录请求基类
 *
 * @author lang : 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class OAuth2Request extends LoginRequest {

    /**
     * 客户端 ID
     */
    protected String clientId;

    /**
     * 客户端密钥（部分授权模式需要）
     */
    protected String clientSecret;

    /**
     * 授权范围
     */
    protected String scope;

    @Override
    public TypeLogin type() {
        return TypeLogin.OAUTH2;
    }

    /**
     * 获取 OAuth2 授权类型
     */
    public abstract AuthorizationGrantType typeGrant();

    protected OAuth2Request() {
    }

    protected OAuth2Request(final JObject request) {
        this.clientId = request.getString(OAuth2ParameterNames.CLIENT_ID);
        this.clientSecret = request.getString(OAuth2ParameterNames.CLIENT_SECRET);
        this.scope = request.getString(OAuth2ParameterNames.SCOPE);
        this.app(request.getString(BaseScope.F_APP_ID));
        this.tenant(request.getString(BaseScope.F_TENANT_ID));
    }
}



