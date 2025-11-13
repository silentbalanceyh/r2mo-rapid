package io.r2mo.spring.security.oauth2;

import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * OAuth2 与 JWT 共存标记 Bean
 *
 * 当 OAuth2 启用且模式为 JWT 时，此 Bean 存在，
 * 用于让旧的 JwtAuthenticateFilter 条件化禁用
 *
 * @author lang : 2025-11-13
 */
@Slf4j
@Component
@ConditionalOnBean(ConfigSecurityOAuth2.class)
public class OAuth2JwtCoexistenceMarker {

    private final ConfigSecurityOAuth2 config;

    public OAuth2JwtCoexistenceMarker(final ConfigSecurityOAuth2 config) {
        this.config = config;
        if (this.shouldDisableLegacyJwt()) {
            log.warn("[ R2MO ] OAuth2 已启用 JWT 模式，旧的 JWT Filter 将被禁用");
        }
    }

    /**
     * 是否应该禁用旧的 JWT 认证
     */
    public boolean shouldDisableLegacyJwt() {
        return this.config != null
            && this.config.isOn()
            && (this.config.isJwt() || this.config.isOidc());
    }
}

