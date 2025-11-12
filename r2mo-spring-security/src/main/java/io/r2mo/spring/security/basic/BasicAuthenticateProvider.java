package io.r2mo.spring.security.basic;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.auth.UserDetailsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义 Basic 认证提供者
 * 通过注入 AuthService 来处理认证逻辑
 *
 * @author lang : 2025-11-11
 */
@Slf4j
@Component
public class BasicAuthenticateProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userService;

    @Autowired
    public BasicAuthenticateProvider(final UserDetailsService authService) {
        this.userService = authService;
        this.passwordEncoder = SpringUtil.getBean(PasswordEncoder.class);
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        // -- 关键：设置认证策略到上下文
        UserDetailsContext.setStrategy(BasicLoginRequest.TYPE);

        try {
            final UserDetails stored = this.userService.loadUserByUsername(username);

            // 密码校验
            if (!this.passwordEncoder.matches(password, stored.getPassword())) {
                log.error("[ R2MO ] 用户 `{}` 密码校验失败 | {} : {}", username, password, stored.getPassword());
                throw new BadCredentialsException("[ R2MO ] 用户名或密码错误");
            }

            // 构造认证成功的 Authentication 对象
            return new UsernamePasswordAuthenticationToken(
                stored.getUsername(),
                null,
                stored.getAuthorities()
            );
        } finally {
            // -- 关键：清空认证策略
            UserDetailsContext.clearStrategy();
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}