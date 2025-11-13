package io.r2mo.spring.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 将 SecurityFilterChain 的 @Bean 放在独立类中，以便自动装配 HttpSecurity
 */
@Configuration
@EnableWebSecurity
public class OAuth2SecurityFilterChainConfig {

    @Autowired(required = false)
    private OAuth2SpringAuthenticator authenticator;

    @Bean
    @Order(1)
    public SecurityFilterChain oauth2SecurityFilterChain(final HttpSecurity http) throws Exception {
        if (this.authenticator == null) {
            return null;
        }

        // delegate configuration to authenticator
        this.authenticator.configureSecurityFilterChain(http);

        return http.build();
    }
}
