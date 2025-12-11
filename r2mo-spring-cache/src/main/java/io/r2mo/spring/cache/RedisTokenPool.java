package io.r2mo.spring.cache;

import io.r2mo.base.io.transfer.token.TransferToken;
import io.r2mo.base.io.transfer.token.TransferTokenPool;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 传输令牌池实现
 * 使用 Spring RedisTemplate 进行高性能存储
 *
 * @author lang : 2025-09-16
 */
@Component
@Slf4j
public class RedisTokenPool implements TransferTokenPool {

    private static final JUtil UT = SPI.V_UTIL;
    private static final String TOKEN_KEY_PREFIX = "r2mo:transfer:token:";
    private static final String TOKEN_EXPIRE_KEY_PREFIX = "r2mo:transfer:token:expire:";
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean runSave(final TransferToken token, final long expiredAt) {
        try {
            final String tokenId = token.getToken();
            final String tokenKey = TOKEN_KEY_PREFIX + tokenId;
            final String expireKey = TOKEN_EXPIRE_KEY_PREFIX + tokenId;

            // 序列化 TransferToken 对象
            final JObject tokenJson = UT.serializeJson(token);
            final String tokenValue = tokenJson.encode();

            // 计算过期时间差（秒）
            final long currentTime = System.currentTimeMillis();
            final long expireSeconds = Math.max(1, (expiredAt - currentTime) / 1000);

            // 批量存储
            this.redisTemplate.opsForValue().set(tokenKey, tokenValue, expireSeconds, TimeUnit.SECONDS);
            this.redisTemplate.opsForValue().set(expireKey, String.valueOf(expiredAt), expireSeconds, TimeUnit.SECONDS);

            log.debug("[ R2MO ] 保存令牌到Redis: tokenId={}, expireAt={}", tokenId, expiredAt);
            return true;

        } catch (final Exception e) {
            log.error("[ R2MO ] 保存令牌到Redis失败: tokenId={}", token.getToken(), e);
            return false;
        }
    }

    @Override
    public boolean runExtend(final String token, final long expiredAt) {
        try {
            final String tokenKey = TOKEN_KEY_PREFIX + token;
            final String expireKey = TOKEN_EXPIRE_KEY_PREFIX + token;

            // 检查令牌是否存在
            final Boolean exists = this.redisTemplate.hasKey(tokenKey);
            if (!exists) {
                log.warn("[ R2MO ] 延长令牌过期时间失败，令牌不存在: {}", token);
                return false;
            }

            // 计算新的过期时间差（秒）
            final long currentTime = System.currentTimeMillis();
            final long expireSeconds = Math.max(1, (expiredAt - currentTime) / 1000);

            // 更新过期时间
            this.redisTemplate.expire(tokenKey, expireSeconds, TimeUnit.SECONDS);
            this.redisTemplate.opsForValue().set(expireKey, String.valueOf(expiredAt), expireSeconds, TimeUnit.SECONDS);

            log.debug("[ R2MO ] 延长令牌过期时间: tokenId={}, newExpireAt={}", token, expiredAt);
            return true;

        } catch (final Exception e) {
            log.error("[ R2MO ] 延长令牌过期时间失败: tokenId={}", token, e);
            return false;
        }
    }

    @Override
    public boolean runDelete(final String token) {
        try {
            final String tokenKey = TOKEN_KEY_PREFIX + token;
            final String expireKey = TOKEN_EXPIRE_KEY_PREFIX + token;

            // 批量删除
            final Boolean deleted1 = this.redisTemplate.delete(tokenKey);
            final Boolean deleted2 = this.redisTemplate.delete(expireKey);

            final boolean success = deleted1 || deleted2;
            log.debug("[ R2MO ] 删除Redis令牌: tokenId={}, success={}", token, success);
            return success;

        } catch (final Exception e) {
            log.error("[ R2MO ] 删除Redis令牌失败: tokenId={}", token, e);
            return false;
        }
    }

    @Override
    public int runClean(final boolean expiredOnly) {
        try {
            if (expiredOnly) {
                return this.cleanExpired();
            } else {
                return this.clean();
            }
        } catch (final Exception e) {
            log.error("[ R2MO ] 清理Redis令牌失败", e);
            return 0;
        }
    }

