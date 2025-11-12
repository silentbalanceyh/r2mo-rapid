package io.r2mo.spring.security.jwt;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestSkip;

import java.util.Set;

/**
 * @author lang : 2025-11-12
 */
public class RequestSkipJwt implements RequestSkip {
    @Override
    public Set<String> openApi(final ConfigSecurity security) {
        return Set.of(
            "/jwt/captcha",
            "/jwt/login"
        );
    }
}
