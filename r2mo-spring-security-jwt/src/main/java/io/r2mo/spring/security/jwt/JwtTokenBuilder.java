package io.r2mo.spring.security.jwt;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.token.TokenBuilder;

/**
 * @author lang : 2025-11-12
 */
public class JwtTokenBuilder implements TokenBuilder {
    @Override
    public String build(final UserAt userAt) {
        return "";
    }
}