    @Override
    public TransferToken findBy(final String token) {
        try {
            final String tokenKey = TOKEN_KEY_PREFIX + token;

            // 检查是否存在
            final Boolean exists = this.redisTemplate.hasKey(tokenKey);
            if (!exists) {
                return null;
            }

            // 获取令牌数据
            final String tokenValue = this.redisTemplate.opsForValue().get(tokenKey);
            if (tokenValue == null || tokenValue.isEmpty()) {
                return null;
            }

            // 反序列化
            final JObject tokenJ = JBase.parse(tokenValue);
            return UT.deserializeJson(tokenJ, TransferToken.class);

        } catch (final Exception e) {
            log.error("[ R2MO ] 从Redis获取令牌失败: tokenId={}", token, e);
            return null;
        }
    }

    @Override
    public boolean isExists(final String token) {
        try {
            final String tokenKey = TOKEN_KEY_PREFIX + token;
            return this.redisTemplate.hasKey(tokenKey);
        } catch (final Exception e) {
            log.error("[ R2MO ] 检查Redis令牌存在性失败: tokenId={}", token, e);
            return false;
        }
    }

    @Override
    public long getExpired(final String token) {
        try {
            final String tokenKey = TOKEN_KEY_PREFIX + token;
            final String expireKey = TOKEN_EXPIRE_KEY_PREFIX + token;

            // 检查令牌是否存在
            final Boolean exists = this.redisTemplate.hasKey(tokenKey);
            if (!exists) {
                return -1L;
            }

            // 获取过期时间
            final String expireValue = this.redisTemplate.opsForValue().get(expireKey);
            if (expireValue == null || expireValue.isEmpty()) {
                return -1L;
            }

            final long expireTime = Long.parseLong(expireValue);
            final long currentTime = System.currentTimeMillis();

            if (currentTime > expireTime) {
                // 已过期，清理
                this.runDelete(token);
                return -1L;
            }

            return expireTime - currentTime;

        } catch (final Exception e) {
            log.error("[ R2MO ] 获取Redis令牌剩余过期时间失败: tokenId={}", token, e);
            return -1L;
        }
    }

    /**
     * 清理所有过期令牌
     */
    private int cleanExpired() {
        try {
            int cleanedCount = 0;
            final long currentTime = System.currentTimeMillis();

            // 扫描所有令牌键
            final Set<String> keys = this.redisTemplate.keys(TOKEN_EXPIRE_KEY_PREFIX + "*");

            for (final String expireKey : keys) {
                try {
                    final String expireValue = this.redisTemplate.opsForValue().get(expireKey);
                    if (expireValue != null && !expireValue.isEmpty()) {
                        final long expireTime = Long.parseLong(expireValue);
                        if (currentTime > expireTime) {
                            // 过期了，清理对应的令牌
                            final String tokenId = expireKey.substring(TOKEN_EXPIRE_KEY_PREFIX.length());
                            final String tokenKey = TOKEN_KEY_PREFIX + tokenId;

                            this.redisTemplate.delete(expireKey);
                            this.redisTemplate.delete(tokenKey);
                            cleanedCount++;
                        }
                    }
                } catch (final Exception e) {
                    // 忽略单个清理错误
                }
            }

            log.info("[ R2MO ] 清理过期Redis令牌完成，清理数量: {}", cleanedCount);
            return cleanedCount;

        } catch (final Exception e) {
            log.error("[ R2MO ] 清理过期Redis令牌失败", e);
            return 0;
        }
    }

    /**
     * 清理所有令牌
     */
    private int clean() {
        try {
            int cleanedCount = 0;

            // 扫描所有令牌键
            final Set<String> keys = this.redisTemplate.keys(TOKEN_EXPIRE_KEY_PREFIX + "*");

            for (final String expireKey : keys) {
                try {
                    final String tokenId = expireKey.substring(TOKEN_EXPIRE_KEY_PREFIX.length());
                    final String tokenKey = TOKEN_KEY_PREFIX + tokenId;

                    this.redisTemplate.delete(expireKey);
                    this.redisTemplate.delete(tokenKey);
                    cleanedCount++;
                } catch (final Exception e) {
                    // 忽略单个清理错误
                }
            }

            log.info("[ R2MO ] 清理所有Redis令牌完成，清理数量: {}", cleanedCount);
            return cleanedCount;

        } catch (final Exception e) {
            log.error("[ R2MO ] 清理所有Redis令牌失败", e);
            return 0;
        }
    }
}
