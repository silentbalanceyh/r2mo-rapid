package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.enums.TypeID;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserSession;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 基类实现，为了让开发人员可以更快速地实现自己的用户加载逻辑
 *
 * @author lang : 2025-11-12
 */
@Slf4j
public abstract class ServiceUserAtBase implements ServiceUserAt {
    @Override
    public UserAt loadLogged(final LoginRequest request) {
        final TypeID type = request.type();
        if (type != this.idType()) {
            throw new IllegalArgumentException("[ R2MO ] 加载用户信息的 ID 类型错误：期望类型 = " + this.idType() + "，实际类型 = " + type);
        }
        // 此处不加载员工数据，员工数据的选择交给 UserContext 来处理
        final String identifier = request.getId();
        log.info("[ R2MO ] 登录加载：identifier = {} / provider = {}", identifier, this.getClass().getName());
        return this.findUser(request.getId());
    }

    public abstract UserAt findUser(final String id);

    public abstract TypeID idType();

    protected UserAt ofUserAt(final MSUser user) {
        return UserSession.of().userAt(user);
    }

    protected UserAt ofUserAt(final MSUser user, final MSEmployee employee) {
        return UserSession.of().userAt(this.ofUserAt(user), employee);
    }

    @Override
    public UserAt loadLogged(final String identifier) {
        // 缓存中加载账号数据
        final UserAt cached = UserSession.of().find(identifier);
        if (Objects.nonNull(cached)) {
            return cached;
        }
        log.info("[ R2MO ] 验证加载：identifier = {} / provider = {}", identifier, this.getClass().getName());
        final UserAt userAt = this.findUser(identifier);
        // 查找内容写缓存
        UserSession.of().userAt(userAt);
        return userAt;
    }
}
