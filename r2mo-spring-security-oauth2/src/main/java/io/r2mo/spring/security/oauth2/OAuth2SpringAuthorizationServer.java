package io.r2mo.spring.security.oauth2;

import io.r2mo.spi.SPI;
import io.r2mo.spring.security.oauth2.bean.OAuth2JwtTokenCustomizer;
import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import io.r2mo.spring.security.oauth2.config.OAuth2TokenMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcConfigurer;
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
public class OAuth2SpringAuthorizationServer {

    private final ConfigSecurityOAuth2 oauth2Config;

    public OAuth2SpringAuthorizationServer(final ConfigSecurityOAuth2 oauth2Config) {
        this.oauth2Config = oauth2Config;
    }


    /**
     * 配置 SPI AuthenticationProvider 到 HttpSecurity
     */
    private void configureProviders(final HttpSecurity http) {
        final List<OAuth2AuthenticationProvider> providers = SPI.findMany(OAuth2AuthenticationProvider.class);
        if (providers.isEmpty()) {
            return;
        }


        providers.stream()
            .sorted(Comparator.comparingInt(OAuth2AuthenticationProvider::getOrder))
            .forEach(http::authenticationProvider);
        log.info("[ R2MO ] 发现 {} 个自定义 AuthenticationProvider", providers.size());
    }


    private void configureConverters(final OAuth2TokenEndpointConfigurer configurer) {
        final List<OAuth2AuthenticationConverter> converters = SPI.findMany(OAuth2AuthenticationConverter.class);
        if (converters.isEmpty()) {
            return;
        }

        log.info("[ R2MO ] 发现 {} 个自定义 AuthenticationConverter", converters.size());
        configurer.accessTokenRequestConverters(convertList ->
            converters.stream()
                .sorted(Comparator.comparingInt(OAuth2AuthenticationConverter::getOrder))
                .forEach(convertList::add)
        );
    }


    /**
     * 将 Authorization Server 的 HttpSecurity 配置移到这里
     */
    public void configureAuthorizationServer(final HttpSecurity http) throws Exception {

        // 确保应用默认的 Authorization Server 配置
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            // 配置 OIDC
            .oidc(this::configureOidc)
            // 配置自定义 AuthenticationConverter（SPI）
            .tokenEndpoint(this::configureConverters);

        // 注册自定义 AuthenticationProvider（SPI）
        this.configureProviders(http);
    }


    /**
     * 配置 OIDC 相关设置
     */
    private void configureOidc(final OidcConfigurer oidc) {
        if (OAuth2TokenMode.OIDC != this.oauth2Config.getMode()) {
            return;
        }
        log.info("[ R2MO ] 启用 OIDC 配置");
        oidc.userInfoEndpoint(userInfo -> {
            if (this.oauth2Config.getOidc() != null && this.oauth2Config.getOidc().isUserClaims()) {
                log.info("[ R2MO ] OIDC UserInfo 端点已启用");
            }
        });
    }


    /**
     * 配置 JWT Token Customizer
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> configureJwtTokenCustomizer() {
        log.info("[ R2MO ] 配置 JWT Token Customizer");
        return new OAuth2JwtTokenCustomizer();
    }


    /**
     * 配置 Authorization Server 设置
     */
    @Bean
    public AuthorizationServerSettings configureAuthorizationServerSettings() {
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
    }
}
