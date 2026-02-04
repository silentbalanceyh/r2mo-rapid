package io.r2mo.spring.security.auth;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.jaas.session.UserSession;
import io.r2mo.spring.security.basic.BasicAuthenticateProvider;
import io.r2mo.spring.security.exception._80204Exception401PasswordNotMatch;
import io.r2mo.spring.security.exception._80244Exception401LoginTypeWrong;
import io.r2mo.typed.enums.TypeLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Objects;

/**
 * 基类实现，为了让开发人员可以更快速地实现自己的用户加载逻辑
 *
 * @author lang : 2025-11-12
 */
@Slf4j
public abstract class ServiceUserAtBase implements ServiceUserAt {

    protected final PasswordEncoder encoder;
    private TypeLogin typeLogin;

    public ServiceUserAtBase() {
        this.encoder = SpringUtil.getBean(PasswordEncoder.class);
    }

    protected ServiceUserAtBase(final TypeLogin typeLogin) {
        this();
        this.typeLogin = typeLogin;
    }

    @Override
    public UserAt loadLogged(final LoginRequest request) {
        final TypeLogin type = request.type();
        if (type != this.loginType()) {
            throw new _80244Exception401LoginTypeWrong.Unauthorized("[ R2MO ] 加载用户信息的 ID 类型错误：期望类型 = " + this.loginType() + "，实际类型 = " + type);
        }
        // 此处不加载员工数据，员工数据的选择交给 UserContext 来处理
        final String identifier = request.getId();
        log.info("[ R2MO ] 登录加载：id = `{}` / provider = `{}`", identifier, this.getClass().getName());
        final UserAt userAt = this.findUser(request.getId());
        final boolean isMatch = this.isMatched(request, userAt);
        if (!isMatch) {
            throw new _80204Exception401PasswordNotMatch.Unauthorized("密码错误！", identifier);
        }
        return userAt;
    }

    // --------------- 提供默认方法让子类重写

    /**
     * 常用的密码检查
     *
     * @param request 登录请求
     * @param userAt  用户密码
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

    public TypeLogin loginType() {
        return Objects.isNull(this.typeLogin) ? TypeLogin.PASSWORD : this.typeLogin;
    }
    // --------------- 子类必须实现的方法

    public abstract UserAt findUser(String id);

    protected UserAt userAtEphemeral(final MSUser user) {
        return UserSession.of().userAtEphemeral(user);
    }

    /**
     * 转移位置参考 {@link BasicAuthenticateProvider} 中的 58 行
     *
     * @param identifier 账号标识
     * @return 用户信息
     */
    @Override
    public UserAt loadLogged(final String identifier) {
        // 缓存中加载账号数据
        final UserAt cached = UserSession.of().find(identifier).get();
        // 追加账号的 isOk 判断，保证有内容
        if (Objects.nonNull(cached) && cached.isOk()) {
            return cached;
        }
        log.info("[ R2MO ] 验证加载：identifier = {} / provider = {}", identifier, this.getClass().getName());
        return this.findUser(identifier);
    }

    /**
     * 验证码模式专用的检查
     *
     * @param request  登录请求
     * @param userAt   存储的用户记录
     * @param duration 配置提取的时间（会影响 UserCache）
     * @return 是否匹配
     */
    protected boolean isMatched(final LoginRequest request, final UserAt userAt,
                                final Duration duration) {
        final CaptchaArgs captchaArgs = CaptchaArgs.of(this.loginType(), duration);
        final String id = request.getId();
        final String codeStored = UserCache.of().authorize(id, captchaArgs).get();
        if (Objects.isNull(codeStored)) {
            return false;
        }
        final String code = request.getCredential();
        return codeStored.equals(code);
    }
}
