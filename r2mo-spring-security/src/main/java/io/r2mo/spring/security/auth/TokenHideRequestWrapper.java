package io.r2mo.spring.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * <pre>
 * 🎭 隐身请求包装器 (Stealth Request Wrapper)
 * =============================================================================
 * 这是一个基于装饰器模式（Decorator Pattern）的请求包装类，用于在过滤器链中
 * “屏蔽”特定的 HTTP 头信息（此处特指 Authorization）。
 *
 * 💡 设计初衷 (Design Rationale)
 * -----------------------------------------------------------------------------
 * 在混合认证架构（Custom AES + Native OAuth2）中，存在典型的“共享信道冲突”：
 *
 * 1. 冲突背景：
 * AuthTokenFilter (自定义) 和 BearerTokenAuthenticationFilter (原生)
 * 都监听同一个 {@code Authorization: Bearer ...} 请求头。
 *
 * 2. 致命问题：
 * 即使 AuthTokenFilter 成功认证了 AES Token 并设置了 SecurityContext，
 * 请求依然会流向下游的原生 OAuth2 过滤器。
 * 原生过滤器看到 Bearer 头，会强行尝试将其解析为 JWT。
 *
 * 3. 后果：
 * 由于 AES Token 不是标准的 JWT 格式，原生过滤器会抛出
 * {@code MalformedJwtException} 或 {@code InvalidTokenException}，
 * 导致最终响应变成 401，覆盖了我们之前辛苦建立的成功认证状态。
 *
 * ⚙️ 核心机制 (Mechanism)
 * -----------------------------------------------------------------------------
 * 本包装器继承自 {@link jakarta.servlet.http.HttpServletRequestWrapper}，
 * 实施了 "Consumed & Erased" (消费即擦除) 策略：
 *
 * 👉 拦截 (Intercept):
 * 重写 {@code getHeader}, {@code getHeaders}, {@code getHeaderNames} 方法。
 *
 * 👉 隐身 (Masking):
 * 当下游调用 {@code getHeader("Authorization")} 时，直接返回 {@code null}。
 * 这就好比告诉下游过滤器：“这个请求没有携带令牌，你可以跳过了。”
 *
 * 👉 放行 (Delegate):
 * 对于除 Authorization 以外的所有 Header，原样委托给原始 Request 处理。
 *
 * 🚀 使用场景 (Scenarios)
 * -----------------------------------------------------------------------------
 * ✅ 仅用于 {@link TokenAuthFilter} 成功认证之后。
 * 通过 {@code chain.doFilter(new HideTokenRequestWrapper(request), response)}
 * 将“净化”过的请求传给下游，确保认证结果的安全落地，防止被原生组件误杀。
 * </pre>
 *
 * @author lang : 2026-01-07
 */
class TokenHideRequestWrapper extends HttpServletRequestWrapper {

    TokenHideRequestWrapper(final HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(final String name) {
        // 如果下游试图获取 Authorization，告诉它没有
        if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
            return null;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(final String name) {
        if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
            return Collections.emptyEnumeration();
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        final List<String> names = Collections.list(super.getHeaderNames());
        // 从名字列表中移除 Authorization
        names.removeIf(HttpHeaders.AUTHORIZATION::equalsIgnoreCase);
        return Collections.enumeration(names);
    }
}
