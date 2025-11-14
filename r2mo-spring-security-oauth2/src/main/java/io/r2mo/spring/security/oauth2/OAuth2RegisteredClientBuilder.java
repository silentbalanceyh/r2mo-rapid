package io.r2mo.spring.security.oauth2;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Set;

/**
 * SPI 模式下的接口，用于将 Client 直接注册到 OAuth2 Registered Client 存储中，不同项目提供实现来处理
 *
 * @author lang : 2025-11-14
 */
public interface OAuth2RegisteredClientBuilder {
    Set<RegisteredClient> build();
}
