package io.r2mo.spring.security.oauth2.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 映射完整的 spring.security.oauth2.client 配置。
 * <p>
 * 支持所有 Spring Boot 3.x 官方支持的 OAuth2 Client 属性，
 * 可用于自动构建 RegisteredClient（Auth Server 场景）或调试。
 */
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
@Data
public class ConfigOAuth2SpringClient {

    @JsonProperty("registration")
    private final Map<String, Registration> registration = new LinkedHashMap<>();

    @JsonProperty("provider")
    private final Map<String, Provider> provider = new LinkedHashMap<>();

    public boolean isEnabled() {
        return !this.registration.isEmpty() || !this.provider.isEmpty();
    }
    // ==================================================
    // Inner Class: Registration
    // ==================================================

    /**
     * Registration 的格式处理
     * <pre>
     *     spring:
     *       security:
     *         oauth2:
     *           client:
     *             registration:
     *               provider:
     *               client-id:
     *               client-secret:
     *               client-authentication-method:
     *               authorization-grant-type: x,y,z
     *               redirect-uri:
     *               post-logout-redirect-uri:
     *               scope: x,y,z
     *               client-name:
     *               client-setting:
     *                 require-proof-key: false
     *                 require-authorization-consent: false
     *               token-setting:
     *                 expired-at: ??? // 分钟
     *                 refresh-at: ??? // 天
     *                 reuse-refresh-token:  true|false
     * </pre>
     */
    @Data
    public static class Registration {

        @JsonProperty("provider")
        private String provider;

        @JsonProperty("client-id")
        private String clientId;

        @JsonProperty("client-secret")
        private String clientSecret;

        @JsonProperty("client-authentication-method")
        @Nullable
        private String clientAuthenticationMethod;

        @JsonProperty("authorization-grant-type")
        private String authorizationGrantType = "authorization_code";

        @JsonProperty("redirect-uri")
        private String redirectUri;

        @JsonProperty("post-logout-redirect-uri")
        private String redirectUriPostLogout;

        @JsonProperty("scope")
        private List<String> scope = new ArrayList<>();

        @JsonProperty("client-name")
        @Nullable
        private String clientName;

        @JsonProperty("client-setting")
        private JObject settingClient = SPI.J();

        @JsonProperty("token-setting")
        private JObject settingToken = SPI.J();
    }

    // ==================================================
    // Inner Class: Provider
    // ==================================================

    @Data
    public static class Provider {

        @JsonProperty("issuer-uri")
        @Nullable
        private String issuerUri;

        @JsonProperty("authorization-uri")
        @Nullable
        private String authorizationUri;

        @JsonProperty("token-uri")
        @Nullable
        private String tokenUri;

        @JsonProperty("user-info-uri")
        @Nullable
        private String userInfoUri;

        @JsonProperty("user-name-attribute")
        @Nullable
        private String userNameAttribute;

        @JsonProperty("jwk-set-uri")
        @Nullable
        private String jwkSetUri;

        @JsonProperty("end-session-endpoint")
        @Nullable
        private String endSessionEndpoint;
    }
}