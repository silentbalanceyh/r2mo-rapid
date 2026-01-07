package io.r2mo.spring.security.basic;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.session.UserSession;
import io.r2mo.spring.security.auth.UserAuthContext;
import io.r2mo.spring.security.auth.UserAuthDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        UserAuthContext.setStrategy(BasicLoginRequest.TYPE);

        try {
            final UserDetails stored = this.userService.loadUserByUsername(username);

            // 密码校验
            if (!this.passwordEncoder.matches(password, stored.getPassword())) {
                log.error("[ R2MO ] 用户 `{}` 密码校验失败 | {} : {}", username, password, stored.getPassword());
                throw new BadCredentialsException("[ R2MO ] 用户名或密码错误");
            }

            // 🟢 2. 核心修改：直接从 HTTP 请求头判断是否为 Basic 请求
            // 这种方式最纯粹，不依赖上下文策略，只看 HTTP 协议本身
            final boolean isBasicAuth = this.isBasicAuthRequest();

            // 🟢 3. 只有【不是】Basic 请求时，才写入 UserSession
            // Form 登录 (无 Basic 头) -> 写入缓存
            // API 登录 (有 Basic 头) -> 跳过缓存
            // 此处成功了才处理缓存信息，追加新逻辑，但凡认证失败就不会去触碰会话数据相关信息
            if (!isBasicAuth) {
                if (stored instanceof final UserAuthDetails verified) {
                    UserSession.of().userAt(verified.getUser());
                }
            }


            // 构造认证成功的 Authentication 对象
            return new UsernamePasswordAuthenticationToken(
                stored.getUsername(),
                null,
                stored.getAuthorities()
            );
        } finally {
            // -- 关键：清空认证策略
            UserAuthContext.clearStrategy();
        }
    }

    /**
     * 辅助方法：判断当前请求是否携带了 Basic Auth 头
     */
    private boolean isBasicAuthRequest() {
        try {
            // 获取当前请求属性
            final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return false;
            }

            final HttpServletRequest request = attributes.getRequest();
            final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

            // 判断 Header 是否以 "Basic " 开头 (忽略大小写)
            return StringUtils.startsWithIgnoreCase(header, "Basic ");
        } catch (final Exception e) {
            // 防御性编码，如果不在 Web 环境下运行，视为 false
            return false;
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}