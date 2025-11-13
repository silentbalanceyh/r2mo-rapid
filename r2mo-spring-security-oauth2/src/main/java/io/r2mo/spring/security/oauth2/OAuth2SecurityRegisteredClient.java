package io.r2mo.spring.security.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * OAuth2 Security Registered Client 配置
 * <p>
 * 专门负责 OAuth2 客户端注册相关的 Bean 定义：
 * - RegisteredClientRepository（客户端仓库）
 * - OAuth2AuthorizationService（授权服务）
 * - OAuth2AuthorizationConsentService（授权同意服务）
 *
 * @author lang : 2025-11-13
 */
@Configuration
@Slf4j
public class OAuth2SecurityRegisteredClient extends OAuth2ConfigurationBase {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private OAuth2RegisteredClientInitializer clientInitializer;

    /**
     * 配置 RegisteredClient 仓库
     */
    @Bean
    public RegisteredClientRepository configureRegisteredClientRepository() {
        return this.configureIfEnabled(() -> {
            if (this.jdbcTemplate == null) {
                throw new IllegalStateException("[ R2MO ] OAuth2 需要 JdbcTemplate，请配置数据源");
            }

            if (this.clientInitializer == null) {
                throw new IllegalStateException("[ R2MO ] OAuth2 需要 OAuth2RegisteredClientInitializer");
            }

            log.info("[ R2MO ] 配置 RegisteredClientRepository");
            return this.clientInitializer.build(this.jdbcTemplate);
        });
    }

    /**
     * 配置 OAuth2 授权服务
     */
    @Bean
    public OAuth2AuthorizationService configureAuthorizationService(final RegisteredClientRepository clientRepository) {
        return this.configureIfEnabled(() -> {
            if (this.jdbcTemplate == null) {
                throw new IllegalStateException("[ R2MO ] OAuth2 需要 JdbcTemplate，请配置数据源");
            }

            log.info("[ R2MO ] 配置 OAuth2AuthorizationService");
            return new JdbcOAuth2AuthorizationService(this.jdbcTemplate, clientRepository);
        });
    }

    /**
     * 配置 OAuth2 授权同意服务
     */
    @Bean
    public OAuth2AuthorizationConsentService configureAuthorizationConsentService(final RegisteredClientRepository clientRepository) {
        return this.configureIfEnabled(() -> {
            if (this.jdbcTemplate == null) {
                throw new IllegalStateException("[ R2MO ] OAuth2 需要 JdbcTemplate，请配置数据源");
            }

            log.info("[ R2MO ] 配置 OAuth2AuthorizationConsentService");
            return new JdbcOAuth2AuthorizationConsentService(this.jdbcTemplate, clientRepository);
        });
    }
}
