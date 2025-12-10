package io.r2mo.spring.weco;

import io.r2mo.xync.weco.WeCoSession;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

/**
 * WeCoSession 的 Spring Cache 实现
 * <p>依赖 io.r2mo.spring.cache.SpringCacheManager 的特性，通过命名规则设置 TTL。</p>
 *
 * @author lang
 */
@Component
@RequiredArgsConstructor
public class WeCoSessionSpring implements WeCoSession {

    // 注入 CacheManager (由 r2mo-spring-cache 提供)
    private final CacheManager cacheManager;

    // 基础缓存名称
    private static final String CACHE_NAME_BASE = "weco_scan_session";

    @Override
    public void save(final String uuid, final String statusOr, final Duration expiredAt) {
        // 利用 r2mo-spring-cache 的命名规则：name#ttl
        // 注意：WeCoSession.keyOf(uuid) 返回的是完整的 Redis Key (如 weco:scan:uuid)
        // Spring Cache 会自动加前缀，这里作为 Cache Key 使用

        final String cacheName = CACHE_NAME_BASE + "#" + expiredAt.toMillis() + "ms";
        final Cache cache = this.cacheManager.getCache(cacheName);
        if (Objects.nonNull(cache)) {
            // key 使用 uuid 本身还是 fullKey? 
            // 接口定义的 keyOf(uuid) 是为了统一规范 key 格式。
            // 这里我们存入 keyOf(uuid)，这样如果有人直接查 Redis 也能查到（虽然 Spring Cache 会加一层 Name 前缀）
            // 假设 CacheManager 是 RedissonSpringCacheManager，它会将 Cache Name 作为 Hash Key (Redis Key)，Entry Key 作为 Field。
            // 所以 Redis 结构是: Hash("weco_scan_session") -> Field("weco:scan:uuid") = value
            // 这与直接 String Set 不同，但对于 Session 存储是完全 OK 的。

            // 为了保持 keyOf 的语义，我们使用 keyOf(uuid) 作为 key
            final String key = WeCoSession.keyOf(uuid);
            cache.put(key, statusOr);
        }
    }

    @Override
    public String get(final String uuid) {
        // 获取缓存时，ttl 后缀不影响获取同一个 Cache 实例 (只要 base name 相同)
        final Cache cache = this.cacheManager.getCache(CACHE_NAME_BASE);
        if (Objects.nonNull(cache)) {
            final String key = WeCoSession.keyOf(uuid);
            return cache.get(key, String.class);
        }
        return null;
    }

    @Override
    public void remove(final String uuid) {
        final Cache cache = this.cacheManager.getCache(CACHE_NAME_BASE);
        if (Objects.nonNull(cache)) {
            final String key = WeCoSession.keyOf(uuid);
            cache.evict(key);
        }
    }
}
