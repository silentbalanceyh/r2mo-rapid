package io.r2mo.spring.security.jwt;

import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.json.JObject;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-11-12
 */
@Data
public class JwtLoginResponse implements Serializable {
    private String token;
    private String tokenType = "Bearer";
    private long expiresIn = 86400;
    private String refreshToken;
    private JObject user;

    public JwtLoginResponse(final UserAt userAt) {
        
    }
}
