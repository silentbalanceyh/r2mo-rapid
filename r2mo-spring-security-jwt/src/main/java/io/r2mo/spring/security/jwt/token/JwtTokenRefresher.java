package io.r2mo.spring.security.jwt.token;

import io.r2mo.jaas.session.UserCache;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

/**
 * Refresh Token 管理器 (基于 UserCache)
 * 负责生成、验证、存储和刷新 Refresh Token
 * 依赖 UserCache 存储 Token 与用户 ID 的映射，并利用其 Ko 方法使 Token 失效
 *
 * @author lang : 2025-11-12
 */
@Component
public class JwtTokenRefresher {

    @Autowired
    private ConfigSecurity config; // 注入配置

    @Autowired
    private JwtTokenGenerator tokenGenerator; // 注入 Access Token 生成器

    /**
     * 生成 Refresh Token 并存储到 UserCache
     *
     * @param userId 用户 ID (String 格式，假设为 UUID 字符串)
     *
     * @return Refresh Token 字符串
     */
    public String tokenGenerate(final String userId) {
        final ConfigSecurityJwt jwt = this.getConfiguration();
        if (Objects.isNull(jwt)) {
            return null;
        }
        // 1. 生成一个安全的随机 Refresh Token
        final String refreshToken = UUID.randomUUID().toString().replace("-", ""); // 移除 UUID 中的横线

        // 2. 获取 UserCache 实例
        final UserCache userCache = UserCache.of();

        // 3. 将 Refresh Token 与用户 ID 存储到缓存
        userCache.tokenRefresh(refreshToken, UUID.fromString(userId)); // 假设 userId 是 UUID 字符串

        return refreshToken;
    }

    /**
     * 验证 Refresh Token 是否有效，并生成新的 Access Token (可选新 Refresh Token)
     * 同时，实现 "one-time use" 机制：验证成功后，立即使旧的 Refresh Token 失效。
     *
     * @param refreshToken 客户端提供的旧 Refresh Token
     *
     * @return 包含新 Access Token (和可选新 Refresh Token) 的 Map。
     * 如果旧 Refresh Token 无效，则返回 null。
     */
    public String tokenRefresh(final String refreshToken) {
        final ConfigSecurityJwt jwt = this.getConfiguration();
        if (Objects.isNull(jwt)) {
            return null;
        }
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return null;
        }

        // 1. 获取 UserCache 实例
        final UserCache userCache = UserCache.of();

        // 2. 验证 Refresh Token 是否有效 (是否存在且未过期)
        final UUID userId = userCache.tokenRefresh(refreshToken);
        if (userId == null) {
            // Token 不存在或已过期
            return null;
        }

        // 3. 获取 loginId (简化处理)
        final String loginId = userId.toString();

        // 4. 生成新的 Access Token
        final String newAccessToken = this.tokenGenerator.tokenGenerate(loginId, null); // additionalData 传 null
        if (newAccessToken == null) {
            // Access Token 生成失败
            return null;
        }

        // --- 实现 "one-time use"：使旧的 Refresh Token 失效 ---
        userCache.tokenRefreshKo(refreshToken); // <--- 关键步骤：使旧 Token 失效

        // 5. (可选) 生成并返回新的 Refresh Token (实现 "rotation")
        // String newRefreshToken = tokenGenerate(userId.toString());
        // return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);

        // 6. 返回新生成的 Access Token (当前实现不返回新 Refresh Token)
        // 这个 Map 会被 Controller 接收，并作为 JSON 响应体的一部分返回给客户端
        return newAccessToken;
        // 或者，如果需要返回更多信息：
        // return Map.of("token", newAccessToken, "tokenType", "Bearer", "expiresIn", 3600); // 示例
    }

    /**
     * 撤销 (删除/作废) 指定的 Refresh Token
     * 通常在用户登出时调用
     *
     * @param refreshToken 要撤销的 Refresh Token
     */
    public void tokenRevoke(final String refreshToken) {
        if (refreshToken != null && !refreshToken.trim().isEmpty()) {
            final UserCache userCache = UserCache.of();
            // 调用 UserCache 的 Ko 方法来删除/标记 Refresh Token
            userCache.tokenRefreshKo(refreshToken);
        }
    }


    /**
     * 获取 JWT 配置 (如果需要从配置中获取 Refresh Token 的有效期等信息)
     *
     * @return ConfigSecurityJwt 对象，如果未启用或配置错误则返回 null
     */
    private ConfigSecurityJwt getConfiguration() {
        if (Objects.isNull(this.config)) {
            return null;
        }
        if (!this.config.isJwt()) {
            return null;
        }
        return this.config.getJwt();
    }
}