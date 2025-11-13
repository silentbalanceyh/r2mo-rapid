package io.r2mo.spring.security.oauth2;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2Native;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.StringUtils;

/**
 * OAuth2 Security Encoder 配置
 * <p>
 * 专门负责 JWT 和 Opaque Token 的编解码器：
 * - JWKSource（JWT 签名密钥源）
 * - JwtDecoder（JWT 解码器）
 * - OpaqueTokenIntrospector（不透明令牌内省器）
 *
 * @author lang : 2025-11-13
 */
@Configuration
@Slf4j
public class OAuth2SecurityEncoder extends OAuth2ConfigurationBase {

    @Autowired(required = false)
    private ResourceLoader resourceLoader;

    /**
     * 配置 JWK Source（用于 JWT 签名）
     */
    @Bean
    public JWKSource<SecurityContext> configureJwkSource() {
        return this.configureIfEnabled(() -> {
            final OAuth2JwkSourceManager manager = new OAuth2JwkSourceManager(this.oauth2Config, this.resourceLoader);
            log.info("[ R2MO ] 配置 JWKSource");
            return manager.createJwkSource();
        });
    }

    /**
     * 配置 JWT Decoder
     */
    @Bean
    public JwtDecoder configureJwtDecoder(final JWKSource<SecurityContext> jwkSource) {
        return this.configureIfEnabled(() -> {
            log.info("[ R2MO ] 配置 JwtDecoder");
            return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
        });
    }

    /**
     * 配置 Opaque Token Introspector
     */
    @Bean
    @SuppressWarnings("deprecation")
    public OpaqueTokenIntrospector configureOpaqueTokenIntrospector() {
        return this.configureIfEnabled(() -> {
            // 1) 优先使用原生配置（如果存在）
            try {
                final ConfigSecurityOAuth2Native nativeCfg = this.oauth2Config.getNativeCfg();
                if (nativeCfg != null && nativeCfg.getResourceserver() != null
                    && nativeCfg.getResourceserver().getOpaqueToken() != null
                    && nativeCfg.getResourceserver().getOpaqueToken().isEnabled()) {
                    final String introspectionUri = nativeCfg.getResourceserver().getOpaqueToken().getIntrospectionUri();
                    final String clientId = nativeCfg.getResourceserver().getOpaqueToken().getClientId();
                    final String clientSecret = nativeCfg.getResourceserver().getOpaqueToken().getClientSecret();

                    if (StringUtils.hasText(introspectionUri) && StringUtils.hasText(clientId) && StringUtils.hasText(clientSecret)) {
                        log.info("[ R2MO ] 使用原生配置创建 OpaqueTokenIntrospector: {}", introspectionUri);
                        return new NimbusOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
                    }
                }

                // 2) 回退到插件配置：serverSettings.tokenIntrospectionEndpoint + issuer
                final ConfigSecurityOAuth2.ServerSettings settings = this.oauth2Config.getServerSettings();
                if (settings != null && settings.getTokenIntrospectionEndpoint() != null && this.oauth2Config.issuer() != null) {
                    String endpoint = settings.getTokenIntrospectionEndpoint();
                    String issuer = this.oauth2Config.issuer();
                    // 拼接 URL（处理斜杠）
                    String introspectionUri = endpoint.startsWith("http") ? endpoint
                        : (issuer.endsWith("/") ? issuer.substring(0, issuer.length() - 1) : issuer)
                        + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);

                    // 尝试从插件静态 clients 中选择一个支持 client_credentials 的客户端作为 introspection client
                    String clientId = null;
                    String clientSecret = null;
                    if (this.oauth2Config.getClients() != null) {
                        for (final ConfigSecurityOAuth2.Client c : this.oauth2Config.getClients()) {
                            if (c.getGrantTypes() != null && c.getGrantTypes().contains("client_credentials")
                                && StringUtils.hasText(c.getClientId()) && StringUtils.hasText(c.getClientSecret())) {
                                clientId = c.getClientId();
                                clientSecret = c.getClientSecret();
                                break;
                            }
                        }
                    }

                    if (StringUtils.hasText(clientId) && StringUtils.hasText(clientSecret)) {
                        log.info("[ R2MO ] 使用插件配置创建 OpaqueTokenIntrospector: {}", introspectionUri);
                        return new NimbusOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
                    }

                    log.warn("[ R2MO ] 找到 introspection endpoint {} 但未找到可用的 client credentials，无法创建 OpaqueTokenIntrospector", introspectionUri);
                    return null;
                }

                // 3) 无法创建
                log.debug("[ R2MO ] 未配置 Opaque Token 内省信息（native 或 plugin），OpaqueTokenIntrospector 未创建");
                return null;
            } catch (final Exception ex) {
                log.error("[ R2MO ] 创建 OpaqueTokenIntrospector 失败", ex);
                return null;
            }
        });
    }
}
