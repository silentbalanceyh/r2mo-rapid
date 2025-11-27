package io.r2mo.spring.security.oauth2;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import io.r2mo.spring.security.oauth2.config.ConfigOAuth2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * OAuth2 Spring Authenticator
 * 负责 OAuth2 SecurityFilterChain 的完整配置：
 * - 创建 OAuth2 SecurityFilterChain Bean
 * - 配置 Authorization Server（OIDC、SPI Provider/Converter）
 * - 配置 Resource Server（JWT 验证）
 * - 异常处理
 * - 注册 Token Builder
 *
 * @author lang : 2025-11-13
 */
@Slf4j
public class OAuth2SpringAuthResource extends SpringAuthenticatorBase {

    private final ConfigOAuth2 oauth2Config;

    public OAuth2SpringAuthResource(final ConfigSecurity configuration) {
        super(configuration);
        // 从 Spring 容器获取 OAuth2 配置
        this.oauth2Config = SpringUtil.getBean(ConfigOAuth2.class);
    }

    @Override
    public void configure(final HttpSecurity security, final Object attached) {
        try {
            log.info("[ R2MO ] 资源服务器    / OAuth2 ResourceServer...");
            if (this.oauth2Config.isJwt() || this.oauth2Config.isOidc()) {
                security.oauth2ResourceServer(resource ->
                    resource.jwt(Customizer.withDefaults())
                );
            }
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        log.info("[ R2MO ] ( Auth ) OAuth2资源服务器 配置完成！");
    }
}
