package io.r2mo.spring.security.extension.valve;

import io.r2mo.spring.security.config.ConfigSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author lang : 2025-11-11
 */
@Slf4j
public class RequestValveStatic implements RequestValve {
    @Override
    public void execute(final AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry registry,
                        final ConfigSecurity config, final Object attached) {
        log.info("[ R2MO ] 固定规则处理。");
        registry.requestMatchers(
            AntPathRequestMatcher.antMatcher("/webjars/**"),        // 静态资源
            AntPathRequestMatcher.antMatcher("/css/**"),            // 静态资源 CSS
            AntPathRequestMatcher.antMatcher("/js/**"),             // 静态资源 JS
            AntPathRequestMatcher.antMatcher("/images/**"),         // 静态资源 图片
            AntPathRequestMatcher.antMatcher("/favicon.ico"),       // 网站图标
            AntPathRequestMatcher.antMatcher("/error/**")           // 错误页面
        ).permitAll();
    }
}
