package io.r2mo.spring.security.oauth2;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * OAuth2 Token 服务类
 *
 * @author lang : 2025-11-11
 */
@Service
public class OAuth2TokenService {
    
    /**
     * 生成 Access Token
     *
     * @param userId 用户ID
     * @return OAuth2 Access Token
     */
    public OAuth2AccessToken generateAccessToken(UUID userId) {
        // 实际项目中这里应该生成真正的 OAuth2 Token
        // 这里只是一个示例实现
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "access_token_" + userId.toString(),
                Instant.now(),
                Instant.now().plusSeconds(3600) // 1小时后过期
        );
    }

    /**
     * 生成 Refresh Token
     *
     * @param userId 用户ID
     * @return OAuth2 Refresh Token
     */
    public OAuth2RefreshToken generateRefreshToken(UUID userId) {
        // 实际项目中这里应该生成真正的 OAuth2 Refresh Token
        // 这里只是一个示例实现
        return new OAuth2RefreshToken(
                "refresh_token_" + userId.toString(),
                Instant.now()
        );
    }
}