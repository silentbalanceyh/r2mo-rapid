package io.r2mo.spring.security.oauth2;

import io.r2mo.spi.SPI;
import io.r2mo.spring.security.oauth2.config.OAuth2TokenMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Comparator;
import java.util.List;

/**
 * OAuth2 Authorization Server 配置
 * <p>
 * 专门负责 Authorization Server 设置和 SPI 注入：
 * - AuthorizationServerSettings（服务器端点配置）
 * - OAuth2TokenCustomizer（JWT Token 自定义）
 * - SPI AuthenticationProvider 配置
 * - SPI AuthenticationConverter 配置
 *
 * @author lang : 2025-11-13
 */
@Configuration
@Slf4j
public class OAuth2SecurityAuthorizationServer extends OAuth2ConfigurationBase {

    /**
     * 配置 SPI AuthenticationProvider 到 HttpSecurity
     */
    public void configureSpiProviders(final HttpSecurity http) {
        final List<OAuth2AuthenticationProvider> spiProviders = SPI.findMany(OAuth2AuthenticationProvider.class);
        if (!spiProviders.isEmpty()) {
            log.info("[ R2MO ] 发现 {} 个自定义 AuthenticationProvider", spiProviders.size());
            spiProviders.stream()
                .sorted(Comparator.comparingInt(OAuth2AuthenticationProvider::getOrder))
                .forEach(provider -> {
                    http.authenticationProvider(provider);
                    log.info("[ R2MO ]   - {} (优先级: {})",
                        provider.getClass().getName(),
                        provider.getOrder());
                });
        }
    }

    /**
     * 获取 SPI AuthenticationConverter 列表
     */
    public List<OAuth2AuthenticationConverter> getSpiConverters() {
        return SPI.findMany(OAuth2AuthenticationConverter.class);
    }

    /**
     * 将 Authorization Server 的 HttpSecurity 配置移到这里
     */
    public void configureAuthorizationServer(final HttpSecurity http) throws Exception {
        // 确保应用默认的 Authorization Server 配置
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            // 配置 OIDC
            .oidc(oidc -> {
                if (this.oauth2Config != null && this.oauth2Config.getMode() == OAuth2TokenMode.OIDC) {
                    log.info("[ R2MO ] 启用 OIDC 配置");
                    oidc.userInfoEndpoint(userInfo -> {
                        if (this.oauth2Config.getOidc().isUserClaims()) {
                            log.info("[ R2MO ] OIDC UserInfo 端点已启用");
                        }
                    });
                }
            })
            // 配置自定义 AuthenticationConverter（SPI）
            .tokenEndpoint(tokenEndpoint -> {
                final List<OAuth2AuthenticationConverter> spiConverters = this.getSpiConverters();
                if (!spiConverters.isEmpty()) {
                    log.info("[ R2MO ] 发现 {} 个自定义 AuthenticationConverter", spiConverters.size());
                    tokenEndpoint.accessTokenRequestConverters(converters ->
                        spiConverters.stream()
                            .sorted(Comparator.comparingInt(OAuth2AuthenticationConverter::getOrder))
                            .forEach(converter -> {
                                converters.add(converter);
                                log.info("[ R2MO ]   - {} (优先级: {})",
                                    converter.getClass().getName(),
                                    converter.getOrder());
                            })
                    );
                }
            });

        // 注册自定义 AuthenticationProvider（SPI）
        this.configureSpiProviders(http);
    }

    /**
     * 配置 JWT Token Customizer
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> configureJwtTokenCustomizer() {
        return this.configureIfEnabled(() -> {
            log.info("[ R2MO ] 配置 JWT Token Customizer");
            return new OAuth2JwtTokenCustomizer();
        });
    }

    /**
     * 配置 Authorization Server 设置
     */
    @Bean
    public AuthorizationServerSettings configureAuthorizationServerSettings() {
        return this.configureIfEnabled(() -> {
            final String issuer = this.oauth2Config.issuer();
            final AuthorizationServerSettings.Builder builder = AuthorizationServerSettings.builder();

            if (issuer != null && !issuer.isBlank()) {
                builder.issuer(issuer);
                log.info("[ R2MO ] 配置 Authorization Server Issuer: {}", issuer);
            }

            // 自定义端点路径
            final var settings = this.oauth2Config.getServerSettings();
            if (settings != null) {
                if (settings.getAuthorizationEndpoint() != null) {
                    builder.authorizationEndpoint(settings.getAuthorizationEndpoint());
                }
                if (settings.getTokenEndpoint() != null) {
                    builder.tokenEndpoint(settings.getTokenEndpoint());
                }
                if (settings.getJwkSetEndpoint() != null) {
                    builder.jwkSetEndpoint(settings.getJwkSetEndpoint());
                }
                if (settings.getTokenRevocationEndpoint() != null) {
                    builder.tokenRevocationEndpoint(settings.getTokenRevocationEndpoint());
                }
                if (settings.getTokenIntrospectionEndpoint() != null) {
                    builder.tokenIntrospectionEndpoint(settings.getTokenIntrospectionEndpoint());
                }
                if (settings.getOidcConfigurationEndpoint() != null) {
                    log.warn("[ R2MO ] OIDC 配置端点暂不支持自定义");
                }
                if (settings.getOidcUserInfoEndpoint() != null) {
                    builder.oidcUserInfoEndpoint(settings.getOidcUserInfoEndpoint());
                }
            }

            log.info("[ R2MO ] 配置 AuthorizationServerSettings 完成");
            return builder.build();
        });
    }
}
