package io.r2mo.spring.security.config;

import io.r2mo.jaas.token.TokenType;
import lombok.Data;

import java.io.Serializable;

/**
 * 第三方集成 Token 交换配置
 * <pre>
 *     security:
 *       exchange:
 *         enabled: true
 *         client-id: "third-party-client"
 *         client-secret: "secret-key"
 *         token-type: AES
 *         expired-at: 2h
 * </pre>
 *
 * @author lang : 2026-05-06
 */
@Data
public class ConfigSecurityExchange implements Serializable {
    private boolean enabled = false;
    private String clientId;
    private String clientSecret;
    private TokenType tokenType = TokenType.AES;
    private String expiredAt = "2h";
}
