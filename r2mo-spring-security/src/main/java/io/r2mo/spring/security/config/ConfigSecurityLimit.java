package io.r2mo.spring.security.config;

import io.r2mo.jaas.token.TokenType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @author lang : 2025-11-12
 */
@Configuration
@ConfigurationProperties(prefix = "security.limit")
@Data
@RefreshScope
public class ConfigSecurityLimit implements Serializable {
    /**
     * <pre>
     *     - 不登录的 BASIC，一次性的，只能使用 {@link TokenType#BASIC}，无刷新机制，因为客户端拿不到 accessToken
     *     - 登录后的 BASIC，只能使用 {@link TokenType#AES}，支持刷新机制
     *     - 开启了 JWT 和 OAUTH2 之后
     *       - POST /auth/login             依旧使用 AES，BASIC 标配
     *       - POST /auth/login-jwt         使用 JWT，JWT 标配
     *       - OAUTH2 登录后可使用 {@link TokenType#JWT} 或 {@link TokenType#OPAQUE}，取决于授权服务器配置
     *     其他场景：短信、邮件、LDAP 则使用此字段定义的默认值
     *       - 这种场景下可以使用 tokenType 定义的类型，此时才能选择 {@link TokenType#OPAQUE}
     * </pre>
     */
    private TokenType tokenType = TokenType.JWT;
    private long session = 8192;
    private long token = 4096;
    private long timeout = 120;
    private long authorize = 2048;
}
