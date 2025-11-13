package io.r2mo.spring.security.oauth2.token;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.token.TokenBuilder;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.util.Objects;

/**
 * OAuth2 Opaque Token Builder
 *
 * 用于构建 OAuth2 Opaque（不透明）Token
 * 从 OAuth2AuthorizationService 获取已生成的 Token
 *
 * @author lang : 2025-11-13
 */
@Slf4j
public class OAuth2TokenBuilder implements TokenBuilder {

    private final OAuth2AuthorizationService authorizationService;

    public OAuth2TokenBuilder() {
        this.authorizationService = SpringUtil.getBean(OAuth2AuthorizationService.class);
    }

    @Override
    public String build(final UserAt userAt) {
        final MSUser logged = userAt.logged();
        if (Objects.isNull(logged)) {
            throw new _401UnauthorizedException("[ R2MO ] 无法构造 OAuth2 Token，登录用户信息缺失");
        }

        // OAuth2 Token 通常由 Authorization Server 在认证流程中生成
        // 这里从 AuthorizationService 中查询已生成的 Token
        final String principal = logged.getUsername();

        try {
            // 查找该用户的最新授权
            // 注意：这是简化实现，实际应用中可能需要更复杂的逻辑
            final OAuth2Authorization authorization = this.authorizationService.findByToken(
                principal,
                OAuth2TokenType.ACCESS_TOKEN
            );

            if (authorization != null && authorization.getAccessToken() != null) {
                return authorization.getAccessToken().getToken().getTokenValue();
            }

            log.warn("[ R2MO ] 未找到用户 {} 的 OAuth2 Token", principal);
            throw new _401UnauthorizedException("[ R2MO ] 未找到有效的 OAuth2 Token");

        } catch (final Exception ex) {
            log.error("[ R2MO ] 构建 OAuth2 Token 失败", ex);
            throw new _401UnauthorizedException("[ R2MO ] OAuth2 Token 构建失败: " + ex.getMessage());
        }
    }
}

