package io.r2mo.spring.security.oauth2.config;

import io.r2mo.base.util.R2MO;
import io.r2mo.spring.security.config.ConfigSecurityJwt;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * r2mo OAuth2 插件配置
 * 绑定 security.oauth2 节点，是 r2mo-spring-security-oauth2 的主配置入口。
 * <pre>
 * 设计目标：
 * - 配置尽量少，只保留 OAuth2 必需选项；
 * - JWT 相关时间配置可复用 security.jwt；
 * - spring.security.oauth2.* 存在时，插件自动“让位”。
 * </pre>
 * <pre>
 * 外部推荐使用的方法：
 * - isNative()    : 是否存在原生 spring.security.oauth2 配置
 * - isOn()        : 插件是否真正接管
 * - isJwt()       : 是否使用 JWT 模式
 * - isOidc()      : 是否使用 OIDC 模式
 * - isResource()  : Resource Server 是否启用
 * - issuer()      : 有效 Issuer（原生优先）
 * - msAccessAt()  : AccessToken 过期毫秒数
 * - msRefreshAt() : RefreshToken 过期毫秒数
 * </pre>
 *
 * @author lang : 2025-11-13
 */
@Configuration
@ConfigurationProperties(prefix = "security.oauth2")
@Data
public class ConfigOAuth2 implements Serializable {

    // ---------------- 基础开关 ----------------

    /**
     * 是否启用 r2mo OAuth2 插件（配置层面的开关）
     */
    private boolean enabled;

    /**
     * OAuth2 模式：
     * - JWT  : 标准 OAuth2 + JWT AccessToken / RefreshToken
     * - OIDC : 在 JWT 基础上开启 OIDC（id_token / userinfo / discovery）
     */
    private OAuth2TokenMode mode = OAuth2TokenMode.JWT;

    /**
     * r2mo 模式下的 Issuer
     * 若 spring.security.oauth2.authorizationserver.issuer 存在，则优先使用原生配置。
     */
    private String issuer;

    // ---------------- Token 配置 ----------------

    /**
     * AccessToken 过期时间（如：30m / 2h / 1d）
     * 为空时可复用 security.jwt.expiredAt。
     */
    private String accessTokenAt;

    /**
     * RefreshToken 过期时间（如：7d / 30d）
     * 为空时可复用 security.jwt.refreshAt。
     */
    private String refreshTokenAt;

    /**
     * 是否重用 RefreshToken
     */
    private boolean reuseRefreshToken = true;

    /**
     * 是否启用 Resource Server 保护业务接口
     */
    private boolean resourceEnabled = true;

    // ---------------- OIDC / 客户端 ----------------

    /**
     * OIDC 配置
     */
    private Oidc oidc = new Oidc();

    /**
     * JWK 配置
     */
    private Jwk jwk = new Jwk();

    /**
     * Authorization Server 设置
     */
    private ServerSettings serverSettings = new ServerSettings();

    /**
     * 静态客户端列表，启动时同步到 oauth2_registered_client
     */
    private List<ConfigOAuth2Client> clients = new ArrayList<>();

    // ---------------- 注入的其它配置（可选） ----------------

    /**
     * 可选：原生 spring.security.oauth2.* 配置
     */
    @Autowired(required = false)
    private ConfigOAuth2Spring nativeCfg;

    /**
     * 可选：原有 security.jwt 配置，用于复用过期时间等。
     */
    @Autowired(required = false)
    private ConfigSecurityJwt jwtCfg;

    // ---------------- 统一的便捷方法 ----------------

    /**
     * 是否存在原生 spring.security.oauth2 配置。
     */
    public boolean isNative() {
        return this.nativeCfg != null && this.nativeCfg.isEnabled();
    }

    /**
     * 插件是否真正启用（接管配置）。
     * 语义：
     * - enabled = true 且不存在原生配置 → 插件接管 → true
     * - enabled = true 且存在原生配置 → 让位给 spring.security.oauth2 → false
     */
    public boolean isOn() {
        return this.enabled || this.isNative();
    }

    /**
     * 当前是否使用 JWT 模式（插件维度）。
     */
    public boolean isJwt() {
        return this.isOn() && this.mode == OAuth2TokenMode.JWT;
    }

