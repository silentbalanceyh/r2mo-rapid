package io.r2mo.spring.security.extension.cache;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserContext;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityJwt;
import io.r2mo.spring.security.config.ConfigSecurityLimit;
import io.r2mo.typed.cc.CacheAt;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.enums.TypeLogin;
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

    private void ensureAuthorize(final CaptchaArgs configuration) {
        final TypeLogin type = configuration.type();
        if (TypeLogin.CAPTCHA == type && !this.security.isCaptcha()) {
            throw new _501NotSupportException("[ R2MO ] 未启用图片验证码功能！");
        }
    }

    /**
     * 验证码的设置一定是在传入之前就创建好了，确保设置过程中的唯一性，主要包含
     *
     * @param configuration 验证码配置
     *
     * @return 验证码缓存
     */
    @Override
    public CacheAt<String, String> ofAuthorize(final CaptchaArgs configuration) {
        // 验证配置信息
        this.ensureAuthorize(configuration);
        Objects.requireNonNull(configuration, "[ R2MO ] Captcha 配置不可为空！");


        // 此处新版的名称改成 configuration 的 hashCode，只要配置不相同，则 hashCode 肯定不同
        final String name = UserCache.NAME_AUTHORIZE + "@" + configuration.hashCode();
        return (CacheAt<String, String>) CC_CACHE.pick(() -> {
            final CacheAt<String, String> ofAuthorize = this.create(name, String.class, String.class);


            // 提取核心配置信息
            final ConfigSecurityLimit limit = this.security.getLimit();
            final long size = limit.getAuthorize();
            final Duration duration = configuration.duration();


            // 执行配置初始化完成
            ofAuthorize.configure(duration, size);
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