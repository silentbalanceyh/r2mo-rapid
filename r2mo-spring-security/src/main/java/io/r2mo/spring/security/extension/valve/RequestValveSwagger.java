package io.r2mo.spring.security.extension.valve;

import io.r2mo.spring.security.config.ConfigSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author lang : 2025-11-11
 */
@Slf4j
public class RequestValveSwagger implements RequestValve {

    @Override
    public void execute(final AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry registry,
                        final ConfigSecurity config, final Object attached) {
        log.info("[ R2MO ] Swagger 规则处理。");
        registry.requestMatchers(
            // Swagger 专用
            AntPathRequestMatcher.antMatcher("/doc.html"),              // 文档首页
            AntPathRequestMatcher.antMatcher("/v3/api-docs/**"),        // API 文档
            AntPathRequestMatcher.antMatcher("/swagger-ui/**"),         // Swagger UI 静态
            AntPathRequestMatcher.antMatcher("/swagger-resources/**")   // Swagger 资源
        ).permitAll();
    }
}
