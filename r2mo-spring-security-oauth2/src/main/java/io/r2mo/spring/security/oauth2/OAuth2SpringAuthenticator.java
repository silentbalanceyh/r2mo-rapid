package io.r2mo.spring.security.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.enums.TypeToken;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import io.r2mo.spring.security.oauth2.token.OAuth2TokenBuilder;
import io.r2mo.spring.security.oauth2.token.OAuth2TokenBuilderRefresh;
import io.r2mo.spring.security.token.TokenBuilderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Objects;

/**
 * OAuth2 Spring Authenticator
 *
 * 负责 OAuth2 SecurityFilterChain 的完整配置：
 * - 创建 OAuth2 SecurityFilterChain Bean
 * - 配置 Authorization Server（OIDC、SPI Provider/Converter）
 * - 配置 Resource Server（JWT 验证）
 * - 异常处理
 * - 注册 Token Builder
 *
 * @author lang : 2025-11-13
 */
@Configuration
@Slf4j
public class OAuth2SpringAuthenticator extends SpringAuthenticatorBase {

    private final ConfigSecurityOAuth2 oauth2Config;

    @Autowired(required = false)
    private OAuth2SecurityAuthorizationServer authorizationServerConfig;

    public OAuth2SpringAuthenticator() {
        super(SpringUtil.getBean(ConfigSecurity.class));
        // 从 Spring 容器获取 OAuth2 配置
        this.oauth2Config = SpringUtil.getBean(ConfigSecurityOAuth2.class);
    }

    @Override
    public void configure(final HttpSecurity security, final Object attached) {
        if (Objects.isNull(this.oauth2Config) || !this.oauth2Config.isOn()) {
            log.warn("[ R2MO ] OAuth2 认证未启用，跳过 OAuth2 认证配置");
            return;
        }

        // 注册 OAuth2 的 Token 配置（Opaque Token）
        TokenBuilderManager.of().registry(TypeToken.OPAQUE, OAuth2TokenBuilder::new);
        TokenBuilderManager.of().registry(TypeToken.OPAQUE_REFRESH, OAuth2TokenBuilderRefresh::new);

        log.info("[ R2MO ] ( Auth ) OAuth2 认证器配置完成！");
    }

    /**
     * 将 SecurityFilterChain 的配置逻辑暴露为公有方法，实际的 @Bean 在独立配置类中创建
     */
    public void configureSecurityFilterChain(final HttpSecurity http) throws Exception {
        if (this.oauth2Config == null || !this.oauth2Config.isOn()) {
            return;
        }

        log.info("[ R2MO ] 开始配置 OAuth2 SecurityFilterChain...");
        log.info("[ R2MO ] OAuth2 模式：{}", this.oauth2Config.getMode());
        log.info("[ R2MO ] OAuth2 Issuer：{}", this.oauth2Config.issuer());

        // Authorization Server 的默认安全配置由 OAuth2SecurityAuthorizationServer.applyDefaultSecurity 负责

        // 配置 Authorization Server（委托）
        if (this.authorizationServerConfig != null) {
            this.authorizationServerConfig.configureAuthorizationServer(http);
        }

        // 如果启用 Resource Server，配置 JWT 验证
        if (this.oauth2Config.isResource()) {
            this.configureResourceServer(http);
        }

        log.info("[ R2MO ] OAuth2 SecurityFilterChain 配置完成");
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
}
