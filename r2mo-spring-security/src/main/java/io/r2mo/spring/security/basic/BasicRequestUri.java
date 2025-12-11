package io.r2mo.spring.security.basic;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestUri;

import java.util.Set;

/**
 * @author lang : 2025-12-05
 */
public class BasicRequestUri implements RequestUri {
    @Override
    public Set<String> ignores(final ConfigSecurity security) {
        /*
         * 此处包含目前默认的路径
         * GET:     /auth/captcha
         * POST:    /auth/login
         */
        return Set.of("/auth/**:*");
    }

    @Override
    public Set<String> noCache(final ConfigSecurity security) {
        /*
         * 告诉核心模块，匹配此路径的请求，不使用 HttpSessionRequestCache 保存到 Session 中，这同样登录抛出的异常时，Security 就不会
         * 创建 Session，没有 Session 则不会引起下边的 OAuth2 的重定向问题，导致 Basic 登录失败之后，跳转到 /login 的 OAuth2 登录界面
         * o.s.s.w.s.HttpSessionRequestCache        : Saved request <a href="http://localhost:9002/security/auth/login?continue">...</a> to session
         * o.s.s.web.DefaultRedirectStrategy        : Redirecting to <a href="http://localhost:9002/security/login">...</a>
         * o.s.security.web.FilterChainProxy        : Securing GET /login
         * .s.r.w.a.BearerTokenAuthenticationFilter : Did not process request since did not find bearer token
         * o.s.s.w.a.AnonymousAuthenticationFilter  : Set SecurityContextHolder to anonymous SecurityContext
         * estMatcherDelegatingAuthorizationManager : Authorizing GET /login
         * estMatcherDelegatingAuthorizationManager : Checking authorization on GET /login using org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer$$Lambda/0x0000007801ada300@6186b13c
         * o.s.security.web.FilterChainProxy        : Secured GET /login
         */
        return Set.of("/auth/**");
    }

    @Override
    public Set<String> noRedirect(final ConfigSecurity security) {
        return this.noCache(security);
    }
}
