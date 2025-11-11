package io.r2mo.spring.security.auth.executor;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;

import java.util.Objects;

/**
 * 根据 {@link LoginRequest} 执行不同的加载逻辑，加载用户信息
 *
 * @author lang : 2025-11-11
 */
public interface UserAtService {

    MSUser loadUser(LoginRequest request);

    UserAt loadUser(MSUser user);

    default UserAt loadLogged(final LoginRequest request) {
        final MSUser user = this.loadUser(request);
        if (Objects.isNull(user)) {
            return null;
        }
        return this.loadUser(user);
    }
}
