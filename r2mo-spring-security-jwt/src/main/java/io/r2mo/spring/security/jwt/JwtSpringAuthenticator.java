package io.r2mo.spring.security.jwt;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.enums.TypeToken;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.SpringAuthenticatorBase;
import io.r2mo.spring.security.jwt.token.JwtTokenBuilder;
import io.r2mo.spring.security.jwt.token.JwtTokenBuilderRefresh;
import io.r2mo.spring.security.token.TokenBuilderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author lang : 2025-11-12
 */
@Slf4j
public class JwtSpringAuthenticator extends SpringAuthenticatorBase {
    private final JwtAuthenticateFilter filter;

    public JwtSpringAuthenticator(final ConfigSecurity configuration) {
        super(configuration);
        this.filter = SpringUtil.getBean(JwtAuthenticateFilter.class);
    }


    @Override
    public void configure(final HttpSecurity security, final Object attached) {
        security.addFilterBefore(this.filter, UsernamePasswordAuthenticationFilter.class);

        // 注册 Jwt 的 Token 配置
        TokenBuilderManager.of().registry(TypeToken.JWT, JwtTokenBuilder::new);

        // 注册 Jwt 的 Token 刷新
        TokenBuilderManager.of().registry(TypeToken.JWT_REFRESH, JwtTokenBuilderRefresh::new);

        log.info("[ R2MO ] ( Auth ) JWT 认证器配置完成！");
    }
}
