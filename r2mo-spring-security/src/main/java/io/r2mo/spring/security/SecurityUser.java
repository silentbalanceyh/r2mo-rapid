package io.r2mo.spring.security;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.jaas.session.UserSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * 安全上下文
 *
 * @author lang : 2025-11-12
 */
public class SecurityUser {

    public static UserAt logged() {
        // 通过 SecurityContextHolder 获取
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            return null;
        }
        final String username = authentication.getName();
        return UserSession.of().find(username);
    }

    public static UserContext context() {
        final UserAt userAt = logged();
        if (Objects.isNull(userAt)) {
            return null;
        }
        return UserSession.of().context(userAt.id());
    }

    public static String id() {
        return id(false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T id(final boolean isUuid) {
        final UserAt userAt = logged();
        if (Objects.isNull(userAt)) {
            return null;
        }
        if (isUuid) {
            return (T) userAt.id();
        } else {
            return (T) userAt.id().toString();
        }
    }
}
