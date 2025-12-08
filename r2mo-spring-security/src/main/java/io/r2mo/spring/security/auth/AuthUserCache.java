package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.spi.SPI;
import io.r2mo.spring.security.extension.cache.CacheAtSecurity;
import io.r2mo.spring.security.extension.cache.EhcacheCacheAtSecurity;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.CacheAt;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * 如果后期改用分布式存储，此处的内容可进行扩展，所以此处重新设计成基类方式，若子类可直接重写内置缓存的实现方案，则可直接处理
 * 缓存实现的方案有两种
 * <pre>
 *     1. 直接定制优先级更高的 {@link UserCache} 实现类
 *        - 这种模式自定义化的程度更高
 *     2. 直接定义类继承 {@link EhcacheCacheAtSecurity} 并重写缓存实现方案
 *        - 这种模式更方便，直接更改底层缓存实现即可
 *        - 记得此处一定要继承！！！
 * </pre>
 *
 * @author lang : 2025-11-12
 */
@Slf4j
@SPID
public class AuthUserCache implements UserCache {
    private static final Cc<String, CacheAtSecurity> CC_FACTORY = Cc.openThread();

    private CacheAtSecurity factory() {
        return CC_FACTORY.pick(() -> {
            final CacheAtSecurity found = SPI.findOneOf(CacheAtSecurity.class);
            if (Objects.isNull(found)) {
                log.error("[ R2MO ] 未配置缓存工厂：{}，请检查相关配置！", found);
            }
            return found;
        });
    }


    @Override
    public void login(final UserContext context) {
        this.factory().userContext().put(context.id(), context);
        this.cacheVector(context.logged());
    }

    @Override
    public void login(final UserAt userAt) {
        this.factory().userAt().put(userAt.id(), userAt);
        this.cacheVector(userAt.logged());
    }

    private void cacheVector(final MSUser user) {
        final Set<String> idKeys = user.ids();
        idKeys.forEach(id -> this.factory().userVector().put(id, user.getId()));
    }

    @Override
    public void logout(final UUID userId) {
        this.factory().userAt().remove(userId);
        this.factory().userContext().remove(userId);
        // 登出时可能需要清理相关的 Token 缓存，但这通常依赖 Token 自身的过期
        // 或者需要维护反向映射 (UserId -> TokenSet) 才能高效清理
        // 暂时忽略，依赖 TTL
    }

    @Override
    public UserContext context(final UUID id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return this.factory().userContext().find(id);
    }

    @Override
    public UserAt find(final String idOr) {
        final UUID userId = this.factory().userVector().find(idOr);
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
        return this.factory().userAt().find(id);
    }

    @Override
    public void authorize(final Kv<String, String> generated, final CaptchaArgs configuration) {
        final CacheAt<String, String> cache = this.factory().ofAuthorize(configuration);
        cache.put(generated.key(), generated.value());
        log.info("[ R2MO ] 生成验证码：id = {} / code = {}", generated.key(), generated.value());
    }

    @Override
    public String authorize(final String consumerId, final CaptchaArgs configuration) {
        final CacheAt<String, String> cache = this.factory().ofAuthorize(configuration);
        final String generated = cache.find(consumerId);
        if (Objects.isNull(generated)) {
            return null;
        }
        return generated;
    }

    @Override
    public void authorizeKo(final String consumerId, final CaptchaArgs configuration) {
        final CacheAt<String, String> cache = this.factory().ofAuthorize(configuration);
        cache.remove(consumerId);
        log.info("[ R2MO ] 消费验证码：id = {}", consumerId);
    }

    // --- 实现令牌部分专用缓存方法 ---
    @Override
    public void token(final String token, final UUID userId) {
        if (Objects.isNull(token) || Objects.isNull(userId)) {
            return;
        }

        this.factory().ofToken().put(token, userId); // 缓存 Token -> UserId
    }

    @Override
    public UUID token(final String token) {
        if (Objects.isNull(token)) {
            return null;
        }
        return this.factory().ofToken().find(token);
    }

    @Override
    public boolean tokenKo(final String token) {
        if (Objects.isNull(token)) {
            return false;
        }
        this.factory().ofToken().remove(token);
        return true;
    }

    @Override
    public void tokenRefresh(final String refreshToken, final UUID userId) {
        if (Objects.isNull(refreshToken) || Objects.isNull(userId)) {
            return;
        }
        this.factory().ofRefresh().put(refreshToken, userId); // 缓存 Refresh Token -> UserId
    }

    @Override
    public UUID tokenRefresh(final String refreshToken) {
        if (Objects.isNull(refreshToken)) {
            return null;
        }
        return this.factory().ofRefresh().find(refreshToken); // 获取 Refresh Token 对应的 UserId
    }

    @Override
    public boolean tokenRefreshKo(final String refreshToken) {
        if (Objects.isNull(refreshToken)) {
            return false;
        }
        return this.factory().ofRefresh().remove(refreshToken);
    }
    // --- 结束令牌部分专用缓存方法 ---
}