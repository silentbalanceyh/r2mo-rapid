package io.r2mo.jaas.session;

import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 用户会话管理，根据会话本身数据执行相关管理，会设置缓存时间长度
 * <pre>
 *     1. {@link UserAt} 当前登录账号
 *     2. {@link UserContext} 当前登录账号的上下文信息，多组织，{@link MSEmployee} x N
 * </pre>
 * 流程设计
 * <pre>
 *     第一阶段：
 *     1. 执行认证方法，登录成功构造 {@link MSUser} 信息，然后初始化 {@link UserContext}
 *     2. 提取所有合法的 {@link MSEmployee} 信息
 *     3. 初始化登录上下文
 *
 *     第二阶段：
 *     1. 用户选择（若只有一个自动选择）
 *     2. 筛选员工信息 {@link MSEmployee}
 *     3. 初始化组织内登录会话信息
 * </pre>
 *
 * @author lang : 2025-11-10
 */
@Slf4j
public class UserSession {
    private static final String NAME_AT = "CACHE_USER_AT";
    private static final String NAME_CONTEXT = "CACHE_USER_CONTEXT";

    private static UserSession INSTANCE;
    private static CacheManager USER_AT;
    private static CacheManager USER_CONTEXT;

    private UserSession() {
    }

    public UserContext context(final MSUser user, final List<MSEmployee> employee) {
        // 构造
        final UserContextImpl context = new UserContextImpl(user.getId());
        context.logged(user).employee(employee);

        // 添加
        this.context(context);
        log.info("[ R2MO ] 初始化账号上下文：{} / id = {}", user.getUsername(), user.getId());
        return context;
    }

    public UserAt user(final MSUser user, final MSEmployee employee) {
        // 构造
        final UserAtLogged userAt = new UserAtLogged(user.getId());
        userAt.logged(user).employee(employee);
        // 添加
        this.user(userAt);
        log.info("[ R2MO ] 登录账号：{} / 员工工号 = {}", user.getUsername(), employee.getWorkNumber());
        return userAt;
    }

    public void logout(final MSUser user) {
        this.userCache().remove(user.getId());
        this.contextCache().remove(user.getId());
        log.info("[ R2MO ] 登出账号：{}", user.getUsername());
    }

    @SuppressWarnings("all")
    private UserSession user(final UserAt userAt) {
        userCache().put(userAt.id(), userAt);
        return this;
    }

    public UserAt user(final UUID id) {
        return this.userCache().get(id);
    }

    @SuppressWarnings("all")
    private UserSession context(final UserContext context) {
        contextCache().put(context.id(), context);
        return this;
    }

    public UserContext context(final UUID id) {
        return this.contextCache().get(id);
    }

    private Cache<UUID, UserAt> userCache() {
        return USER_AT.getCache(NAME_AT, UUID.class, UserAt.class);
    }

    private Cache<UUID, UserContext> contextCache() {
        return USER_CONTEXT.getCache(NAME_CONTEXT, UUID.class, UserContext.class);
    }

    // --------------- 上边为提取值的 API ---------------
    public static UserSession of() {
        return of(120);
    }

    public static UserSession of(final int mins) {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new UserSession();
        }
        return INSTANCE.configure(mins);
    }

    private UserSession configure(final int mins) {
        // 默认 120 分钟
        this.buildUserAt(mins);

        this.buildUserContext(mins);

        return this;
    }

    private void buildUserContext(final int mins) {
        if (Objects.nonNull(USER_CONTEXT)) {
            return;
        }

        // 当前登录账号
        USER_CONTEXT = configure(NAME_CONTEXT, mins, UserContext.class);
    }

    private void buildUserAt(final int mins) {
        if (Objects.nonNull(USER_AT)) {
            return;
        }

        // 当前登录账号
        USER_AT = configure(NAME_AT, mins, UserAt.class);
    }

    // ehcache
    private static CacheManager configure(final String name, final int mins, final Class<?> clazzT) {
        return CacheManagerBuilder.newCacheManagerBuilder()
            .withCache(name, CacheConfigurationBuilder
                .newCacheConfigurationBuilder(
                    UUID.class, clazzT,
                    ResourcePoolsBuilder.heap(20000)   // 默认缓存 20000 个对象
                )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(
                    Duration.of(mins, ChronoUnit.MINUTES)
                ))
            ).build(true);
    }
}
