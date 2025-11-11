package io.r2mo.spring.security.auth.basic;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.enums.UserIDType;
import io.r2mo.spring.security.auth.AuthProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

/**
 * @author lang : 2025-11-11
 */
@Service
public class BasicAuthService implements AuthProvider {
    @Override
    public LoginResponse login(final LoginRequest loginRequest) throws AuthenticationException {

        return null;
    }

    @Override
    public final boolean supports(final LoginRequest loginRequest) {
        return UserIDType.PASSWORD == loginRequest.getIdType();
    }
}

