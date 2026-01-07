package io.r2mo.spring.security.jwt;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.AuthService;
import io.r2mo.typed.json.JObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lang : 2025-11-12
 */
@RestController
public class JwtLoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/jwt-login")
    public JwtLoginResponse loginJwt(final JObject requestJ) {
        final JwtLoginRequest request = new JwtLoginRequest(requestJ);
        final UserAt userAt = this.authService.login(request);
        return new JwtLoginResponse(userAt);
    }
}
