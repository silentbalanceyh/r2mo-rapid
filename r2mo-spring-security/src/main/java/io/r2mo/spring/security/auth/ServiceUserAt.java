package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;

/**
 * 根据 {@link LoginRequest} 执行不同的加载逻辑，加载用户信息
 *
 * @author lang : 2025-11-11
 */
public interface ServiceUserAt {

    UserAt loadLogged(LoginRequest request);

    UserAt loadLogged(String identifier);
}
