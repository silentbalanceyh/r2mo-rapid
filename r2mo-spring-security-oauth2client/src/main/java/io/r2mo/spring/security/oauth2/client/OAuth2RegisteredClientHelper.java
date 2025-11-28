package io.r2mo.spring.security.oauth2.client;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lang : 2025-11-14
 */
class OAuth2RegisteredClientHelper {

    static Set<AuthorizationGrantType> parseGrantTypes(final String grantTypeStr) {
        final Set<AuthorizationGrantType> types = new HashSet<>();
        if (!StringUtils.hasText(grantTypeStr)) {
            types.add(AuthorizationGrantType.AUTHORIZATION_CODE);
            return types;
        }

        for (final String part : grantTypeStr.split(",")) {
            final String trimmed = part.trim();
            switch (trimmed) {
                case "authorization_code":
                    types.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    break;
                case "client_credentials":
                    types.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                    break;
                case "refresh_token":
                    types.add(AuthorizationGrantType.REFRESH_TOKEN);
                    break;
                case "jwt":
                case "urn:ietf:params:oauth:grant-type:jwt-bearer":
                    types.add(AuthorizationGrantType.JWT_BEARER);
                    break;
                case "device":
                case "urn:ietf:params:oauth:grant-type:device_code":
                    types.add(AuthorizationGrantType.DEVICE_CODE);
                    break;
                case "exchange":
                case "urn:ietf:params:oauth:grant-type:token-exchange":
                    types.add(AuthorizationGrantType.TOKEN_EXCHANGE);
                    break;
                // 注意：PASSWORD 被明确排除（不安全且已废弃）
            }
        }
        return types;
    }

    static Set<String> parseScopes(final List<String> scopes) {
        final Set<String> scopeSet = new HashSet<>();
        if (scopes != null && !scopes.isEmpty()) {
            scopeSet.addAll(scopes);
        } else {
            scopeSet.add("openid");
        }
        return scopeSet;
    }

    static TokenSettings parseTokenSettings(final JObject settingJ) {
        if (SPI.V_UTIL.isEmpty(settingJ)) {
            return null;
        }
        final TokenSettings.Builder builder = TokenSettings.builder();
        final int minutes = settingJ.getInt("expired-at", 30);
        final int days = settingJ.getInt("refresh-at", 7);
        final boolean reuse = settingJ.getBool("reuse-refresh-token", false);
        builder.accessTokenTimeToLive(Duration.ofMinutes(minutes));
        builder.refreshTokenTimeToLive(Duration.ofDays(days));
        builder.reuseRefreshTokens(reuse);
        return builder.build();
    }

    static ClientSettings parseClientSettings(final JObject settingJ) {
        if (SPI.V_UTIL.isEmpty(settingJ)) {
            return null;
        }
        final ClientSettings.Builder builder = ClientSettings.builder();
        final boolean requireAuth = settingJ.getBool("require-proof-key", false);
        final boolean requireConsent = settingJ.getBool("require-authorization-consent", false);
        builder.requireAuthorizationConsent(requireConsent);
        builder.requireProofKey(requireAuth);
        return builder.build();
    }

    static Set<ClientAuthenticationMethod> parseClientAuthMethods(final String methodStr) {
        final Set<ClientAuthenticationMethod> methods = new HashSet<>();
        if (!StringUtils.hasText(methodStr)) {
            methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            return methods;
        }

        for (final String part : methodStr.split(",")) {
            final String trimmed = part.trim();
            switch (trimmed) {
                case "basic":       // 可兼容的值
                case "client_secret_basic":
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    break;
                case "post":        // 旧版，推荐拿掉
                case "client_secret_post":
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                    break;
                case "client_secret_jwt":
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
                    break;
                case "private_key_jwt":
                    methods.add(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
                    break;
                case "tls_client_auth":
                    methods.add(ClientAuthenticationMethod.TLS_CLIENT_AUTH);
                    break;
                case "self_signed_tls_client_auth":
                    methods.add(ClientAuthenticationMethod.SELF_SIGNED_TLS_CLIENT_AUTH);
                    break;
                case "none":
                    methods.add(ClientAuthenticationMethod.NONE);
                    break;
                // 不再处理 "basic"、"post" 等非标准别名
            }
        }
        return methods;
    }

    static boolean validateClient(final RegisteredClient client) {
        if (client == null) {
            return false;
        }

        if (client.getClientId() == null || client.getClientId().isBlank()) {
            return false;
        }

        if (client.getClientName() == null || client.getClientName().isBlank()) {
            return false;
        }

        final Set<ClientAuthenticationMethod> authMethods = client.getClientAuthenticationMethods();
        if (authMethods == null || authMethods.isEmpty()) {
            return false;
        }

        final Set<AuthorizationGrantType> grantTypes = client.getAuthorizationGrantTypes();
        if (grantTypes == null || grantTypes.isEmpty()) {
            return false;
        }

        final Set<String> scopes = client.getScopes();
        if (scopes == null || scopes.isEmpty()) {
            return false;
        }

        if (grantTypes.contains(AuthorizationGrantType.AUTHORIZATION_CODE)) {
            final Set<String> redirectUris = client.getRedirectUris();
            if (redirectUris == null || redirectUris.isEmpty()) {
                return false;
            }
        }

        return client.getClientId().length() <= 100;
    }
}