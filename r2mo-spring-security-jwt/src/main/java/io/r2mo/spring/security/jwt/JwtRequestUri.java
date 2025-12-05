package io.r2mo.spring.security.jwt;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestUri;

import java.util.Set;

/**
 * @author lang : 2025-11-12
 */
public class JwtRequestUri implements RequestUri {
    @Override
    public Set<String> ignores(final ConfigSecurity security) {
        return Set.of(
            "/auth/login-jwt"
        );
    }
}
