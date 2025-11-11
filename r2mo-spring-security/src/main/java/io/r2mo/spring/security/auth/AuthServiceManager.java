package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.auth.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lang : 2025-11-11
 */
@Service
public class AuthServiceManager implements AuthService {
    private final List<AuthProvider> authServices;

    public AuthServiceManager(@Autowired final List<AuthProvider> authServices) {
        this.authServices = authServices;
    }

    @Override
    public boolean authorize(final LoginRequest loginRequest) {
        for (final AuthProvider service : this.authServices) {
            if (service.supports(loginRequest)) {
                return service.authorize(loginRequest);
            }
        }
        return true;
    }

    @Override
    public LoginResponse login(final LoginRequest loginRequest) throws AuthenticationException {
        for (final AuthProvider service : this.authServices) {
            if (service.supports(loginRequest)) {
                return service.login(loginRequest);
            }
        }
        throw new AuthenticationException("[R2MO] 未找到合适的认证服务") {
        };
    }

    @Override
    public boolean supports(final LoginRequest loginRequest) {
        return this.authServices.stream().anyMatch(service -> service.supports(loginRequest));
    }
}
