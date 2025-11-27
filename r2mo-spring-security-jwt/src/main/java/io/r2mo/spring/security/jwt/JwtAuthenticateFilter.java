package io.r2mo.spring.security.jwt;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.spring.security.auth.AuthUserDetail;
import io.r2mo.spring.security.extension.AuthSwitcher;
import io.r2mo.spring.security.jwt.token.JwtTokenGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * JWT 认证过滤器
 * 从请求头中提取 JWT Token，验证其有效性，并设置 SecurityContext。
 *
 * @author lang : 2025-11-12
 */
@Slf4j
@Component
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserCache userCache;

    public JwtAuthenticateFilter(final JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.userCache = UserCache.of();
    }

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {

        final AuthSwitcher authSwitcher = AuthSwitcher.of();

        // 0. 检查是否应该跳过（OAuth2 JWT 模式启用时）
        if (authSwitcher.hasJwt()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 从请求头获取 JWT Token
        final String token = this.parseJwt(request);
        // 如果 token 为空，直接放行
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 验证 Token 是否有效
        if (!this.jwtTokenGenerator.tokenValidate(token)) {
            // Token 无效，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 从 Token 获取用户 ID (loginId)
        final String loginIdStr = this.jwtTokenGenerator.tokenSubject(token);
        // 如果 sub 为空，直接放行
        if (!StringUtils.hasText(loginIdStr)) {
            filterChain.doFilter(request, response);
            return;
        }

        final UUID userId;
        try {
            // 尝试将 loginIdStr 解析为 UUID
            userId = UUID.fromString(loginIdStr);
        } catch (final IllegalArgumentException e) {
            // loginIdStr 不是有效的 UUID 格式，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 从 UserCache 获取 UserAt
        final UserAt userAt = this.userCache.find(userId);
        // 如果 UserAt 不存在，直接放行
        if (userAt == null) {
            // 可选：记录日志，Token 有效但用户信息不存在
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Token 有效，用户信息也存在，设置 SecurityContext
        final AuthUserDetail userDetails = new AuthUserDetail(userAt); // 从 UserAt 构建 MSUserDetail
        final UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 6. 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中解析 JWT Token
     *
     * @param request HTTP 请求
     *
     * @return JWT Token 字符串，如果不存在则返回 null
     */
    private String parseJwt(final HttpServletRequest request) {
        final String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // 移除 "Bearer " 前缀
        }

        return null;
    }
}