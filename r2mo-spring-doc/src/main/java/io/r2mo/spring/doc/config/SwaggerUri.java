package io.r2mo.spring.doc.config;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestUri;

import java.util.Set;

public class SwaggerUri implements RequestUri {
    @Override
    public Set<String> ignores(final ConfigSecurity security) {
        return Set.of(
            "/swagger-ui.html:GET",
            "/swagger-ui/**:GET",
            "/v3/api-docs:GET",
            "/v3/api-docs/**:GET"
        );
    }
}
