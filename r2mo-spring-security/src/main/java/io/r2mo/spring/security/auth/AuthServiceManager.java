package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.spring.security.exception._80243Exception401UserNotFound;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.enums.TypeLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

/**
 * @author lang : 2025-11-11
 */
@Service
@Slf4j
public class AuthServiceManager implements AuthService {

    private final ServiceFactory manager;

    public AuthServiceManager() {
        this.manager = ServiceFactory.of();
    }

    @Override
    public String authorize(final LoginRequest loginRequest, final Duration duration) {
        // 根据 type() 查找匹配的
        final ServicePreAuth service = this.manager.authorizeProvider(loginRequest.type());
        final Kv<String, String> generated = service.authorize(loginRequest.getId());
        // 缓存处理 - 60s 有效期，特殊配置后边来处理（比如5分钟验证码）
        final UserCache cache = UserCache.of();
        final CaptchaArgs captchaArgs = CaptchaArgs.of(loginRequest.type(), duration);
        cache.authorize(generated, captchaArgs);
        return generated.value();
    }

    @Override
    public UserAt login(final LoginRequest loginRequest) throws AuthenticationException {
        final TypeLogin type = loginRequest.type();
        final ServiceUserAt service = this.manager.userProvider(type);

        final String identifier = loginRequest.getId();
        final UserAt user = service.loadLogged(loginRequest);
        if (Objects.isNull(user) || !user.isOk()) {
            log.error("[ R2MO ] 用户不存在或被禁用，登录标识：{}", identifier);
            throw new _80243Exception401UserNotFound.Unauthorized("用户不存在！", identifier);
        }
        return user;
    }
}
