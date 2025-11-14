package io.r2mo.spring.security.oauth2;

import io.r2mo.spi.SPI;
import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * OAuth2 RegisteredClient 仓库初始化器
 *
 * 职责：
 * 1. 创建 JdbcRegisteredClientRepository
 * 2. 初始化固定客户端（从配置文件）
 * 3. 支持动态客户端管理
 *
 * @author lang : 2025-11-13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2RegisteredClientInitializer {

    private final ConfigSecurityOAuth2 config;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构建 RegisteredClientRepository 并初始化固定客户端
     */
    public RegisteredClientRepository build(final JdbcTemplate jdbcTemplate) {
        final JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcTemplate);

        if (!this.config.isOn()) {
            log.info("[ R2MO ] OAuth2 插件未启用，跳过客户端初始化");
            return repository;
        }
        // 初始化固定客户端
        this.initializeClientFixed(repository);

        // 初始化扩展客户端 SPI 模式
        this.initializeClientExtension(repository);

        return repository;
    }

    private void initializeClientExtension(final JdbcRegisteredClientRepository repository) {
        final List<OAuth2RegisteredClientBuilder> builder = SPI.findMany(OAuth2RegisteredClientBuilder.class);

        builder.forEach(each -> {
            final Set<RegisteredClient> clientSet = each.buildSet();
            log.info("[ R2MO ] OAuth2 客户端构建器：{} / 数量：{}", each.getClass().getName(), clientSet.size());
            clientSet.stream()
                .filter(Objects::nonNull)
                .filter(this::validateClient)
                .forEach(repository::save);
        });
    }

    /**
     * 初始化固定客户端
     */
    private void initializeClientFixed(final JdbcRegisteredClientRepository repository) {
        final List<ConfigSecurityOAuth2.Client> clients = this.config.getClients();
        if (clients == null || clients.isEmpty()) {
            return;
        }

        for (final ConfigSecurityOAuth2.Client clientConfig : clients) {
            this.initializeClient(repository, clientConfig);
        }

        log.info("[ R2MO ] OAuth2 固定客户端初始化完成，共 {} 个", clients.size());
    }

    private boolean validateClient(final RegisteredClient client) {
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

        // 只有授权码模式需要 redirectUris
        if (grantTypes.contains(AuthorizationGrantType.AUTHORIZATION_CODE)) {
            final Set<String> redirectUris = client.getRedirectUris();
            if (redirectUris == null || redirectUris.isEmpty()) {
                return false;
            }
        }

        // 可选：长度限制
        return client.getClientId().length() <= 100;
    }

    /**
     * 初始化单个客户端
     */
    private void initializeClient(final JdbcRegisteredClientRepository repository,
                                  final ConfigSecurityOAuth2.Client clientConfig) {

        // 检查客户端是否已存在
        final RegisteredClient existing = repository.findByClientId(clientConfig.getClientId());
        if (existing != null) {
            log.info("[ R2MO ] OAuth2 客户端已存在，跳过：clientId = {}", clientConfig.getClientId());
            return;
        }

        // 构建 RegisteredClient
        final RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(clientConfig.getClientId())
            .clientName(clientConfig.getClientName() != null ? clientConfig.getClientName() : clientConfig.getClientId());

        // 客户端密钥（加密）
        if (clientConfig.getClientSecret() != null) {
            String encodedSecret = clientConfig.getClientSecret();
            // 如果密钥未加密，则加密
            if (!encodedSecret.startsWith("{")) {
                encodedSecret = this.passwordEncoder.encode(encodedSecret);
            }
            builder.clientSecret(encodedSecret);
        }

        // 客户端认证方式
        if (clientConfig.getAuthMethods() != null && !clientConfig.getAuthMethods().isEmpty()) {
            clientConfig.getAuthMethods().forEach(method ->
                builder.clientAuthenticationMethod(this.resolveAuthMethod(method))
            );
        } else {
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        }

        // 授权类型
        if (clientConfig.getGrantTypes() != null && !clientConfig.getGrantTypes().isEmpty()) {
            clientConfig.getGrantTypes().forEach(grantType ->
                builder.authorizationGrantType(this.resolveGrantType(grantType))
            );
        } else {
            builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        }

        // 回调地址
        if (clientConfig.getRedirectUris() != null && !clientConfig.getRedirectUris().isEmpty()) {
            builder.redirectUris(uris -> uris.addAll(clientConfig.getRedirectUris()));
        }

        // 登出回调地址
        if (clientConfig.getPostLogoutRedirectUris() != null && !clientConfig.getPostLogoutRedirectUris().isEmpty()) {
            builder.postLogoutRedirectUris(uris -> uris.addAll(clientConfig.getPostLogoutRedirectUris()));
        }

        // Scope
        if (clientConfig.getScopes() != null && !clientConfig.getScopes().isEmpty()) {
            builder.scopes(scopes -> scopes.addAll(clientConfig.getScopes()));
        }

        // 客户端设置
        final ClientSettings.Builder clientSettings = ClientSettings.builder();
        clientSettings.requireAuthorizationConsent(clientConfig.isRequireConsent());
        builder.clientSettings(clientSettings.build());

        // Token 设置
        final TokenSettings.Builder tokenSettings = TokenSettings.builder();
        tokenSettings.accessTokenTimeToLive(Duration.ofMillis(this.config.msAccessAt()));
        tokenSettings.refreshTokenTimeToLive(Duration.ofMillis(this.config.msRefreshAt()));
        tokenSettings.reuseRefreshTokens(this.config.isReuseRefreshToken());
        builder.tokenSettings(tokenSettings.build());

        // 保存客户端
        final RegisteredClient client = builder.build();
        if (!this.validateClient(client)) {
            log.warn("[ R2MO ] OAuth2 忽略非法客户端：clientId = {}", clientConfig.getClientId());
            return;
        }
        repository.save(client);

        log.info("[ R2MO ] OAuth2 客户端初始化成功：clientId = {}, grantTypes = {}",
            clientConfig.getClientId(),
            clientConfig.getGrantTypes());
    }

    /**
     * 解析客户端认证方式
     */
    private ClientAuthenticationMethod resolveAuthMethod(final String method) {
        return switch (method.toLowerCase()) {
            case "client_secret_basic" -> ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
            case "client_secret_post" -> ClientAuthenticationMethod.CLIENT_SECRET_POST;
            case "client_secret_jwt" -> ClientAuthenticationMethod.CLIENT_SECRET_JWT;
            case "private_key_jwt" -> ClientAuthenticationMethod.PRIVATE_KEY_JWT;
            case "none" -> ClientAuthenticationMethod.NONE;
            default -> {
                log.warn("[ R2MO ] 未知的客户端认证方式：{}，使用默认值 CLIENT_SECRET_BASIC", method);
                yield ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
            }
        };
    }

    /**
     * 解析授权类型
     */
    private AuthorizationGrantType resolveGrantType(final String grantType) {
        return switch (grantType.toLowerCase()) {
            case "authorization_code" -> AuthorizationGrantType.AUTHORIZATION_CODE;
            case "client_credentials" -> AuthorizationGrantType.CLIENT_CREDENTIALS;
            case "refresh_token" -> AuthorizationGrantType.REFRESH_TOKEN;
            case "password" -> new AuthorizationGrantType("password");
            case "device_code" -> AuthorizationGrantType.DEVICE_CODE;
            default -> {
                log.warn("[ R2MO ] 未知的授权类型：{}，使用默认值 authorization_code", grantType);
                yield AuthorizationGrantType.AUTHORIZATION_CODE;
            }
        };
    }
}

