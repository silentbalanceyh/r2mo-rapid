package io.r2mo.spring.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * @author lang : 2025-11-12
 */
public interface SecurityWebConfigurer {

    void configure(HttpSecurity security, final HandlerMappingIntrospector introspector);
}
