package io.r2mo.spring.security.oauth2;

import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * OAuth2 Access Token Filter
 * <p>
 * - 提取 Authorization: Bearer <token>
 * - 优先使用 Spring 原生的 JwtDecoder / OpaqueTokenIntrospector
 * - 优先通过 AuthenticationManager 交给 Provider 处理；若没有 AuthenticationManager，则本地解码并直接设置 SecurityContext
 * - 所有字段名常量化，使用 OAuth2ParameterNames
 */
@Slf4j
@Component
@ConditionalOnBean(ConfigSecurityOAuth2.class)
public class OAuth2AccessTokenFilter extends OncePerRequestFilter {

    private static final String HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";

    private static final String CLAIM_CLIENT_ID = OAuth2ParameterNames.CLIENT_ID; // "client_id"
    private static final String CLAIM_USERNAME = OAuth2ParameterNames.USERNAME; // "username"
    private static final String CLAIM_SCOPE = OAuth2ParameterNames.SCOPE; // "scope"
    private static final String CLAIM_SUB = "sub";

    private static final String AUTH_SCOPE_PREFIX = "SCOPE_";
    private static final String ATTR_ISSUED_AT = "issued_at";
    private static final String ATTR_EXPIRES_AT = "expires_at";

    private final ConfigSecurityOAuth2 config;

    // 可选注入：如果有 AuthenticationManager，会把 BearerTokenAuthenticationToken 发给它处理（通常由 Provider 组合）
    private AuthenticationManager authenticationManager;
    private JwtDecoder jwtDecoder;
    private OpaqueTokenIntrospector opaqueTokenIntrospector;

    public OAuth2AccessTokenFilter(final ConfigSecurityOAuth2 config) {
        this.config = config;
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setJwtDecoder(final JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setOpaqueTokenIntrospector(final OpaqueTokenIntrospector opaqueTokenIntrospector) {
        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {

        if (this.config == null || !this.config.isOn()) {
            filterChain.doFilter(request, response);
            return;
        }

        final String rawToken = extractBearerToken(request);
        if (!StringUtils.hasText(rawToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 优先交由 AuthenticationManager（如果存在）
        if (this.authenticationManager != null) {
            try {
                final BearerTokenAuthenticationToken authRequest = new BearerTokenAuthenticationToken(rawToken);
                final var result = this.authenticationManager.authenticate(authRequest);
                if (result != null && result.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(result);
                }
                filterChain.doFilter(request, response);
                return;
            } catch (final Exception ex) {
                log.debug("[ R2MO ] AuthenticationManager 处理 BearerToken 失败，尝试本地解码: {}", ex.getMessage());
                // 继续回退处理
            }
        }

        // 回退：本地使用 JwtDecoder 或 OpaqueTokenIntrospector 解码并直接设置 SecurityContext
        try {
            if (this.jwtDecoder != null) {
                final Jwt jwt = this.jwtDecoder.decode(rawToken);
                this.handleJwt(jwt, rawToken);
            } else if (this.opaqueTokenIntrospector != null) {
                final OAuth2AuthenticatedPrincipal principal = this.opaqueTokenIntrospector.introspect(rawToken);
                this.handleOpaque(principal, rawToken);
            } else {
                log.debug("[ R2MO ] 未配置 JwtDecoder 或 OpaqueTokenIntrospector，无法验证 Access Token");
            }
        } catch (final Exception ex) {
            log.debug("[ R2MO ] Access token 解码/内省失败: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void handleJwt(final Jwt jwt, final String rawToken) {
        if (jwt == null) {
            return;
        }

        final String principal = firstNonBlank(
            asString(jwt.getClaimAsString(CLAIM_USERNAME)),
            asString(jwt.getClaimAsString(CLAIM_SUB)),
            asString(jwt.getClaimAsString(CLAIM_CLIENT_ID))
        );
        if (!StringUtils.hasText(principal)) {
            return;
        }

        final Set<String> scopes = extractScopesFromClaim(jwt.getClaim(CLAIM_SCOPE));
        final var authorities = scopes.stream()
            .filter(StringUtils::hasText)
            .map(s -> new SimpleGrantedAuthority(AUTH_SCOPE_PREFIX + s))
            .collect(Collectors.toList());

        // 对于 JWT，使用 JwtAuthenticationToken
        final JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleOpaque(final OAuth2AuthenticatedPrincipal principal, final String rawToken) {
        if (principal == null) {
            return;
        }

        final String principalName = principal.getName();
        final String principalFromAttr = asString(principal.getAttribute(CLAIM_USERNAME));
        final String principalSub = asString(principal.getAttribute(CLAIM_SUB));
        final String principalClient = asString(principal.getAttribute(CLAIM_CLIENT_ID));

        final String principalId = firstNonBlank(principalFromAttr, principalSub, principalClient, principalName);
        if (!StringUtils.hasText(principalId)) {
            return;
        }

        final Set<String> scopes = extractScopesFromClaim(principal.getAttribute(CLAIM_SCOPE));
        final var authorities = scopes.stream()
            .filter(StringUtils::hasText)
            .map(s -> new SimpleGrantedAuthority(AUTH_SCOPE_PREFIX + s))
            .collect(Collectors.toList());

        // 对于 Opaque，构建 BearerTokenAuthentication
        final OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            rawToken,
            null,
            null,
            scopes
        );

        final BearerTokenAuthentication authentication = new BearerTokenAuthentication(principal, accessToken, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static String extractBearerToken(final HttpServletRequest request) {
        final String header = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private static String asString(final Object obj) {
        return obj == null ? null : obj.toString();
    }

    private static Set<String> extractScopesFromClaim(final Object claim) {
        if (claim == null) {
            return Collections.emptySet();
        }
        if (claim instanceof Collection) {
            return ((Collection<?>) claim).stream()
                .map(Object::toString)
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        }
        final String raw = claim.toString();
        if (!StringUtils.hasText(raw)) {
            return Collections.emptySet();
        }
        return Arrays.stream(raw.split(" "))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .collect(Collectors.toSet());
    }

    private static String firstNonBlank(final String... values) {
        if (values == null) {
            return null;
        }
        for (final String v : values) {
            if (StringUtils.hasText(v)) {
                return v;
            }
        }
        return null;
    }
}
