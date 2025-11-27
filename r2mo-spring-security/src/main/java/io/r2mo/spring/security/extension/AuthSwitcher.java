package io.r2mo.spring.security.extension;

import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;

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

    Cc<String, AuthSwitcher> CC_SWITCHER = Cc.openThread();

    static AuthSwitcher of() {
        return CC_SWITCHER.pick(() -> SPI.findOneOf(AuthSwitcher.class));
    }

    boolean hasJwt();

    boolean hasOAuth2();
}
