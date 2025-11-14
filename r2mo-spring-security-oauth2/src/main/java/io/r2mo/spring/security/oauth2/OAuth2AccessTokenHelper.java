package io.r2mo.spring.security.oauth2;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * OAuth2 Token 工具类
 * 提供 JWT 和 Opaque Token 的通用处理方法
 */
public class OAuth2AccessTokenHelper {
    public static final String CLAIM_CLIENT_ID = OAuth2ParameterNames.CLIENT_ID;
    public static final String CLAIM_USERNAME = OAuth2ParameterNames.USERNAME;
    public static final String CLAIM_SCOPE = OAuth2ParameterNames.SCOPE;
    public static final String CLAIM_SUB = "sub";
    public static final String AUTH_SCOPE_PREFIX = "SCOPE_";

    /**
     * 从 JWT 中提取主体标识
     *
     * @param jwt JWT 对象
     *
     * @return 主体标识
     */
    public static String extractPrincipalFromJwt(final Jwt jwt) {
        return firstNonBlank(
            jwt.getClaimAsString(CLAIM_USERNAME),
            jwt.getClaimAsString(CLAIM_SUB),
            jwt.getClaimAsString(CLAIM_CLIENT_ID)
        );
    }

    /**
     * 从 Opaque Token 中提取主体标识
     *
     * @param principal OAuth2AuthenticatedPrincipal 对象
     *
     * @return 主体标识
     */
    public static String extractPrincipalFromOpaque(final OAuth2AuthenticatedPrincipal principal) {
        final String name = principal.getName();
        final String username = asString(principal.getAttribute(CLAIM_USERNAME));
        final String sub = asString(principal.getAttribute(CLAIM_SUB));
        final String clientId = asString(principal.getAttribute(CLAIM_CLIENT_ID));

        return firstNonBlank(username, sub, clientId, name);
    }

    /**
     * 将权限范围转换为 SimpleGrantedAuthority 列表
     *
     * @param scopes 权限范围集合
     *
     * @return SimpleGrantedAuthority 列表
     */
    public static List<SimpleGrantedAuthority> toAuthorities(final Set<String> scopes) {
        return scopes.stream()
            .filter(StringUtils::hasText)
            .map(s -> new SimpleGrantedAuthority(AUTH_SCOPE_PREFIX + s))
            .collect(Collectors.toList());
    }

    /**
     * 从声明中提取权限范围
     *
     * @param claim 声明对象
     *
     * @return 权限范围集合
     */
    @SuppressWarnings("unused")
    public static Set<String> extractScopesFromClaim(final Object claim) {
        if (claim == null) {
            return Collections.emptySet();
        }

        if (claim instanceof Collection<?>) {
            return ((Collection<?>) claim).stream()
                .map(Object::toString)
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        }

        final String raw = claim.toString().trim();
        if (raw.isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(raw.split(" "))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .collect(Collectors.toSet());
    }

    /**
     * 将对象转换为字符串
     *
     * @param obj 对象
     *
     * @return 字符串表示
     */
    public static String asString(final Object obj) {
        return obj == null ? null : obj.toString();
    }

    /**
     * 获取第一个非空字符串
     *
     * @param values 字符串数组
     *
     * @return 第一个非空字符串，如果都为空则返回 null
     */
    public static String firstNonBlank(final String... values) {
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