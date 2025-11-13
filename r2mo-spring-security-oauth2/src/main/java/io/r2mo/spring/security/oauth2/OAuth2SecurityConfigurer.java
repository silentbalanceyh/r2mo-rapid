package io.r2mo.spring.security.oauth2;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.r2mo.spi.SPI;
import io.r2mo.spring.security.config.SecurityWebConfigurerBase;
import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import io.r2mo.spring.security.oauth2.config.OAuth2TokenMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Comparator;
import java.util.List;

/**
 * OAuth2 Authorization Server 配置器
 *
 * SPI 插件化集成：OAuth2 认证配置
 *
 * 功能：
 * 1. 自动发现 AuthenticationProvider 和 AuthenticationConverter（SPI）
 * 2. JWT Token 定制（从 UserDetails 和 MSUser 提取 Claims）
 * 3. 支持 OIDC
 * 4. 支持公私钥对（KeyStore）
 * 5. 可定制 AuthorizationServerSettings
 *
 * @author lang : 2025-11-13
 */
@Configuration
@Slf4j
public class OAuth2SecurityConfigurer extends SecurityWebConfigurerBase {

    @Autowired(required = false)
    private ConfigSecurityOAuth2 oauth2Config;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private OAuth2RegisteredClientInitializer clientInitializer;

    @Autowired(required = false)
    private ResourceLoader resourceLoader;

    public OAuth2SecurityConfigurer() {
        super();
    }

    @Override
    public void configure(final HttpSecurity http, final HandlerMappingIntrospector introspector) {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            log.warn("[ R2MO ] OAuth2 未启用或配置缺失，跳过配置");
            return;
        }

