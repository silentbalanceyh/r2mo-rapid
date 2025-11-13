package io.r2mo.spring.security.extension;

/**
 * 认证方式选择器
 * <pre>
 *     1. Basic / JWT / OAuth2 是可共存的认证方式
 *     2. 但是 JWT 和 OAuth2 中的 Bearer 认证方式只能二选一
 * </pre>
 *
 * @author lang : 2025-11-13
 */
public interface AuthSwitcher {
    boolean hasJwt();
}
