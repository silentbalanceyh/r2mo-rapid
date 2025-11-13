package io.r2mo.spring.security.oauth2;

import io.r2mo.spring.security.config.SecurityWebConfigurerBase;
import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * OAuth2 Security 配置器（Bean 级别配置协调器）
 * <p>
 * 负责协调各个 Bean 配置组件：
 * - OAuth2SecurityEncoder（JWT/Opaque Token 编解码器）
 * - OAuth2SecurityRegisteredClient（客户端注册）
 * - OAuth2SecurityAuthorizationServer（Authorization Server 设置）
 * <p>
 * 注意：SecurityFilterChain 的配置在 OAuth2SpringAuthenticator 中完成
 *
 * @author lang : 2025-11-13
 */
@Configuration
@Slf4j
public class OAuth2SecurityConfigurer extends SecurityWebConfigurerBase {

    @Autowired(required = false)
    private ConfigSecurityOAuth2 oauth2Config;

    public OAuth2SecurityConfigurer() {
        super();
    }

    @Override
    public void configure(final HttpSecurity http, final HandlerMappingIntrospector introspector) {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            log.debug("[ R2MO ] OAuth2 未启用或配置缺失，跳过 Bean 配置");
            return;
        }

        // Bean 配置已由以下类自动完成：
        // - OAuth2SecurityEncoder: JWKSource, JwtDecoder, OpaqueTokenIntrospector
        // - OAuth2SecurityRegisteredClient: RegisteredClientRepository, OAuth2AuthorizationService, OAuth2AuthorizationConsentService
        // - OAuth2SecurityAuthorizationServer: AuthorizationServerSettings, OAuth2TokenCustomizer
        // - OAuth2SpringAuthenticator: SecurityFilterChain

        log.info("[ R2MO ] OAuth2 Bean 配置已就绪");
    }
}
