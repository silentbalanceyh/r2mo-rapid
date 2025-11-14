package io.r2mo.spring.security.oauth2.client;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.spring.security.oauth2.OAuth2RegisteredClientBuilder;
import io.r2mo.spring.security.oauth2.config.ConfigOAuth2SpringClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
            .clientName(StringUtils.hasText(reg.getClientName()) ? reg.getClientName() : registrationId)
            .authorizationGrantTypes(grantTypes -> grantTypes.addAll(
                OAuth2RegisteredClientHelper.parseGrantTypes(reg.getAuthorizationGrantType())))
            .clientAuthenticationMethods(methods -> methods.addAll(
                OAuth2RegisteredClientHelper.parseClientAuthMethods(reg.getClientAuthenticationMethod())))
            .scopes(scopes -> {
                final List<String> scopeList = reg.getScope();
                if (scopeList != null && !scopeList.isEmpty()) {
                    scopes.addAll(scopeList);
                } else {
                    scopes.add("openid");
                }
            });

        final Set<AuthorizationGrantType> grantTypes = OAuth2RegisteredClientHelper.parseGrantTypes(reg.getAuthorizationGrantType());
        if (grantTypes.contains(AuthorizationGrantType.AUTHORIZATION_CODE)) {
            final String redirectUri = this.getRedirectUri(reg, provider, registrationId);
            if (StringUtils.hasText(redirectUri)) {
                builder.redirectUris(uris -> uris.add(redirectUri));
            }
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