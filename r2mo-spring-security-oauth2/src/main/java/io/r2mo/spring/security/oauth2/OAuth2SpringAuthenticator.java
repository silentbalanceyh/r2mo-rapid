package io.r2mo.spring.security.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.enums.TypeToken;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import io.r2mo.spring.security.token.TokenBuilderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Objects;

/**
 * OAuth2 Spring Authenticator
 *
 * 负责将 OAuth2 认证集成到 Spring Security 配置中
 * 注册 Token Builder，处理 OAuth2 相关的认证流程
 *
 * @author lang : 2025-11-13
 */
@Slf4j
public class OAuth2SpringAuthenticator extends SpringAuthenticatorBase {

    private final ConfigSecurityOAuth2 oauth2Config;

    public OAuth2SpringAuthenticator(final ConfigSecurity configuration) {
        super(configuration);
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
        // 注意：OAuth2 Authorization Server 默认使用 JWT Token
        // 这里的 OPAQUE Token Builder 用于特殊场景（如需要不透明 Token 时）
        TokenBuilderManager.of().registry(TypeToken.OPAQUE,
            io.r2mo.spring.security.oauth2.token.OAuth2TokenBuilder::new);
        log.info("[ R2MO ] 注册 OAuth2 Opaque Token Builder");

        // 预留：OAuth2 的 Token 刷新支持
        // 在实际使用中，OAuth2 的 refresh_token 由 Authorization Server 自动管理
        // TokenBuilderManager.of().registry(TypeToken.OPAQUE_REFRESH,
        //     io.r2mo.spring.security.oauth2.token.OAuth2TokenBuilderRefresh::new);

        log.info("[ R2MO ] ( Auth ) OAuth2 认证器配置完成！");
    }
}



