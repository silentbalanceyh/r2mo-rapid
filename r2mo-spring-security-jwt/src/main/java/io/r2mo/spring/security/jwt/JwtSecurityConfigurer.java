package io.r2mo.spring.security.jwt;

import io.r2mo.spring.security.config.SecurityWebConfigurerBase;
import io.r2mo.spring.security.extension.SpringAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * SPI 插件化集成：纯 JWT 认证配置
 *
 * @author lang : 2025-11-12
 */
@Slf4j
public class JwtSecurityConfigurer extends SecurityWebConfigurerBase {
    public JwtSecurityConfigurer() {
        super();
    }

    @Override
    public void configure(final HttpSecurity http, final HandlerMappingIntrospector introspector) {
        if (!this.config().isJwt()) {
            log.warn("[ R2MO ] (W) 未启用 JWT 功能，请检查 application.yml 配置！");
            /*
             * 此处为了防止混用，在不启用 Jwt 的场景模式下，应该直接禁用掉此配置，return，而不应该触发
             * JwtAuthenticateFilter 的配置流程，这样才可以避免不必要的混乱。
             */
            return;
        }
        // 添加 JWT 认证过滤器
        final SpringAuthenticator authenticator = SpringAuthenticator.of(this.config(), JwtSpringAuthenticator::new);
        authenticator.configure(http, this.handler());
        log.info("[ R2MO ] 启用 JWT 认证器");
    }
}
