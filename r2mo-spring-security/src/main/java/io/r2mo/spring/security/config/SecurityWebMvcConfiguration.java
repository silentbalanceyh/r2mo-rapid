package io.r2mo.spring.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author lang : 2025-11-11
 */
@Configuration
@Slf4j
public class SecurityWebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private SecurityScopeResolver scopeResolver;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(this.scopeResolver);
        log.info("[ R2MO ] 追加 Resolver 作为方法参数解析器, {}", this.scopeResolver.getClass().getName());
    }
}
