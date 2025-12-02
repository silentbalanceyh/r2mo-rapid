package io.r2mo.spring.security.extension.cache;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.enums.TypeLogin;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityCaptcha;
import io.r2mo.spring.security.config.ConfigSecurityJwt;
import io.r2mo.spring.security.config.ConfigSecurityLimit;
import io.r2mo.typed.cc.CacheAt;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.web._501NotSupportException;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * 缓存工厂基类，提供统一配置和基础方法
 *
 * @author lang : 2025-12-02
 */
@SuppressWarnings("unchecked")
public abstract class CacheAtSecurityBase implements CacheAtSecurity {

    protected final ConfigSecurity security;
    protected final long size;
    protected final Duration duration;
    protected static final Cc<String, CacheAt<?, ?>> CC_CACHE = Cc.open();

    public CacheAtSecurityBase() {
        this.security = SpringUtil.getBean(ConfigSecurity.class);
        Objects.requireNonNull(this.security, "[ R2MO ] 配置项未正确初始化！");
        this.size = this.security.getLimit().getSession();
        this.duration = Duration.ofMinutes(this.security.getLimit().getTimeout());
    }

    /**
     * 获取授权码缓存的限制配置
     */
    protected Kv<Long, Duration> findLimit(final TypeLogin type) {
        Duration duration = null;

        // 处理验证码特殊情况
        if (type.name().startsWith(TypeLogin.CAPTCHA.name())) {
            if (!this.security.isCaptcha()) {
                throw new _501NotSupportException("[ R2MO ] 未启用图片验证码功能！");
            }
            final ConfigSecurityCaptcha captcha = this.security.getCaptcha();
            duration = Duration.ofSeconds(captcha.getExpiredAt());
        }

        final ConfigSecurityLimit limit = this.security.getLimit();
        if (Objects.isNull(duration)) {
            final Kv<Long, Duration> kv = limit.getLimit(type);
            if (Objects.nonNull(kv)) {
                return kv;
            }
            duration = Duration.ofSeconds(60); // 默认60秒
        }

        final long authorizeSize = limit.getAuthorize();
        return Kv.create(authorizeSize, duration);
    }

    @Override
    public CacheAt<String, String> ofAuthorize(final TypeLogin type) {
        Objects.requireNonNull(type, "[ R2MO ] 授权码类型不可为空！");
        final String name = UserCache.NAME_AUTHORIZE + "@" + type.name() + "/" + this.getClass().getName();
        return (CacheAt<String, String>) CC_CACHE.pick(() -> {
            final CacheAt<String, String> ofAuthorize = this.create(name, String.class, String.class);
            final Kv<Long, Duration> limit = this.findLimit(type);
            ofAuthorize.configure(limit.value(), limit.key());
            return ofAuthorize;
        }, name);
    }

    @Override
    public CacheAt<UUID, UserAt> userAt() {
        final String name = UserCache.NAME_USER_AT + "/" + this.getClass().getName();
        return (CacheAt<UUID, UserAt>) CC_CACHE.pick(() -> {
            final CacheAt<UUID, UserAt> userAt = this.create(name, UUID.class, UserAt.class);
            userAt.configure(this.duration, this.size);
            return userAt;
        }, name);
    }

    @Override
    public CacheAt<UUID, UserContext> userContext() {
        final String name = UserCache.NAME_USER_CONTEXT + "/" + this.getClass().getName();
        return (CacheAt<UUID, UserContext>) CC_CACHE.pick(() -> {
            final CacheAt<UUID, UserContext> userContext = this.create(name, UUID.class, UserContext.class);
            userContext.configure(this.duration, this.size);
            return userContext;
        }, name);
    }

    @Override
    public CacheAt<String, UUID> userVector() {
        final String name = UserCache.NAME_USER_VECTOR + "/" + this.getClass().getName();
        return (CacheAt<String, UUID>) CC_CACHE.pick(() -> {
            final CacheAt<String, UUID> userVector = this.create(name, String.class, UUID.class);
            userVector.configure(this.duration, this.size * 8);
            return userVector;
        }, name);
    }

    @Override
    public CacheAt<String, UUID> ofToken() {
        final ConfigSecurityJwt jwt = this.security.getJwt();
        if (Objects.isNull(jwt)) {
            throw new _501NotSupportException("[ R2MO ] 未启用 JWT 功能，无法使用 Token 缓存");
        }
        final String name = UserCache.NAME_TOKEN + "/" + this.getClass().getName();
        return (CacheAt<String, UUID>) CC_CACHE.pick(() -> {
            final CacheAt<String, UUID> ofToken = this.create(name, String.class, UUID.class);
            final Duration duration = R2MO.toDuration(jwt.getExpiredAt());
            ofToken.configure(duration, this.security.getLimit().getToken());
            return ofToken;
        }, name);
    }

    @Override
    public CacheAt<String, UUID> ofRefresh() {
        final ConfigSecurityJwt jwt = this.security.getJwt();
        if (Objects.isNull(jwt)) {
            throw new _501NotSupportException("[ R2MO ] 未启用 JWT 功能，无法使用 Refresh Token 缓存");
        }
        final String name = UserCache.NAME_REFRESH + "/" + this.getClass().getName();
        return (CacheAt<String, UUID>) CC_CACHE.pick(() -> {
            final CacheAt<String, UUID> ofToken = this.create(name, String.class, UUID.class);
            final Duration duration = R2MO.toDuration(jwt.getRefreshAt());
            ofToken.configure(duration, this.security.getLimit().getToken());
            return ofToken;
        }, name);
    }

    protected abstract <K, V> CacheAt<K, V> create(String name, Class<K> keyType, Class<V> valueType);
}