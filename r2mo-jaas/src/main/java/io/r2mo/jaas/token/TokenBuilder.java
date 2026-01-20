package io.r2mo.jaas.token;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.typed.common.Kv;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author lang : 2025-11-12
 */
public interface TokenBuilder {

    static String withRefresh(final String refreshToken, final Function<String, String> refreshFn) {
        if (StrUtil.isEmpty(refreshToken)) {
            return null;
        }
        // 获取用户缓存
        final UserCache userCache = UserCache.of();

        // 验证 Refresh（查缓存），验证 Refresh Token 是否有效 (是否存在且未过期)
        final UUID userId = userCache.tokenRefresh(refreshToken);
        if (Objects.isNull(userId)) {
            // Token 不存在或过期
            return null;
        }

        // 获取 loginId，生成新的 Token
        final String loginId = userId.toString();
        // 生成新的 Access Token
        final String generated = refreshFn.apply(loginId);
        if (StrUtil.isEmpty(generated)) {
            // Access Token 生成失败
            return null;
        }
        // --- 实现 "one-time use"：使旧的 Refresh Token 失效 ---
        userCache.tokenRefreshKo(refreshToken); // <--- 关键步骤：使旧 Token 失效

        // (可选) 生成并返回新的 Refresh Token (实现 "rotation")
        // String newRefreshToken = tokenGenerate(userId.toString());
        // return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
        // 返回最新的 Access Token
        // 客户端收到后，更新 Header Authorization: Bearer r2a_...
        return generated;
    }

    /**
     * 此处执行两个流程
     * <pre>
     *     1. 验证 Token 有效性
     *     2. 解析 Token 获取用户ID
     * </pre>
     * 如果是一次性 Basic 的模式，则不支持此操作，返回 null 即可
     *
     * @param token 处理指定的 Token 字符串
     * @return 用户ID
     */
    default String accessOf(final String token) {
        return null;
    }

    String accessOf(UserAt userAt);

    default String refreshOf(final UserAt userAt) {
        throw new UnsupportedOperationException("[ R2MO ] 不支持的 Token 刷新操作");
    }

    default Kv<String, TokenType> tokenOf(final String token) {
        return null;
    }
}
