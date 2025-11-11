package io.r2mo.spring.security.extension;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.config.ConfigSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author lang : 2025-11-11
 */
public abstract class SpringAuthenticatorBase implements SpringAuthenticator {
    private final ConfigSecurity configuration;
    private final UserDetailsService userService;

    protected SpringAuthenticatorBase(final ConfigSecurity configuration) {
        this.configuration = configuration;
        this.userService = SpringUtil.getBean(UserDetailsService.class);
    }

    protected ConfigSecurity config() {
        return this.configuration;
    }

    protected UserDetailsService userService() {
        return this.userService;
    }
}
