package io.r2mo.spring.security.auth;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.enums.TypeID;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.common.Kv;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 如果后期改用分布式存储，此处的内容可进行扩展
 *
 * @author lang : 2025-11-12
 */
@Slf4j
@SPID(UserCache.DEFAULT_NAME)
public class AuthUserCache implements UserCache {

    private static final CacheManager USER_AT =
        configure(NAME_AT, 120, UUID.class, UserAt.class);
    private static final CacheManager USER_CONTEXT =
        configure(NAME_CONTEXT, 120, UUID.class, UserContext.class);
    private static final CacheManager USER_VECTOR =
        configure(NAME_VECTOR, 120, String.class, UUID.class);
    // --- 新增：Access Token 缓存 ---
    private static final CacheManager TOKEN =
        configure(NAME_TOKEN, 120, String.class, UUID.class); // 缓存 Token -> UserId
    // --- 新增：Refresh Token 缓存 ---
    private static final CacheManager TOKEN_REFRESH =
        configure(NAME_TOKEN_REFRESH, 43200, String.class, UUID.class); // 缓存 Refresh Token -> UserId, 30天
    private static final ConcurrentMap<TypeID, CacheManager> USER_AUTHORIZE = new ConcurrentHashMap<>();

    private Cache<UUID, UserAt> cacheUserAt() {
        return USER_AT.getCache(NAME_AT, UUID.class, UserAt.class);
    }

    private Cache<UUID, UserContext> cacheContext() {
        return USER_CONTEXT.getCache(NAME_CONTEXT, UUID.class, UserContext.class);
    }

    private Cache<String, UUID> cacheVector() {
        return USER_VECTOR.getCache(NAME_VECTOR, String.class, UUID.class);
    }

    // --- 新增：Token 缓存访问方法 ---
    private Cache<String, UUID> cacheToken() {
        return TOKEN.getCache(NAME_TOKEN, String.class, UUID.class);
    }

    private Cache<String, UUID> cacheTokenRefresh() {
        return TOKEN_REFRESH.getCache(NAME_TOKEN_REFRESH, String.class, UUID.class);
    }


    @Override
    public void login(final UserContext context) {
        this.cacheContext().put(context.id(), context);
        this.cacheVector(context.logged());
    }

    @Override
    public void login(final UserAt userAt) {
        this.cacheUserAt().put(userAt.id(), userAt);
        this.cacheVector(userAt.logged());
    }

    private void cacheVector(final MSUser user) {
        final Set<String> idKeys = user.idKeys();
        idKeys.forEach(id -> this.cacheVector().put(id, user.getId()));
    }

    @Override
    public void logout(final UUID userId) {
        this.cacheUserAt().remove(userId);
        this.cacheContext().remove(userId);
        // 登出时可能需要清理相关的 Token 缓存，但这通常依赖 Token 自身的过期
        // 或者需要维护反向映射 (UserId -> TokenSet) 才能高效清理
        // 暂时忽略，依赖 TTL
    }

    @Override
    public UserContext context(final UUID id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return this.cacheContext().get(id);
    }

    @Override
    public UserAt find(final String idOr) {
        final UUID userId = this.cacheVector().get(idOr);
        if (Objects.isNull(userId)) {
            return null;
        }
        return this.find(userId);
    }

    @Override
    public UserAt find(final UUID id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return this.cacheUserAt().get(id);
    }

    @Override
    public void authorize(final Kv<String, String> generated, final TypeID type) {
        final Cache<String, String> cache = this.getOrCreate(type);
        cache.put(generated.key(), generated.value());
        log.info("[ R2MO ] 生成验证码：id = {} / code = {}", generated.key(), generated.value());
    }

    @Override
    public void authorize(final String consumerId, final TypeID type) {
        final Cache<String, String> cache = this.getOrCreate(type);
        cache.remove(consumerId);
        log.info("[ R2MO ] 消费验证码：id = {}", consumerId);
    }

    // --- 实现令牌部分专用缓存方法 ---
    @Override
    public void token(final String token, final UUID userId) {
        if (token != null && userId != null) {
            this.cacheToken().put(token, userId); // 缓存 Token -> UserId
        }
    }

    @Override
    public UUID token(final String token) {
        if (token != null) {
            return this.cacheToken().get(token); // 获取 Token 对应的 UserId
        }
        return null;
    }

    @Override
    public boolean tokenKo(final String token) {
        if (token != null) {
            this.cacheToken().remove(token);
        }
        return true;
    }

    @Override
    public void tokenRefresh(final String refreshToken, final UUID userId) {
        if (refreshToken != null && userId != null) {
            this.cacheTokenRefresh().put(refreshToken, userId); // 缓存 Refresh Token -> UserId
        }
    }

    @Override
    public UUID tokenRefresh(final String refreshToken) {
        if (refreshToken != null) {
            return this.cacheTokenRefresh().get(refreshToken); // 获取 Refresh Token 对应的 UserId
        }
        return null;
    }

    @Override
    public boolean tokenRefreshKo(final String refreshToken) {
        if (refreshToken != null) {
            this.cacheTokenRefresh().remove(refreshToken);
        }
        return true;
    }
    // --- 结束令牌部分专用缓存方法 ---


    private Cache<String, String> getOrCreate(final TypeID type) {
        final String name = NAME_AUTHORIZE + "@" + type.name();
        final CacheManager manager = USER_AUTHORIZE.computeIfAbsent(type, idType -> configure(name, 1, String.class, String.class));
        return manager.getCache(name, String.class, String.class);
    }

    private static CacheManager configure(final String name, final int mins,
                                          final Class<?> clazzK, final Class<?> clazzT) {
        return CacheManagerBuilder.newCacheManagerBuilder()
            .withCache(name, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                    clazzK, clazzT,
                    ResourcePoolsBuilder.heap(20000)   // 默认缓存 20000 个对象
                )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                    Duration.of(mins, ChronoUnit.MINUTES)
                ))
            ).build(true);
    }
}