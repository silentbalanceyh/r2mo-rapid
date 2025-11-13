package io.r2mo.spring.security.oauth2;

import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * OAuth2 Access Token AuthenticationProvider
 * 支持 JWT (JwtDecoder) 与 Opaque Token (OpaqueTokenIntrospector)
 */
@Slf4j
@Component
@ConditionalOnBean(value = {ConfigSecurityOAuth2.class})
public class OAuth2AccessTokenProvider implements AuthenticationProvider {

    private static final String CLAIM_CLIENT_ID = OAuth2ParameterNames.CLIENT_ID;
    private static final String CLAIM_USERNAME = OAuth2ParameterNames.USERNAME;
    private static final String CLAIM_SCOPE = OAuth2ParameterNames.SCOPE;
    private static final String CLAIM_SUB = "sub";
    private static final String AUTH_SCOPE_PREFIX = "SCOPE_";
    private static final String ERROR_INVALID_TOKEN = "invalid_token";

    private final JwtDecoder jwtDecoder;
    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    @Autowired
    public OAuth2AccessTokenProvider(@Nullable final JwtDecoder jwtDecoder,
                                     @Nullable final OpaqueTokenIntrospector opaqueTokenIntrospector) {
        this.jwtDecoder = jwtDecoder;
        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Authentication authenticate(@NonNull final Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof BearerTokenAuthenticationToken)) {
            return null;
        }

        final BearerTokenAuthenticationToken bearer = (BearerTokenAuthenticationToken) authentication;
        final String token = bearer.getToken();

        if (!StringUtils.hasText(token)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_INVALID_TOKEN, "empty token", null));
        }

        try {
            if (this.jwtDecoder != null) {
                final Jwt jwt = this.jwtDecoder.decode(token);
                return this.buildFromJwt(jwt);
            }

            if (this.opaqueTokenIntrospector != null) {
                final OAuth2AuthenticatedPrincipal principal = this.opaqueTokenIntrospector.introspect(token);
                return this.buildFromPrincipal(principal, token);
            }

            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_INVALID_TOKEN, "no decoder/introspector configured", null));
        } catch (final OAuth2AuthenticationException ex) {
            throw ex;
        } catch (final Exception ex) {
            log.debug("[ R2MO ] token decode/introspect failed: {}", ex.getMessage());
            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_INVALID_TOKEN, "token invalid", null), ex);
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private Authentication buildFromJwt(final Jwt jwt) {
        if (jwt == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_INVALID_TOKEN, "jwt null", null));
        }

        final String principal = firstNonBlank(
            jwt.getClaimAsString(CLAIM_USERNAME),
            jwt.getClaimAsString(CLAIM_SUB),
            jwt.getClaimAsString(CLAIM_CLIENT_ID)
        );

        if (!StringUtils.hasText(principal)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_INVALID_TOKEN, "missing principal", null));
        }

        final Set<String> scopes = extractScopesFromClaim(jwt.getClaim(CLAIM_SCOPE));
        final var authorities = scopes.stream()
            .filter(StringUtils::hasText)
            .map(s -> new SimpleGrantedAuthority(AUTH_SCOPE_PREFIX + s))
            .collect(Collectors.toList());

        // 使用 JwtAuthenticationToken，Spring 原生用于 JWT
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Authentication buildFromPrincipal(final OAuth2AuthenticatedPrincipal principal, final String rawToken) {
        if (principal == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_INVALID_TOKEN, "principal null", null));
        }

        final String principalName = principal.getName();
        final String principalFromAttr = asString(principal.getAttribute(CLAIM_USERNAME));
        final String principalSub = asString(principal.getAttribute(CLAIM_SUB));
        final String principalClient = asString(principal.getAttribute(CLAIM_CLIENT_ID));

        final String principalId = firstNonBlank(principalFromAttr, principalSub, principalClient, principalName);
        if (!StringUtils.hasText(principalId)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_INVALID_TOKEN, "missing principal", null));
        }

        final Set<String> scopes = extractScopesFromClaim(principal.getAttribute(CLAIM_SCOPE));
        final var authorities = scopes.stream()
            .filter(StringUtils::hasText)
            .map(s -> new SimpleGrantedAuthority(AUTH_SCOPE_PREFIX + s))
            .collect(Collectors.toList());

        final OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            rawToken,
            null,
            null,
            scopes
        );

        return new BearerTokenAuthentication(principal, accessToken, authorities);
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
