package io.r2mo.spring.security.token;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.typed.webflow.AkkaOf;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public abstract class TokenSpring {
    public static Akka<String> refreshOf(final String refreshToken, final Function<String, String> refreshFn) {
        if (StrUtil.isEmpty(refreshToken)) {
            return AkkaOf.of((String) null);
        }
        // 获取用户缓存
        final UserCache userCache = UserCache.of();

        // 验证 Refresh（查缓存），验证 Refresh Token 是否有效 (是否存在且未过期)
        final Akka<UUID> userWait = userCache.tokenRefresh(refreshToken);
        if (Objects.isNull(userWait)) {
            return AkkaOf.of((String) null);
        }
        final UUID userId = userWait.v();
        if (Objects.isNull(userId)) {
            // Token 不存在或过期
            return AkkaOf.of((String) null);
        }

        // 获取 loginId，生成新的 Token
        final String loginId = userId.toString();
        // 生成新的 Access Token
        final String generated = refreshFn.apply(loginId);
        if (StrUtil.isEmpty(generated)) {
            // Access Token 生成失败
            return AkkaOf.of((String) null);
        }
        // --- 实现 "one-time use"：使旧的 Refresh Token 失效 ---
        userCache.tokenRefreshKo(refreshToken); // <--- 关键步骤：使旧 Token 失效

        // (可选) 生成并返回新的 Refresh Token (实现 "rotation")
        // String newRefreshToken = tokenGenerate(userId.toString());
        // return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
        // 返回最新的 Access Token
        // 客户端收到后，更新 Header Authorization: Bearer r2a_...
        return AkkaOf.of(generated);
    }
}
