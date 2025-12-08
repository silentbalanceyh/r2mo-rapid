package io.r2mo.spring.security.auth;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.enums.TypeLogin;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserSession;
import io.r2mo.spring.security.exception._80204Exception401PasswordNotMatch;
import io.r2mo.spring.security.exception._80244Exception401LoginTypeWrong;
import io.r2mo.spring.security.exception._80250Exception401Unauthorized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

/**
 * 基类实现，为了让开发人员可以更快速地实现自己的用户加载逻辑
 *
 * @author lang : 2025-11-12
 */
@Slf4j
public abstract class ServiceUserAtBase implements ServiceUserAt {

    protected final PasswordEncoder encoder;

    public ServiceUserAtBase() {
        this.encoder = SpringUtil.getBean(PasswordEncoder.class);
    }

    @Override
    public UserAt loadLogged(final LoginRequest request) {
        final TypeLogin type = request.type();
        if (type != this.loginType()) {
            throw new _80244Exception401LoginTypeWrong.Unauthorized("[ R2MO ] 加载用户信息的 ID 类型错误：期望类型 = " + this.loginType() + "，实际类型 = " + type);
        }
        // 此处不加载员工数据，员工数据的选择交给 UserContext 来处理
        final String identifier = request.getId();
        log.info("[ R2MO ] 登录加载：identifier = {} / provider = {}", identifier, this.getClass().getName());
        final UserAt userAt = this.findUser(request.getId());
        final boolean isMatch = this.isMatched(request, userAt);
        if (!isMatch) {
            throw new _80204Exception401PasswordNotMatch.Unauthorized("密码错误！", identifier);
        }
        return userAt;
    }

    /**
     * 常用的密码检查
     *
     * @param request 登录请求
     * @param userAt  用户密码
     *
     * @return 是否匹配
     */
    public boolean isMatched(final LoginRequest request, final UserAt userAt) {
        final String credential = request.getCredential();
        final MSUser user = userAt.logged();
        if (Objects.isNull(user)) {
            return false;
        }
        return Objects.requireNonNull(this.encoder).matches(credential, user.getPassword());
    }

    public abstract UserAt findUser(String id);

    public abstract TypeLogin loginType();

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
        try {
            UserSession.of().userAt(userAt);
            return userAt;
        } catch (final Throwable ex) {
            // 包装后抛出，才能触发 Handler
            throw new _80250Exception401Unauthorized.Unauthorized(ex.getMessage(), identifier);
        }
    }
}
