package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.executor.ExecutorCache;
import io.r2mo.spring.security.auth.executor.ExecutorManager;
import io.r2mo.spring.security.auth.executor.ServicePreAuth;
import io.r2mo.spring.security.auth.executor.ServiceUserAt;
import io.r2mo.typed.common.Kv;
import org.ehcache.Cache;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author lang : 2025-11-11
 */
@Service
public class AuthServiceManager implements AuthService {

    private final ExecutorManager manager;
    private final ExecutorCache cache;

    public AuthServiceManager() {
        this.manager = ExecutorManager.of();
        this.cache = ExecutorCache.of();
    }

    @Override
    public String authorize(final LoginRequest loginRequest) {
        // 根据 type() 查找匹配的
        final ServicePreAuth service = this.manager.authorizeProvider(loginRequest.type());
        final Kv<String, String> generated = service.authorize(loginRequest.getId());
        // 缓存处理 - 60s 有效期，特殊配置后边来处理（比如5分钟验证码）
        final Cache<String, String> cache = this.cache.getOrCreate(loginRequest.type());
        cache.put(generated.key(), generated.value());
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
