package io.r2mo.spring.security.jwt;

import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import io.r2mo.spring.security.jwt.token.JwtTokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author lang : 2025-11-12
 */
@Slf4j
public class JwtSpringAuthenticator extends SpringAuthenticatorBase {

    public JwtSpringAuthenticator(final ConfigSecurity configuration) {
        super(configuration);
    }


    @Override
    public void configure(final HttpSecurity security, final Object attached) {
        // security.addFilterBefore(this.filter, UsernamePasswordAuthenticationFilter.class);

        // 注册 Jwt 的 Token 配置
        TokenBuilderManager.of().registry(TokenType.JWT, JwtTokenBuilder::new);

        log.info("[ R2MO ] ( Auth ) JWT 认证器配置完成！");
    }
}