    /**
     * 当前是否使用 OIDC 模式（插件维度）。
     */
    public boolean isOidc() {
        return this.isOn() && this.mode == OAuth2TokenMode.OIDC;
    }

    /**
     * Resource Server 是否启用（插件维度）。
     */
    public boolean isResource() {
        return this.isOn() && this.resourceEnabled;
    }

    /**
     * 有效的 Issuer：
     * - 优先使用 spring.security.oauth2.authorizationserver.issuer；
     * - 否则使用 security.oauth2.issuer；
     * - 都为空时返回 null。
     */
    public String issuer() {
        if (this.nativeCfg != null
            && this.nativeCfg.getAuthorizationserver() != null
            && this.nativeCfg.getAuthorizationserver().getIssuer() != null
            && !this.nativeCfg.getAuthorizationserver().getIssuer().isBlank()) {
            return this.nativeCfg.getAuthorizationserver().getIssuer();
        }
        return this.issuer;
    }

    // ---------------- 时间解析（先用自己，空则复用 jwtCfg） ----------------

    public Duration accessAt() {
        if (this.accessTokenAt != null && !this.accessTokenAt.isBlank()) {
            return R2MO.toDuration(this.accessTokenAt);
        }
        if (this.jwtCfg != null) {
            // jwtCfg.msExpiredAt() 是毫秒
            return Duration.ofMillis(this.jwtCfg.msExpiredAt());
        }
        // 默认兜底：30m
        return R2MO.toDuration("30m");
    }

    public Duration refreshAt() {
        if (this.refreshTokenAt != null && !this.refreshTokenAt.isBlank()) {
            return R2MO.toDuration(this.refreshTokenAt);
        }
        if (this.jwtCfg != null) {
            return Duration.ofMillis(this.jwtCfg.msRefreshAt());
        }
        // 默认兜底：7d
        return R2MO.toDuration("7d");
    }

    /**
     * AccessToken 过期时间（毫秒）
     */
    public long msAccessAt() {
        return this.accessAt().toMillis();
    }

    /**
     * RefreshToken 过期时间（毫秒）
     */
    public long msRefreshAt() {
        return this.refreshAt().toMillis();
    }

    // ---------------- 内部类型 ----------------

    @Data
    public static class Oidc implements Serializable {

        /**
         * 是否在 id_token / userinfo 中附带业务字段（如 email / name 等）
         */
        private boolean userClaims = true;
    }

    @Data
    public static class Jwk implements Serializable {

        /**
         * 密钥 ID
         */
        private String keyId;

        /**
         * 密钥大小（RSA）
         */
        private int keySize = 2048;

        /**
         * KeyStore 配置（可选，用于从文件加载密钥对）
         */
        private JwkKeyStore keyStore;
    }

    @Data
    public static class JwkKeyStore implements Serializable {

        /**
         * KeyStore 文件位置（如：classpath:keystore.jks）
         */
        private String location;

        /**
         * KeyStore 类型（如：JKS, PKCS12）
         */
        private String type = "JKS";

        /**
         * KeyStore 密码
         */
        private String password;

        /**
         * 密钥别名
         */
        private String alias;

        /**
         * 密钥密码
         */
        private String keyPassword;
    }

    @Data
    public static class ServerSettings implements Serializable {

        /**
         * 授权端点路径（默认：/oauth2/authorize）
         */
        private String authorizationEndpoint;

        /**
         * Token 端点路径（默认：/oauth2/token）
         */
        private String tokenEndpoint;

        /**
         * JWK 端点路径（默认：/oauth2/jwks）
         */
        private String jwkSetEndpoint;

        /**
         * Token 撤销端点路径（默认：/oauth2/revoke）
         */
        private String tokenRevocationEndpoint;

        /**
         * Token 内省端点路径（默认：/oauth2/introspect）
         */
        private String tokenIntrospectionEndpoint;

        /**
         * OIDC 发现端点路径（默认：/.well-known/openid-configuration）
         */
        private String oidcConfigurationEndpoint;

        /**
         * UserInfo 端点路径（默认：/userinfo）
         */
        private String oidcUserInfoEndpoint;
    }

}
