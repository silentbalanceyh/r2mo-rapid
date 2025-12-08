package io.r2mo.spring.security.email;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.enums.TypeLogin;

/**
 * @author lang : 2025-12-08
 */
public class EmailLoginRequest extends LoginRequest {
    @Override
    public TypeLogin type() {
        return TypeLogin.EMAIL;
    }
}
