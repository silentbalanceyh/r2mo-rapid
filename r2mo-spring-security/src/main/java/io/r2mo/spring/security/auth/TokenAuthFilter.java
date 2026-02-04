package io.r2mo.spring.security.auth;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.token.TokenBuilder;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.spring.security.extension.AuthSwitcher;
import io.r2mo.spring.security.extension.handler.SecurityEntryPoint;
import io.r2mo.spring.security.token.AESTokenGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class TokenAuthFilter extends OncePerRequestFilter {
    private static final TokenBuilderManager MANAGER = TokenBuilderManager.of();
    private final UserCache userCache;
    private final SecurityEntryPoint entryPoint;
    private final AuthSwitcher switcher;

    public TokenAuthFilter() {
        this.userCache = UserCache.of();
        this.entryPoint = SpringUtil.getBean(SecurityEntryPoint.class);
        this.switcher = AuthSwitcher.of();
    }


    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {
        // 从请求头获取 Bearer Token
        if (this.isSkipped(request)) {
            // 🔸 Basic 认证 -> 放行，匿名访问也放行
            filterChain.doFilter(request, response);
            return;
        }
        final String token = this.parseToken(request);
        if (R2MO.isInvalid(token)) {
            // 🔸 无 Token -> 放行 (可能是匿名访问或 Basic)
            final AuthenticationException failed = new BadCredentialsException("[ R2MO ] Token 丢失，请提供 Token 信息！！");
            this.entryPoint.commence(request, response, failed);
            return;
        }

        // 提取 TokenType (核心嗅探逻辑)
        final TokenType tokenType = this.parseToken(token);
        if (Objects.isNull(tokenType)) {
            // 🔸 无法识别的 Token 类型，直接放行
            final AuthenticationException failed = new BadCredentialsException("[ R2MO ] Token 类型无法解析！！");
            this.entryPoint.commence(request, response, failed);
            return;
        }

        if (!MANAGER.isSupport(tokenType)) {
            // 🔸 检测到不支持的 Token 类型，直接放行
            final AuthenticationException failed = new BadCredentialsException("[ R2MO ] 不支持的认证 Token 类型 = " + tokenType);
            this.entryPoint.commence(request, response, failed);
            return;
        }

        // ----------------- 此处进行强化分流
        /*
         * 此处的矩阵如下：
         * - OAuth2 目前只支持 JWT 和 OPAQUE 两种类型，而这两种类型中 JWT 和 纯JWT实际是共享了 TokenBuilder，所以不冲突
         * - BASIC 模式支持 AES
         * - JWT 强制支持 JWT
         * 简单说就是如果无法做相关解析的时候就判断 OAuth2 并且递交，这样 OAuth2 实际就降级了，整体是共存的
         * TokenFilter 中的 JWT 的解析对 OAuth2 而言也生效，二者共享 Token 解析，但实际后续流程不一样
         */
        final TokenBuilder builder = MANAGER.getOrCreate(tokenType);
        final String userIdStr = builder.accessOf(token).get();
        if (!StringUtils.hasText(userIdStr)) {
            log.error("[ R2MO ] Token 有效但未能提取到用户 ID，type = {} / token = {}",
                tokenType, token);
            if (this.waitOAuth2(tokenType)) {
                // 🔸 OAuth2 模式下，继续处理
                filterChain.doFilter(request, response);
                return;
            }
            // 🔸 如果 sub 为空，直接放行
            final AuthenticationException failed = new BadCredentialsException("[ R2MO ] Token 数据不合法！！");
            this.entryPoint.commence(request, response, failed);
            return;
        }

        final UUID userId;
        try {
            // 尝试将 userIdStr 解析为 UUID
            userId = UUID.fromString(userIdStr);
        } catch (final IllegalArgumentException e) {
            // 🔸 userIdStr 不是有效的 UUID 格式，直接放行
            final AuthenticationException failed = new BadCredentialsException("[ R2MO ] 用户 ID 格式错误：" + userIdStr);
            this.entryPoint.commence(request, response, failed);
            return;
        }

        // 从 UserCache 获取 UserAt
        final UserAt userAt = this.userCache.find(userId).get();
        // 如果 UserAt 不存在，直接放行
        if (userAt == null || !userAt.isOk()) {
            // 🔸 可选：记录日志，Token 有效但用户信息不存在
            final AuthenticationException failed = new BadCredentialsException("[ R2MO ] 用户信息不存在，ID：" + userId);
            this.entryPoint.commence(request, response, failed);
            return;
        }


        // ✅️ Token 有效，用户信息也存在，设置 SecurityContext
        final UserAuthDetails userDetails = new UserAuthDetails(userAt); // 从 UserAt 构建 MSUserDetail
        final UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 继续执行过滤器链
        filterChain.doFilter(new TokenHideRequestWrapper(request), response);
    }

    private boolean waitOAuth2(final TokenType tokenType) {
        if (TokenType.AES == tokenType || TokenType.BASIC == tokenType) {
            return false;
        }
        return Objects.nonNull(this.switcher) && this.switcher.hasJwt();
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

    private boolean isSkipped(final HttpServletRequest request) {
        final String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(headerAuth)) {
            return true;
        }
        return StringUtils.hasText(headerAuth) && headerAuth.startsWith("Basic ");
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
