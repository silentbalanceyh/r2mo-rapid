package io.r2mo.spring.security.extension;

import io.r2mo.spring.security.config.ConfigSecurity;

/**
 * @author lang : 2025-11-11
 */
public abstract class SpringAuthenticatorBase implements SpringAuthenticator {
    private final ConfigSecurity configuration;

    protected SpringAuthenticatorBase(final ConfigSecurity configuration) {
        this.configuration = configuration;
    }

    protected ConfigSecurity config() {
        return configuration;
    }
}
