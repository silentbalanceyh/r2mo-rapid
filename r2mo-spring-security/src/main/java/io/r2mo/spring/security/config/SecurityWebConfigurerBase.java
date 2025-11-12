package io.r2mo.spring.security.config;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.extension.handler.SecurityHandler;

/**
 * @author lang : 2025-11-12
 */
public abstract class SecurityWebConfigurerBase implements SecurityWebConfigurer {

    private final SecurityHandler failure;
    private final ConfigSecurity config;

    public SecurityWebConfigurerBase() {
        this.failure = SpringUtil.getBean(SecurityHandler.class);
        this.config = SpringUtil.getBean(ConfigSecurity.class);
    }

    protected SecurityHandler handler() {
        return this.failure;
    }

    protected ConfigSecurity config() {
        return this.config;
    }
}
