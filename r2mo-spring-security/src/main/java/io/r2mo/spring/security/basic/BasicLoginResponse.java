package io.r2mo.spring.security.basic;

import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.enums.TypeToken;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.token.TokenBuilderManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lang : 2025-11-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BasicLoginResponse extends LoginResponse {
    private String username;

    public BasicLoginResponse(final UserAt userAt) {
        super(userAt);
        final MSUser user = userAt.logged();
        this.username = user.getUsername();
    }

    @Override
    public String getToken(final UserAt user) {
        // 该方法已被覆盖，不会调用
        return TokenBuilderManager.of().getOrCreate(TypeToken.BASIC).build(user);
    }
}
