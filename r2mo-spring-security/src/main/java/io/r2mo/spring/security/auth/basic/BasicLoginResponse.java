package io.r2mo.spring.security.auth.basic;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import lombok.Data;

import java.io.Serializable;
import java.util.Base64;
import java.util.UUID;

/**
 * @author lang : 2025-11-11
 */
@Data
public class BasicLoginResponse implements Serializable {
    private String token;
    private UUID id;
    private String username;

    public BasicLoginResponse(final UserAt userAt) {
        final MSUser user = userAt.logged();
        this.id = user.getId();
        this.username = user.getUsername();
        final String token = user.getUsername() + ":" + user.getPassword();
        this.token = Base64.getEncoder().encodeToString(token.getBytes());
    }
}
