package io.r2mo.io.local.service;

import io.r2mo.base.io.transfer.token.TransferToken;
import io.r2mo.base.io.transfer.token.TransferTokenPool;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 高性能本地传输令牌池实现
 * 直接存储 TransferToken，最大化性能
 *
 * @author lang : 2025-09-16
 */
@Slf4j
public class LocalTokenPool implements TransferTokenPool {

    private static final JUtil UT = SPI.V_UTIL;

    // 使用 volatile 确保内存可见性
    private static final Map<String, TransferToken> TOKEN_STORE = new ConcurrentHashMap<>(1024);

    // 避免创建定时任务的开销，使用惰性清理策略

    @Override
    public boolean runSave(final TransferToken token, final long expiredAt) {
        try {
            // 直接存储，避免不必要的克隆（信任调用方）
            TOKEN_STORE.put(token.getToken(), token);
            return true;
        } catch (final Exception e) {
            log.error("[ R2MO ] 保存令牌失败: tokenId={}", token.getToken(), e);
            return false;
        }
    }

    @Override
    public boolean runExtend(final String token, final long expiredAt) {
        try {
            final TransferToken storedToken = TOKEN_STORE.get(token);
            if (storedToken == null) {
                return false;
            }

            // 直接更新过期时间（假设 TransferToken 支持修改）
            // 如果 TransferToken 是不可变的，需要创建新对象
            try {
                storedToken.setExpiredAt(java.time.LocalDateTime.now()
                    .plusSeconds((expiredAt - System.currentTimeMillis()) / 1000));
                return true;
            } catch (final UnsupportedOperationException e) {
                // 如果是不可变对象，直接替换
                final JObject json = UT.serializeJson(storedToken);
                final TransferToken newToken = UT.deserializeJson(json, TransferToken.class);
                newToken.setExpiredAt(java.time.LocalDateTime.now()
                    .plusSeconds((expiredAt - System.currentTimeMillis()) / 1000));
                TOKEN_STORE.put(token, newToken);
                return true;
            }
        } catch (final Exception e) {
            log.error("[ R2MO ] 延长令牌过期时间失败: tokenId={}", token, e);
            return false;
        }
    }

    @Override
    public boolean runDelete(final String token) {
        try {
            return TOKEN_STORE.remove(token) != null;
        } catch (final Exception e) {
            log.error("[ R2MO ] 删除令牌失败: tokenId={}", token, e);
            return false;
        }
    }

    @Override
    public TransferToken findBy(final String token) {
        try {
            final TransferToken storedToken = TOKEN_STORE.get(token);
            if (storedToken == null) {
                return null;
            }

            // 检查过期（惰性清理）
            if (this.isTokenExpired(storedToken)) {
                TOKEN_STORE.remove(token);
                return null;
            }

            return storedToken; // 直接返回，避免克隆开销
        } catch (final Exception e) {
            log.error("[ R2MO ] 获取令牌失败: tokenId={}", token, e);
            return null;
        }
    }

    @Override
    public boolean isExists(final String token) {
        try {
            final TransferToken storedToken = TOKEN_STORE.get(token);
            if (storedToken == null) {
                return false;
            }

            // 检查过期（惰性清理）
            if (this.isTokenExpired(storedToken)) {
                TOKEN_STORE.remove(token);
                return false;
            }

            return true;
        } catch (final Exception e) {
            log.error("[ R2MO ] 检查令牌存在性失败: tokenId={}", token, e);
            return false;
        }
    }

    @Override
    public long getExpired(final String token) {
        try {
            final TransferToken storedToken = TOKEN_STORE.get(token);
            if (storedToken == null) {
                return -1L;
            }

            // 检查过期（惰性清理）
            if (this.isTokenExpired(storedToken)) {
                TOKEN_STORE.remove(token);
                return -1L;
            }

            // 计算剩余时间
            if (storedToken.getExpiredAt() != null) {
                final long remaining = java.time.Duration.between(
                    java.time.LocalDateTime.now(),
                    storedToken.getExpiredAt()
                ).toMillis();
                return Math.max(0, remaining);
            }

            return -1L;
        } catch (final Exception e) {
            log.error("[ R2MO ] 获取令牌剩余时间失败: tokenId={}", token, e);
            return -1L;
        }
    }

    /**
     * 高性能过期检查
     */
    private boolean isTokenExpired(final TransferToken token) {
        try {
            return token.getExpiredAt() != null &&
                token.getExpiredAt().isBefore(java.time.LocalDateTime.now());
        } catch (final Exception e) {
            // 出错认为已过期
            return true;
        }
    }

    /**
     * 获取当前存储的令牌数量（用于监控）
     */
    public int size() {
        return TOKEN_STORE.size();
    }

    @Override
    public int runClean(final boolean expiredOnly) {
        if (expiredOnly) {
            return this.cleanExpired();
        } else {
            return this.clean();
        }
    }

    /**
     * 清理所有过期令牌（按需调用）
     */
    private int cleanExpired() {
        int cleanedCount = 0;
        final java.time.LocalDateTime now = java.time.LocalDateTime.now();

        for (final Map.Entry<String, TransferToken> entry : TOKEN_STORE.entrySet()) {
            try {
                final TransferToken token = entry.getValue();
                if (token.getExpiredAt() != null && token.getExpiredAt().isBefore(now)) {
                    TOKEN_STORE.remove(entry.getKey());
                    cleanedCount++;
                }
            } catch (final Exception e) {
                // 忽略单个清理错误
            }
        }

        log.info("[ R2MO ] 清理过期令牌完成，清理数量: {}", cleanedCount);
        return cleanedCount;
    }

    /**
     * 清空所有令牌（紧急情况使用）
     */
    private int clean() {
        final int size = TOKEN_STORE.size();
        TOKEN_STORE.clear();
        log.info("[ R2MO ] 清空所有令牌，原数量: {}", size);
        return size;
    }
}