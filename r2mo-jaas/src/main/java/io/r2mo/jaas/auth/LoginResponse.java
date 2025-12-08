package io.r2mo.jaas.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.domain.extension.AbstractScope;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lang : 2025-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class LoginResponse extends AbstractScope implements Serializable {
    private String token;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID id;

    public LoginResponse() {
    }

    public LoginResponse(final UserAt userAt) {
        final MSUser user = userAt.logged();
        this.id = user.getId();
        this.token = this.getToken(userAt);
        this.refreshToken = this.getRefreshToken(userAt);
    }

    protected abstract String getToken(final UserAt user);

    protected String getRefreshToken(final UserAt user) {
        return null;
    }
}
