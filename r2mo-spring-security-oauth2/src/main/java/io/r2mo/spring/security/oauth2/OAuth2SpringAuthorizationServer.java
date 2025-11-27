package io.r2mo.spring.security.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spi.SPI;
import io.r2mo.spring.security.config.ConfigSecurityUri;
import io.r2mo.spring.security.oauth2.config.ConfigOAuth2;
import io.r2mo.spring.security.oauth2.config.OAuth2TokenMode;
import io.r2mo.spring.security.oauth2.token.OAuth2JwtTokenCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2TokenEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

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

    private final ConfigOAuth2 oauth2Config;
    private final ConfigSecurityUri securityUriConfig;

    public OAuth2SpringAuthorizationServer(final ConfigOAuth2 oauth2Config) {
        this.oauth2Config = oauth2Config;
        this.securityUriConfig = SpringUtil.getBean(ConfigSecurityUri.class);
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

        // 1. 获取 Authorization Server 中的 Configurer 实例
        final OAuth2AuthorizationServerConfigurer configurer =
            OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
            // 2. 设置匹配器：只拦截授权服务器相关的端点（如 /oauth2/authorize, /oauth2/token 等）
            //    这相当于原来的 securityMatcher 逻辑
            .securityMatcher(configurer.getEndpointsMatcher())

            // 3. 应该使用 Configurer
            .with(configurer, server -> server
                // 配置 OIDC
                .oidc(this::configureOidc)
                // 配置自定义 AuthenticationConverter（SPI）
                .tokenEndpoint(this::configureConverters)
            )

            // ============================================================
            // [关键修复 1]：必须显式要求认证！
            // 否则 /oauth2/authorize 会被视为匿名允许，进而穿透 Filter 报 404
            // ============================================================
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
            )

            // =======================================================
            // [核心修复]：开启表单登录
            // 如果没有这句，Spring 根本不知道去哪里找登录页
            // =======================================================
            .formLogin(Customizer.withDefaults())

            // ============================================================
            // [关键修复 2]：配置异常处理（重定向到登录页）
            // 如果没有这一步，未登录用户访问授权端点时不知道该去哪
            // ============================================================
            .exceptionHandling(exceptions -> exceptions
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint(this.securityUriConfig.getLogin()), // 假设你的登录页路径是 /login
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            );

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
            log.info("[ R2MO ] 配置 Authorization Server / ( Issuer ) = {}", issuer);
        }

        // 自定义端点路径
        final var settings = this.oauth2Config.getServerSettings();

        // 授权端点
        String uriAuthorization = settings != null ? settings.getAuthorizationEndpoint() : null;
        if (uriAuthorization == null) {
            uriAuthorization = OAuth2Endpoint.AUTHORIZE();
        }
        log.info("[ R2MO ]     ----> {} （授权端点）", uriAuthorization);
        builder.authorizationEndpoint(uriAuthorization);

        // 令牌端点
        String uriToken = settings != null ? settings.getTokenEndpoint() : null;
        if (uriToken == null) {
            uriToken = OAuth2Endpoint.TOKEN();
        }
        log.info("[ R2MO ]     ----> {} （令牌端点）", uriToken);
        builder.tokenEndpoint(uriToken);

        // JWK Set 端点
        String uriJwkSet = settings != null ? settings.getJwkSetEndpoint() : null;
        if (uriJwkSet == null) {
            uriJwkSet = OAuth2Endpoint.JWKS();
        }
        log.info("[ R2MO ]     ----> {} （JWK Set 端点）", uriJwkSet);
        builder.jwkSetEndpoint(uriJwkSet);

        // 令牌撤销端点
        String uriTokenRevocation = settings != null ? settings.getTokenRevocationEndpoint() : null;
        if (uriTokenRevocation == null) {
            uriTokenRevocation = OAuth2Endpoint.REVOKE();
        }
        log.info("[ R2MO ]     ----> {} （令牌撤销端点）", uriTokenRevocation);
        builder.tokenRevocationEndpoint(uriTokenRevocation);

        // 令牌内省端点
        String uriTokenIntrospection = settings != null ? settings.getTokenIntrospectionEndpoint() : null;
        if (uriTokenIntrospection == null) {
            uriTokenIntrospection = OAuth2Endpoint.INTROSPECT();
        }
        log.info("[ R2MO ]     ----> {} （令牌内省端点）", uriTokenIntrospection);
        builder.tokenIntrospectionEndpoint(uriTokenIntrospection);

        // OIDC 配置端点（暂不支持自定义）
        if (settings != null && settings.getOidcConfigurationEndpoint() != null) {
            log.warn("[ R2MO ] OIDC 配置端点暂不支持自定义");
        }

        // OIDC UserInfo 端点
        String uriOidcUserInfo = settings != null ? settings.getOidcUserInfoEndpoint() : null;
        if (uriOidcUserInfo == null) {
            uriOidcUserInfo = "/userinfo";
        }
        log.info("[ R2MO ]     ----> {} （OIDC UserInfo 端点）", uriOidcUserInfo);
        builder.oidcUserInfoEndpoint(uriOidcUserInfo);

        log.info("[ R2MO ] 配置 AuthorizationServerSettings 完成");
        return builder.build();
    }
}
