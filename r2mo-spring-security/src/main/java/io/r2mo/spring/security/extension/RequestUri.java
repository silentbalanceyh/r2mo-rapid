package io.r2mo.spring.security.extension;

import io.r2mo.spring.security.config.ConfigSecurity;

import java.util.Set;

/**
 * SPI（注册专用）
 *
 * @author lang : 2025-11-11
 */
public interface RequestUri {
    /**
     * 忽略的 URI 列表，不做安全认证的白名单！
     *
     * @param security 配置信息
     *
     * @return 忽略的 URI 列表
     */
    default Set<String> ignores(final ConfigSecurity security) {
        return Set.of();
    }

    /**
     * 不缓存的 URI 列表，通常用于不需要缓存的静态资源
     *
     * @param security 配置信息
     *
     * @return 不缓存的 URI 列表
     */
    default Set<String> noCache(final ConfigSecurity security) {
        return Set.of();
    }

    /**
     * 不重定向的 URI 列表，通常用于不需要重定向的 URI
     *
     * @param security 配置信息
     *
     * @return 不重定向的 URI 列表
     */
    default Set<String> noRedirect(final ConfigSecurity security) {
        return Set.of();
    }
}
