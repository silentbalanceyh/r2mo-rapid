package io.r2mo.spring.security.oauth2.token;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.token.TokenBuilder;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.util.Objects;

/**
 * OAuth2 Token Refresher
 *
 * 用于获取 OAuth2 Refresh Token
 *
 * @author lang : 2025-11-13
 */
@Slf4j
public class OAuth2TokenBuilderRefresh implements TokenBuilder {

    private final OAuth2AuthorizationService authorizationService;

    public OAuth2TokenBuilderRefresh() {
        this.authorizationService = SpringUtil.getBean(OAuth2AuthorizationService.class);
    }

    @Override
    public String build(final UserAt userAt) {
        if (Objects.isNull(userAt) || Objects.isNull(userAt.logged())) {
            throw new _401UnauthorizedException("[ R2MO ] 无法获取 Refresh Token，用户信息缺失");
        }

        final String principal = userAt.logged().getUsername();

        try {
            // 查找该用户的最新授权
            final OAuth2Authorization authorization = this.authorizationService.findByToken(
                principal,
                OAuth2TokenType.REFRESH_TOKEN
            );

            if (authorization != null && authorization.getRefreshToken() != null) {
                return authorization.getRefreshToken().getToken().getTokenValue();
            }

            log.warn("[ R2MO ] 未找到用户 {} 的 Refresh Token", principal);
            throw new _401UnauthorizedException("[ R2MO ] 未找到有效的 Refresh Token");

        } catch (final Exception ex) {
            log.error("[ R2MO ] 获取 Refresh Token 失败", ex);
            throw new _401UnauthorizedException("[ R2MO ] Refresh Token 获取失败: " + ex.getMessage());
        }
    }
}

