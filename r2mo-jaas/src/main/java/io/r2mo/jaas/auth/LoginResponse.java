package io.r2mo.jaas.auth;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lang : 2025-11-10
 */
@Data
public abstract class LoginResponse implements Serializable {
    private String token;
    private String refreshToken;
    private UUID id;
    private String username;
    private long expiredAt;
}
