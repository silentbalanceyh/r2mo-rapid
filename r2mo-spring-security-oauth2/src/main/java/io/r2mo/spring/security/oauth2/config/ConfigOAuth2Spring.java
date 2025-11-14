package io.r2mo.spring.security.oauth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring 原生 OAuth2 配置映射
 *
 * 用于：
 * 1. 探测应用是否已经声明 spring.security.oauth2.* 相关配置；
 * 2. 如有需要，可复用部分关键字段（issuer / jwk-set-uri / introspection-uri 等）。
 *
 * 本类只做属性绑定，不做业务逻辑。
 *
 * @author lang : 2025-11-13
 */
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2")
@Data
public class ConfigOAuth2Spring implements Serializable {

    /**
     * Authorization Server 相关配置
     * spring.security.oauth2.authorizationserver.*
     */
    private AuthorizationServer authorizationserver = new AuthorizationServer();

    /**
     * Resource Server 相关配置
     * spring.security.oauth2.resourceserver.*
     */
    private ResourceServer resourceserver = new ResourceServer();

    /**
     * Client 相关配置
     * spring.security.oauth2.client.*
     */
    private ConfigOAuth2SpringClient client = new ConfigOAuth2SpringClient();

    /**
     * 全局判断：是否配置过任意原生 OAuth2 相关属性
     */
    public boolean isEnabled() {
        return this.authorizationserver.isEnabled()
            || this.resourceserver.isEnabled()
            || this.client.isEnabled();
    }

    // ========================
    // 子配置：AuthorizationServer
    // ========================

    @Data
    public static class AuthorizationServer implements Serializable {

        /**
         * Issuer（如果用户用原生方式配置 Authorization Server，会很可能在这里写）
         */
        private String issuer;

        /**
         * 其它零散元数据配置，统一放在这里，避免频繁改类结构。
         */
        private Map<String, Object> metadata = new HashMap<>();

        public boolean isEnabled() {
            return (this.issuer != null && !this.issuer.isBlank())
                || (this.metadata != null && !this.metadata.isEmpty());
        }
    }

    // ========================
    // 子配置：ResourceServer
    // ========================

    @Data
    public static class ResourceServer implements Serializable {

        /**
         * JWT 资源服务器配置
         * spring.security.oauth2.resourceserver.jwt.*
         */
        private Jwt jwt = new Jwt();

        /**
         * Opaque Token 资源服务器配置
         * spring.security.oauth2.resourceserver.opaque-token.*
         */
        private OpaqueToken opaqueToken = new OpaqueToken();

        public boolean isEnabled() {
            return this.jwt.isEnabled() || this.opaqueToken.isEnabled();
        }

        @Data
        public static class Jwt implements Serializable {

            /**
             * JWK Set 地址
             * spring.security.oauth2.resourceserver.jwt.jwk-set-uri
             */
            private String jwkSetUri;

            /**
             * 本地公钥位置（如 classpath:public.key）
             * spring.security.oauth2.resourceserver.jwt.public-key-location
             */
            private String publicKeyLocation;

            public boolean isEnabled() {
                return (this.jwkSetUri != null && !this.jwkSetUri.isBlank())
                    || (this.publicKeyLocation != null && !this.publicKeyLocation.isBlank());
            }
        }

        @Data
        public static class OpaqueToken implements Serializable {

            /**
             * 内省端点 URI
             * spring.security.oauth2.resourceserver.opaque-token.introspection-uri
             */
            private String introspectionUri;

            /**
             * introspection client 信息
             */
            private String clientId;
            private String clientSecret;

            public boolean isEnabled() {
                return (this.introspectionUri != null && !this.introspectionUri.isBlank());
            }
        }
    }
}
