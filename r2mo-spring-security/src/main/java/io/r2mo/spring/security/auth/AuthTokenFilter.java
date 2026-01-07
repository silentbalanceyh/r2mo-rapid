package io.r2mo.spring.security.auth;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.token.TokenBuilder;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.spring.security.extension.AuthSwitcher;
import io.r2mo.spring.security.token.AESTokenGenerator;
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

@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final TokenBuilderManager MANAGER = TokenBuilderManager.of();
    private final UserCache userCache;
    private final AuthSwitcher switcher;

    public AuthTokenFilter() {
        this.userCache = UserCache.of();
        this.switcher = AuthSwitcher.of();
    }


    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {
        if (this.switcher.hasJwt()) {
            // 🔸 优先级更高的 OAuth 2 上线
            filterChain.doFilter(request, response);
            return;
        }


        // 从请求头获取 Bearer Token
        final String token = this.parseToken(request);
        if (!StringUtils.hasText(token)) {
            // 🔸 如果 token 为空，直接放行，不执行当前 Filter 的解析，BASIC 有做它的地方此处直接放行
            filterChain.doFilter(request, response);
            return;
        }


        // 提取 TokenType (核心嗅探逻辑)
        final TokenType tokenType = this.parseToken(token);
        if (tokenType == null) {
            // 🔸 无法识别的 Token 类型，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        if (!MANAGER.isSupport(tokenType)) {
            // 🔸 检测到不支持的 Token 类型，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        final TokenBuilder builder = MANAGER.getOrCreate(tokenType);
        final String userIdStr = builder.accessOf(token);
        if (!StringUtils.hasText(userIdStr)) {
            // 🔸 如果 sub 为空，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        final UUID userId;
        try {
            // 尝试将 userIdStr 解析为 UUID
            userId = UUID.fromString(userIdStr);
        } catch (final IllegalArgumentException e) {
            // 🔸 userIdStr 不是有效的 UUID 格式，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 从 UserCache 获取 UserAt
        final UserAt userAt = this.userCache.find(userId);
        // 如果 UserAt 不存在，直接放行
        if (userAt == null) {
            // 🔸 可选：记录日志，Token 有效但用户信息不存在
            filterChain.doFilter(request, response);
            return;
        }


        // ✅️ Token 有效，用户信息也存在，设置 SecurityContext
        final AuthUserDetail userDetails = new AuthUserDetail(userAt); // 从 UserAt 构建 MSUserDetail
        final UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 核心嗅探方法
     */
    private TokenType parseToken(final String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }

        // 1. 优先判断 AES (最快，O(1) 前缀匹配)
        if (token.startsWith(AESTokenGenerator.TOKEN_PREFIX)) {
            return TokenType.AES;
        }

        // 2. 其次判断 JWT (特征最明显)
        if (this.isJwt(token)) {
            return TokenType.JWT;
        }

        // 3. 剩下的默认视为 Opaque (UUID 或 Redis Key)
        // 只要不是空，不是 AES，不是 JWT，就尝试去 UserCache 查一下
        return TokenType.OPAQUE;
    }

    /**
     * JWT 特征判断
     * 逻辑：包含 2 个点号，且大概率以 ey 开头
     */
    private boolean isJwt(final String token) {
        // 快速剪枝：JWT 长度通常较长
        if (token.length() < 20) {
            return false;
        }

        // 找第一个点
        final int firstDot = token.indexOf('.');
        if (firstDot < 0) {
            return false;
        }

        // 找第二个点
        final int secondDot = token.indexOf('.', firstDot + 1);
        if (secondDot < 0) {
            return false;
        }

        // 找第三个点 (JWT 只有两个点，不能有第三个)
        final int thirdDot = token.indexOf('.', secondDot + 1);

        return thirdDot == -1;
    }

    /**
     * 从请求头中解析 JWT Token
     *
     * @param request HTTP 请求
     * @return JWT Token 字符串，如果不存在则返回 null
     */
    private String parseToken(final HttpServletRequest request) {
        final String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // 移除 "Bearer " 前缀
        }

        return null;
    }
}
