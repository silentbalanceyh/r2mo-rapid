package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.typed.common.Kv;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author lang : 2025-11-11
 */
@Service
public class AuthServiceManager implements AuthService {

    private final ServiceFactory manager;

    public AuthServiceManager() {
        this.manager = ServiceFactory.of();
    }

    @Override
    public String authorize(final LoginRequest loginRequest) {
        // 根据 type() 查找匹配的
        final ServicePreAuth service = this.manager.authorizeProvider(loginRequest.type());
        final Kv<String, String> generated = service.authorize(loginRequest.getId());
        // 缓存处理 - 60s 有效期，特殊配置后边来处理（比如5分钟验证码）
        final UserCache cache = UserCache.of();
        cache.authorize(generated, loginRequest.type());
        return generated.value();
    }

    @Override
    public UserAt login(final LoginRequest loginRequest) throws AuthenticationException {
        final ServiceUserAt service = this.manager.userProvider(loginRequest.type());

        final UserAt user = service.loadLogged(loginRequest);
        if (Objects.isNull(user) || !user.isOk()) {
            throw new AuthenticationException("[ R2MO ] 用户登录异常：id = " + loginRequest.getId()) {
            };
        }
        return user;
    }
}