        try {
            log.info("[ R2MO ] 开始配置 OAuth2 Authorization Server...");
            log.info("[ R2MO ] OAuth2 模式：{}", this.oauth2Config.getMode());
            log.info("[ R2MO ] OAuth2 Issuer：{}", this.oauth2Config.issuer());

            // 配置 Authorization Server
            this.configureAuthorizationServer(http);

            // 如果启用 Resource Server，配置 JWT 验证
            if (this.oauth2Config.isResource()) {
                this.configureResourceServer(http);
            }

            log.info("[ R2MO ] OAuth2 Authorization Server 配置完成");

        } catch (final Exception ex) {
            log.error("[ R2MO ] OAuth2 配置失败", ex);
            throw new RuntimeException("OAuth2 配置失败", ex);
        }
    }

    /**
     * 配置 Authorization Server
     */
    private void configureAuthorizationServer(final HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            // 配置 OIDC
            .oidc(oidc -> {
                if (this.oauth2Config.getMode() == OAuth2TokenMode.OIDC) {
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
                final List<OAuth2AuthenticationConverter> spiConverters = SPI.findMany(OAuth2AuthenticationConverter.class);
                if (!spiConverters.isEmpty()) {
                    log.info("[ R2MO ] 发现 {} 个自定义 AuthenticationConverter", spiConverters.size());
                    tokenEndpoint.accessTokenRequestConverters(converters -> {
                        spiConverters.stream()
                            .sorted(Comparator.comparingInt(OAuth2AuthenticationConverter::getOrder))
                            .forEach(converter -> {
                                converters.add((AuthenticationConverter) converter);
                                log.info("[ R2MO ]   - {} (优先级: {})",
                                    converter.getClass().getName(),
                                    converter.getOrder());
                            });
                    });
                }
            });

        // 注册自定义 AuthenticationProvider（通过 HttpSecurity）
        final List<OAuth2AuthenticationProvider> spiProviders = SPI.findMany(OAuth2AuthenticationProvider.class);
        if (!spiProviders.isEmpty()) {
            log.info("[ R2MO ] 发现 {} 个自定义 AuthenticationProvider", spiProviders.size());
            spiProviders.stream()
                .sorted(Comparator.comparingInt(OAuth2AuthenticationProvider::getOrder))
                .forEach(provider -> {
                    http.authenticationProvider((AuthenticationProvider) provider);
                    log.info("[ R2MO ]   - {} (优先级: {})",
                        provider.getClass().getName(),
                        provider.getOrder());
                });
        }

        // 异常处理
        http.exceptionHandling(exceptions ->
            exceptions
                .authenticationEntryPoint(this.handler().handlerAuthentication())
                .accessDeniedHandler(this.handler().handlerAuthorization())
        );
    }

    /**
     * 配置 Resource Server（JWT 验证）
     */
    private void configureResourceServer(final HttpSecurity http) throws Exception {
        if (this.oauth2Config.isJwt() || this.oauth2Config.isOidc()) {
            log.info("[ R2MO ] 配置 OAuth2 Resource Server - JWT 验证");
            http.oauth2ResourceServer(resource ->
                resource.jwt(Customizer.withDefaults())
            );
        }
    }

    /**
     * 创建 SecurityFilterChain（高优先级，处理 OAuth2 端点）
     */
    @Bean
    @Order(1)
    public SecurityFilterChain oauth2SecurityFilterChain(final HttpSecurity http) throws Exception {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            return null;
        }

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.exceptionHandling(exceptions ->
            exceptions
                .authenticationEntryPoint(this.handler().handlerAuthentication())
        );

        return http.build();
    }

    /**
     * RegisteredClient 仓库
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            return null;
        }

        if (this.jdbcTemplate == null) {
            throw new IllegalStateException("[ R2MO ] OAuth2 需要 JdbcTemplate，请配置数据源");
        }

        return this.clientInitializer.build(this.jdbcTemplate);
    }

    /**
     * OAuth2 授权服务
     */
    @Bean
    public OAuth2AuthorizationService authorizationService() {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            return null;
        }

        if (this.jdbcTemplate == null) {
            throw new IllegalStateException("[ R2MO ] OAuth2 需要 JdbcTemplate，请配置数据源");
        }

        final RegisteredClientRepository clientRepository = this.registeredClientRepository();
        return new JdbcOAuth2AuthorizationService(this.jdbcTemplate, clientRepository);
    }

    /**
     * OAuth2 授权同意服务
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService() {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            return null;
        }

        if (this.jdbcTemplate == null) {
            throw new IllegalStateException("[ R2MO ] OAuth2 需要 JdbcTemplate，请配置数据源");
        }

        final RegisteredClientRepository clientRepository = this.registeredClientRepository();
        return new JdbcOAuth2AuthorizationConsentService(this.jdbcTemplate, clientRepository);
    }

    /**
     * JWK Source（用于 JWT 签名）
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            return null;
        }

        final OAuth2JwkSourceManager manager = new OAuth2JwkSourceManager(this.oauth2Config, this.resourceLoader);
        return manager.createJwkSource();
    }

    /**
     * JWT Decoder
     */
    @Bean
    public JwtDecoder jwtDecoder(final JWKSource<SecurityContext> jwkSource) {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            return null;
        }

        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * JWT Token Customizer
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            return null;
        }

        return new OAuth2JwtTokenCustomizer();
    }

    /**
     * Authorization Server 设置
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            return null;
        }

        final String issuer = this.oauth2Config.issuer();
        final AuthorizationServerSettings.Builder builder = AuthorizationServerSettings.builder();

        if (issuer != null && !issuer.isBlank()) {
            builder.issuer(issuer);
        }

        // 自定义端点路径
        final ConfigSecurityOAuth2.ServerSettings settings = this.oauth2Config.getServerSettings();
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
                // OIDC 配置端点暂时不支持自定义（Spring 限制）
                log.warn("[ R2MO ] OIDC 配置端点暂不支持自定义");
            }
            if (settings.getOidcUserInfoEndpoint() != null) {
                builder.oidcUserInfoEndpoint(settings.getOidcUserInfoEndpoint());
            }
        }

        return builder.build();
    }
}

