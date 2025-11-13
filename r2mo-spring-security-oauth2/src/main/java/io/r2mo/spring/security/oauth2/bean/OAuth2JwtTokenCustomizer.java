package io.r2mo.spring.security.oauth2.bean;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.AuthUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OAuth2 JWT Token 定制器
 * 从 UserDetails 和 MSUser 中提取自定义字段添加到 JWT Claims
 *
 * @author lang : 2025-11-13
 */
@Component
@Slf4j
public class OAuth2JwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(final JwtEncodingContext context) {
        // 获取认证主体
        final Authentication authentication = context.getPrincipal();
        if (authentication == null) {
            return;
        }

        final Object principal = authentication.getPrincipal();

        // 从 AuthUserDetail 中提取 UserAt 信息
        if (principal instanceof AuthUserDetail) {
            final AuthUserDetail userDetail = (AuthUserDetail) principal;
            final UserAt userAt = userDetail.getUser();

            if (userAt != null && userAt.logged() != null) {
                final MSUser user = userAt.logged();

                // 添加用户基本信息
                this.addUserClaims(context, user);

                // 添加用户 Token 数据（MSUser.token()）
                this.addTokenClaims(context, user);

                log.debug("[ R2MO ] JWT Token 定制完成：user = {}, claims = {}",
                    user.getUsername(),
                    context.getClaims().build().getClaims().keySet());
            }
        }
        // 从标准 UserDetails 中提取信息
        else if (principal instanceof UserDetails) {
            final UserDetails userDetails = (UserDetails) principal;

            // 添加基本用户名
            context.getClaims().claim("username", userDetails.getUsername());

            // 添加权限信息
            if (userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty()) {
                context.getClaims().claim("authorities",
                    userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .toList()
                );
            }

            log.debug("[ R2MO ] JWT Token 定制完成（标准 UserDetails）：username = {}",
                userDetails.getUsername());
        }
    }

    /**
     * 添加用户基本信息到 Claims
     */
    private void addUserClaims(final JwtEncodingContext context, final MSUser user) {
        // 用户 ID
        if (user.getId() != null) {
            context.getClaims().claim("user_id", user.getId());
        }

        // 用户名
        if (user.getUsername() != null) {
            context.getClaims().claim("username", user.getUsername());
            // OIDC 标准 preferred_username
            context.getClaims().claim("preferred_username", user.getUsername());
        }

        // 昵称
        if (user.getNickname() != null) {
            context.getClaims().claim("nickname", user.getNickname());
            // OIDC 标准 name
            context.getClaims().claim("name", user.getNickname());
        }

        // 邮箱
        if (user.getEmail() != null) {
            context.getClaims().claim("email", user.getEmail());
            context.getClaims().claim("email_verified", true);
        }

        // 手机号
        if (user.getMobile() != null) {
            context.getClaims().claim("phone_number", user.getMobile());
            context.getClaims().claim("phone_number_verified", true);
        }

        // 头像
        if (user.getAvator() != null) {
            context.getClaims().claim("picture", user.getAvator());
        }

        // 用户类型
        if (user.getType() != null) {
            context.getClaims().claim("user_type", user.getType().getSimpleName());
        }
    }

    /**
     * 添加 MSUser.token() 中的自定义数据到 Claims
     */
    private void addTokenClaims(final JwtEncodingContext context, final MSUser user) {
        final Map<String, Object> tokenData = user.token();
        if (tokenData != null && !tokenData.isEmpty()) {
            tokenData.forEach((key, value) -> {
                if (value != null && !"id".equals(key)) {  // id 已经作为 sub，不重复添加
                    context.getClaims().claim(key, value);
                }
            });
        }
    }
}

