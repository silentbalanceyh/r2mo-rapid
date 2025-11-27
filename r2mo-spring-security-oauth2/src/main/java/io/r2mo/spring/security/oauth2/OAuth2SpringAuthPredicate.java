package io.r2mo.spring.security.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.enums.TypeToken;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import io.r2mo.spring.security.oauth2.config.ConfigOAuth2;
import io.r2mo.spring.security.oauth2.token.OAuth2TokenBuilder;
import io.r2mo.spring.security.oauth2.token.OAuth2TokenBuilderRefresh;
import io.r2mo.spring.security.token.TokenBuilderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author lang : 2025-11-27
 */
@Slf4j
public class OAuth2SpringAuthPredicate extends SpringAuthenticatorBase {


    private final ConfigOAuth2 oauth2Config;

    private final OAuth2SpringAuthorizationServer authorizationServerConfig;

    public OAuth2SpringAuthPredicate(final ConfigSecurity configuration) {
        super(configuration);
        // 从 Spring 容器获取 OAuth2 配置
        this.oauth2Config = SpringUtil.getBean(ConfigOAuth2.class);
        this.authorizationServerConfig = SpringUtil.getBean(OAuth2SpringAuthorizationServer.class);
    }

    @Override
    public void configure(final HttpSecurity security, final Object attached) {

        // 注册 OAuth2 的 Token 配置（Opaque Token）
        TokenBuilderManager.of().registry(TypeToken.OPAQUE, OAuth2TokenBuilder::new);
        TokenBuilderManager.of().registry(TypeToken.OPAQUE_REFRESH, OAuth2TokenBuilderRefresh::new);

        try {

            log.info("[ R2MO ] 认证授权服务器 / OAuth2 AuthorizationServer...");

            log.info("[ R2MO ] ---> OAuth2 Token 模式 = `{}`", this.oauth2Config.getMode());
            // 配置 Authorization Server（委托）
            this.authorizationServerConfig.configureAuthorizationServer(security);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        log.info("[ R2MO ] ( Auth ) OAuth2认证授权服务器 配置完成！");
    }
}
