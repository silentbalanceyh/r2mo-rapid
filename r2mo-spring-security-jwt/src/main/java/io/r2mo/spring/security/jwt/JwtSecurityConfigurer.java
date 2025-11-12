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
        if (this.config().isJwt()) {
            // 添加 JWT 认证过滤器
            final SpringAuthenticator authenticator = SpringAuthenticator.of(this.config(), JwtSpringAuthenticator::new);
            authenticator.configure(http, this.handler());
            log.info("[ R2MO ] 启用 JWT 认证器");
        } else {
            log.warn("[ R2MO ] (W) 未启用 JWT 功能，请检查 application.yml 配置！");
        }
    }
}
