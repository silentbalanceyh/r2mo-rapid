package io.r2mo.spring.security.oauth2;

import io.r2mo.spring.security.oauth2.config.ConfigOAuth2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * OAuth2 Access Token AuthenticationProvider
 * 支持 JWT (JwtDecoder) 与 Opaque Token (OpaqueTokenIntrospector)
 */
@Slf4j
@Component
@ConditionalOnBean(ConfigOAuth2.class)
public class OAuth2AccessTokenProvider implements AuthenticationProvider {

    private static final String CLAIM_CLIENT_ID = OAuth2AccessTokenHelper.CLAIM_CLIENT_ID;
    private static final String CLAIM_USERNAME = OAuth2AccessTokenHelper.CLAIM_USERNAME;
    private static final String CLAIM_SCOPE = OAuth2AccessTokenHelper.CLAIM_SCOPE;
    private static final String CLAIM_SUB = OAuth2AccessTokenHelper.CLAIM_SUB;
    private static final String AUTH_SCOPE_PREFIX = OAuth2AccessTokenHelper.AUTH_SCOPE_PREFIX;
    private static final String ERROR_INVALID_TOKEN = "invalid_token";

    private final org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;
    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    @Autowired
    public OAuth2AccessTokenProvider(@Nullable final org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder,
                                     @Nullable final OpaqueTokenIntrospector opaqueTokenIntrospector) {
        this.jwtDecoder = jwtDecoder;
        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
    }

    @Override
    public Authentication authenticate(@NonNull final Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof BearerTokenAuthenticationToken)) {
            return null;
        }

        final String token = ((BearerTokenAuthenticationToken) authentication).getToken();
        if (!StringUtils.hasText(token)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(ERROR_INVALID_TOKEN, "empty token", null));
        }

        try {
            if (this.jwtDecoder != null) {
                final Jwt jwt = this.jwtDecoder.decode(token);
                return this.buildAuthentication(jwt);
            }

            if (this.opaqueTokenIntrospector != null) {
                final OAuth2AuthenticatedPrincipal principal = this.opaqueTokenIntrospector.introspect(token);
                return this.buildAuthentication(principal, token);
            }

            throw new OAuth2AuthenticationException(
                new OAuth2Error(ERROR_INVALID_TOKEN, "no decoder/introspector configured", null)
            );
        } catch (final OAuth2AuthenticationException ex) {
            throw ex;
        } catch (final Exception ex) {
            log.debug("[ R2MO ] token decode/introspect failed: {}", ex.getMessage());
            throw new OAuth2AuthenticationException(
                new OAuth2Error(ERROR_INVALID_TOKEN, "token invalid", null), ex
            );
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }

    // === 统一构建 Authentication 的入口 ===

    private Authentication buildAuthentication(final Jwt jwt) {
        final String principal = OAuth2AccessTokenHelper.extractPrincipalFromJwt(jwt);
        if (!StringUtils.hasText(principal)) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error(ERROR_INVALID_TOKEN, "missing principal in JWT", null)
            );
        }

        final Set<String> scopes = OAuth2AccessTokenHelper.extractScopesFromClaim(jwt.getClaim(OAuth2AccessTokenHelper.CLAIM_SCOPE));
        final var authorities = OAuth2AccessTokenHelper.toAuthorities(scopes);

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Authentication buildAuthentication(final OAuth2AuthenticatedPrincipal principal, final String rawToken) {
        final String principalName = OAuth2AccessTokenHelper.extractPrincipalFromOpaque(principal);
        if (!StringUtils.hasText(principalName)) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error(ERROR_INVALID_TOKEN, "missing principal in opaque token", null)
            );
        }

        final Set<String> scopes = OAuth2AccessTokenHelper.extractScopesFromClaim(principal.getAttribute(OAuth2AccessTokenHelper.CLAIM_SCOPE));
        final var authorities = OAuth2AccessTokenHelper.toAuthorities(scopes);

        final OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            rawToken,
            null,
            null,
            scopes
        );

        return new BearerTokenAuthentication(principal, accessToken, authorities);
    }
}