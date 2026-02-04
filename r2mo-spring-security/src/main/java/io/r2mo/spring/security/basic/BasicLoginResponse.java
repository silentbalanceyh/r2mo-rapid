package io.r2mo.spring.security.basic;

import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
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
        // 该方法已被覆盖，不会调用父类方法
        return TokenBuilderManager.of().getOrCreate(TokenType.AES)
            .accessOf(user).v();
    }
}
