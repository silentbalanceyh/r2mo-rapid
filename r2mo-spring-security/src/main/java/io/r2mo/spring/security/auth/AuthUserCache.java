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
        /*
         * 不删除向量缓存，保留一定时间会自动清除掉，下次进来也会重写，此处不用绝对的一致性，主要原因在于即使有向量缓存，如果
         * 无法查找到 UserAt 或 UserContext 也会被认为是未登录状态，向量缓存的意义在于可通过各种不同的标识快速查找用户ID，
         * 从而提高命中率
         */
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
