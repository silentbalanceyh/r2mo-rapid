package io.r2mo.spring.security.auth.basic;

import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.spring.security.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

/**
 * 自定义 Basic 认证提供者
 * 通过注入 AuthService 来处理认证逻辑
 *
 * @author lang : 2025-11-11
 */
@Component
public class BasicAuthenticateProvider implements AuthenticationProvider {

    private final AuthService authService;

    @Autowired
    public BasicAuthenticateProvider(final AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        // 构造 LoginRequest
        final BasicLoginRequest loginRequest = new BasicLoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        // 从缓存中加载认证信息

        try {
            // 调用 AuthService 进行认证
            final LoginResponse loginResponse = this.authService.login(loginRequest);

            // 构造认证成功的 Authentication 对象
            return new UsernamePasswordAuthenticationToken(
                loginResponse.getUsername(),
                password,
                AuthorityUtils.createAuthorityList("ROLE_USER")
            );
        } catch (final Exception e) {
            throw new BadCredentialsException("认证失败: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}