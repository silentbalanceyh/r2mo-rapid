package io.r2mo.spring.security.oauth2.client;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.oauth2.OAuth2RegisteredClientBuilder;
import io.r2mo.spring.security.oauth2.config.ConfigOAuth2SpringClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * 自动注册客户端，对应数据格式
 * <pre>
 *     spring:
 *       security:
 *         oauth2:
 *           client:
 *             registration:
 *             provider:
 * </pre>
 *
 * @author lang : 2025-11-14
 */
@Slf4j
public class OAuth2RegisteredClientAuto implements OAuth2RegisteredClientBuilder {

    private final ConfigOAuth2SpringClient configuration;

    public OAuth2RegisteredClientAuto() {
        this.configuration = SpringUtil.getBean(ConfigOAuth2SpringClient.class);
    }

    @Override
    public Set<RegisteredClient> build() {
        if (this.configuration == null || this.configuration.getRegistration() == null) {
            return Collections.emptySet();
        }

        final Set<RegisteredClient> clients = new LinkedHashSet<>();
        for (final Map.Entry<String, ConfigOAuth2SpringClient.Registration> entry : this.configuration.getRegistration().entrySet()) {
            final String registrationId = entry.getKey();
            final ConfigOAuth2SpringClient.Registration reg = entry.getValue();

            final ConfigOAuth2SpringClient.Provider provider = this.getProvider(reg);

            final RegisteredClient candidate = this.buildRegisteredClient(registrationId, reg, provider);
            if (OAuth2RegisteredClientHelper.validateClient(candidate)) {
                clients.add(candidate);
            }
            log.info("[ R2MO ] ( Dev ) OAuth2 自动注册客户端：{}", candidate.getClientId());
        }
        return clients;
    }

    private ConfigOAuth2SpringClient.Provider getProvider(final ConfigOAuth2SpringClient.Registration reg) {
        if (StringUtils.hasText(reg.getProvider()) && this.configuration.getProvider() != null) {
            return this.configuration.getProvider().get(reg.getProvider());
        }
        return null;
    }

    private RegisteredClient buildRegisteredClient(final String registrationId,
                                                   final ConfigOAuth2SpringClient.Registration reg,
                                                   final ConfigOAuth2SpringClient.Provider provider) {
        final RegisteredClient.Builder builder = RegisteredClient.withId(registrationId)
            .clientId(reg.getClientId())
            .clientSecret(reg.getClientSecret())


            // 客户端名称：client-name 优先 -> 如果没有则使用 registrationId
            .clientName(StringUtils.hasText(reg.getClientName()) ? reg.getClientName() : registrationId)


            // 授权模式：authorization-grant-type
            .authorizationGrantTypes(grantTypes -> grantTypes.addAll(
                OAuth2RegisteredClientHelper.parseGrantTypes(reg.getAuthorizationGrantType())))


            // 客户端认证方法：client-authentication-method
            .clientAuthenticationMethods(methods -> methods.addAll(
                OAuth2RegisteredClientHelper.parseClientAuthMethods(reg.getClientAuthenticationMethod())));


        // 授权范围：scope
        final Set<String> scopeSet = OAuth2RegisteredClientHelper.parseScopes(reg.getScope());
        scopeSet.forEach(builder::scope);


        /*
         * 处理 authorization_code 授权模式
         * 处理 redirect-uri
         * 处理 post-logout-redirect-uri
         */
        final Set<AuthorizationGrantType> grantTypes = OAuth2RegisteredClientHelper.parseGrantTypes(reg.getAuthorizationGrantType());
        if (grantTypes.contains(AuthorizationGrantType.AUTHORIZATION_CODE)) {
            // 重定向 URI：redirect-uri
            final String redirectUri = this.getRedirectUri(reg, provider, registrationId);
            if (StringUtils.hasText(redirectUri)) {
                builder.redirectUris(uris -> uris.add(redirectUri));
            }


            // ============================================================
            // 处理 Post Logout Redirect URI (登出回调)
            // ============================================================
            if (StringUtils.hasText(reg.getRedirectUriPostLogout())) {
                builder.postLogoutRedirectUri(reg.getRedirectUriPostLogout());
            }
        }


        // 令牌设置：token-setting
        final TokenSettings tokenSettings = OAuth2RegisteredClientHelper.parseTokenSettings(reg.getSettingToken());
        if (Objects.nonNull(tokenSettings)) {
            builder.tokenSettings(tokenSettings);
        }


        // 客户端设置：client-setting
        final ClientSettings clientSettings = OAuth2RegisteredClientHelper.parseClientSettings(reg.getSettingClient());
        if (Objects.nonNull(clientSettings)) {
            builder.clientSettings(clientSettings);
        }
        return builder.build();
    }

    private String getRedirectUri(final ConfigOAuth2SpringClient.Registration reg,
                                  final ConfigOAuth2SpringClient.Provider provider,
                                  final String registrationId) {
        String redirectUri = reg.getRedirectUri();
        if (!StringUtils.hasText(redirectUri) && provider != null && StringUtils.hasText(provider.getIssuerUri())) {
            redirectUri = provider.getIssuerUri().replaceAll("/$", "") + "/login/oauth2/code/" + registrationId;
        }
        return redirectUri;
    }
}