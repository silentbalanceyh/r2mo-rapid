package io.r2mo.spring.security.extension.cache;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.enums.TypeID;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityCaptcha;
import io.r2mo.spring.security.config.ConfigSecurityJwt;
import io.r2mo.spring.security.config.ConfigSecurityLimit;
import io.r2mo.spring.security.extension.CacheOfFactory;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.CacheAt;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.web._501NotSupportException;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-11-12
 */
@SuppressWarnings("unchecked")
@SPID
public class CacheOfFactorySecurity implements CacheOfFactory {

    private final ConfigSecurity security;
    private final long size;
    private final Duration duration;
    private static final Cc<String, CacheAt<?, ?>> CC_CACHE = Cc.open();

    public CacheOfFactorySecurity() {
        this.security = SpringUtil.getBean(ConfigSecurity.class);
        Objects.requireNonNull(this.security, "[ R2MO ] 配置项未正确初始化！");
        this.size = this.security.getLimit().getSession();
        this.duration = Duration.ofMinutes(this.security.getLimit().getTimeout());
    }

    @Override
    public CacheAt<UUID, UserAt> userAt() {
        return (CacheAt<UUID, UserAt>) CC_CACHE.pick(() -> {
            final CacheAt<UUID, UserAt> userAt =
                this.create(UserCache.NAME_USER_AT, UUID.class, UserAt.class);
            userAt.configure(this.duration, this.size);
            return userAt;
        }, UserCache.NAME_USER_AT);
    }

    @Override
    public CacheAt<UUID, UserContext> userContext() {
        return (CacheAt<UUID, UserContext>) CC_CACHE.pick(() -> {
            final CacheAt<UUID, UserContext> userContext =
                this.create(UserCache.NAME_USER_CONTEXT, UUID.class, UserContext.class);
            userContext.configure(this.duration, this.size);
            return userContext;
        }, UserCache.NAME_USER_CONTEXT);
    }

    @Override
    public CacheAt<String, UUID> userVector() {
        return (CacheAt<String, UUID>) CC_CACHE.pick(() -> {
            final CacheAt<String, UUID> userVector =
                this.create(UserCache.NAME_USER_VECTOR, String.class, UUID.class);
            userVector.configure(this.duration, this.size * 8);
            return userVector;
        }, UserCache.NAME_USER_VECTOR);
    }

    @Override
    public CacheAt<String, UUID> ofToken() {
        // 只有 JWT 才启用
        final ConfigSecurityJwt jwt = this.security.getJwt();
        if (Objects.isNull(jwt)) {
            throw new _501NotSupportException("[ R2MO ] 未启用 JWT 功能，无法使用 Token 缓存");
        }
        return (CacheAt<String, UUID>) CC_CACHE.pick(() -> {
            final CacheAt<String, UUID> ofToken =
                this.create(UserCache.NAME_TOKEN, String.class, UUID.class);
            final Duration duration = R2MO.toDuration(jwt.getExpiredAt());
            ofToken.configure(duration, this.security.getLimit().getToken());
            return ofToken;
        }, UserCache.NAME_TOKEN);
    }

    @Override
    public CacheAt<String, UUID> ofRefresh() {
        // 只有 JWT 才启用
        final ConfigSecurityJwt jwt = this.security.getJwt();
        if (Objects.isNull(jwt)) {
            throw new _501NotSupportException("[ R2MO ] 未启用 JWT 功能，无法使用 Refresh Token 缓存");
        }
        return (CacheAt<String, UUID>) CC_CACHE.pick(() -> {
            final CacheAt<String, UUID> ofToken =
                this.create(UserCache.NAME_REFRESH, String.class, UUID.class);
            final Duration duration = R2MO.toDuration(jwt.getRefreshAt());
            ofToken.configure(duration, this.security.getLimit().getToken());
            return ofToken;
        }, UserCache.NAME_REFRESH);
    }

    @Override
    public CacheAt<String, String> ofAuthorize(final TypeID type) {
        Objects.requireNonNull(type, "[ R2MO ] 授权码类型不可为空！");
        final String name = UserCache.NAME_AUTHORIZE + "@" + type.name();
        return (CacheAt<String, String>) CC_CACHE.pick(() -> {
            final CacheAt<String, String> ofAuthorize =
                this.create(name, String.class, String.class);
            final Kv<Long, Duration> limit = this.findLimit(type);
            ofAuthorize.configure(limit.value(), limit.key());
            return ofAuthorize;
        }, name);
    }

    private Kv<Long, Duration> findLimit(final TypeID type) {
        // 特殊检索，图片验证码
        Duration duration = null;
        if (type.name().startsWith(TypeID.CAPTCHA.name())) {
            if (!this.security.isCaptcha()) {
                throw new _501NotSupportException("[ R2MO ] 未启用图片验证码功能！");
            }
            final ConfigSecurityCaptcha captcha = this.security.getCaptcha();
            duration = Duration.ofSeconds(captcha.getExpiredAt());
        }

        final ConfigSecurityLimit limit = this.security.getLimit();
        // 提取其他类型的限制配置
        if (Objects.isNull(duration)) {
            final Kv<Long, Duration> kv = limit.getLimit(type);
            if (Objects.nonNull(kv)) {
                // 如果可找到对应的限制类型，则直接返回
                return kv;
            }
            // 找不到对应的限制类型，则使用全局的默认配置，默认 60 秒
            duration = Duration.ofSeconds(60);
        }
        // 返回默认限制
        final long size = limit.getAuthorize();
        return Kv.create(size, duration);
    }

    protected <K, V> CacheAt<K, V> create(final String name, final Class<K> clazzK, final Class<V> clazzV) {
        return new CacheAtEhcache<>(name, clazzK, clazzV);
    }
}
