package io.r2mo.jaas.session;

import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

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

    private static UserSession INSTANCE;
    private final UserCache cache;

    private UserSession() {
        // 后期可通过其他手段进行扩展
        this.cache = UserCache.of();
    }

    public UserContext context(final MSUser user, final List<MSEmployee> employee) {
        // 构造
        final UserContextImpl context = new UserContextImpl(user.getId());
        context.logged(user).employee(employee);

        // 添加
        this.cache.login(context);

        log.info("[ R2MO ] 初始化账号上下文：{} / id = {}", user.getUsername(), user.getId());
        return context;
    }

    public UserAt userAt(final MSUser user) {
        // 构造
        final UserAtLogged userAt = new UserAtLogged(user.getId());
        userAt.logged(user);

        // 添加
        this.cache.login(userAt);

        log.info("[ R2MO ] 登录账号：{}", user.getUsername());
        return userAt;
    }

    public UserAt userAt(final UserAt userAt) {
        return this.userAt(userAt, null);
    }

    public UserAt userAt(final UserAt userAt, final MSEmployee employee) {
        if (!(userAt instanceof final UserAtLogged logged)) {
            throw new _401UnauthorizedException("[ R2MO ] 无效的用户会话信息，无法设置员工信息！");
        }
        logged.employee(employee);
        // 独占模式（更新）
        this.cache.login(logged);

        log.info("[ R2MO ] 切换账号员工工号：{} / 员工工号 = {}", logged.logged().getUsername(),
            Objects.isNull(employee) ? "N/A" : employee.getWorkNumber());
        return logged;
    }

    public void logout(final MSUser user) {
        this.cache.logout(user.getId());
        log.info("[ R2MO ] 登出账号：{}", user.getUsername());
    }

    public UserAt find(final String idOr) {
        return this.cache.find(idOr);
    }

    // --------------- 上边为提取值的 API ---------------
    public static UserSession of() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new UserSession();
        }
        return INSTANCE;
    }
}
