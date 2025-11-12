package io.r2mo.spring.security.jwt;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author lang : 2025-11-12
 */
public class JwtSpringAuthenticator extends SpringAuthenticatorBase {

    public JwtSpringAuthenticator(final ConfigSecurity configuration) {
        super(configuration);
    }


    @Override
    public void configure(final HttpSecurity security, final Object attached) {
        
    }
}
