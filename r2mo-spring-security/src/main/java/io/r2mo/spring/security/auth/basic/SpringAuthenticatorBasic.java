package io.r2mo.spring.security.auth.basic;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author lang : 2025-11-11
 */
public class SpringAuthenticatorBasic extends SpringAuthenticatorBase {
    public SpringAuthenticatorBasic(final ConfigSecurity configuration) {
        super(configuration);
    }

    @Override
    public void configure(final HttpSecurity security, final Object attached) {

    }
}
