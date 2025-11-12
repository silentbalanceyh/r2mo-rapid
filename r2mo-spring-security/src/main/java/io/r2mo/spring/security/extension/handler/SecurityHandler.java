package io.r2mo.spring.security.extension.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * @author lang : 2025-11-12
 */
@EnableWebSecurity
@RequiredArgsConstructor
@Component
public class SecurityHandler {

    private final AccessDeniedHandler deniedHandler;
    private final AuthenticationEntryPoint entryPoint;

    public Customizer<ExceptionHandlingConfigurer<HttpSecurity>> handler() {
        return (config) -> {
            config.accessDeniedHandler(this.deniedHandler);
            config.authenticationEntryPoint(this.entryPoint);
        };
    }

    public AuthenticationEntryPoint handlerAuthentication() {
        return this.entryPoint;
    }

    public AccessDeniedHandler handlerAuthorization() {
        return this.deniedHandler;
    }
}
