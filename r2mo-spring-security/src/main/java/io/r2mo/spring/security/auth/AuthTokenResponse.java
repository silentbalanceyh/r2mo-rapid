package io.r2mo.spring.security.auth;

import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用，OTP、邮箱、短信等几种模式都可支持的 Token 响应
 *
 * @author lang : 2025-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthTokenResponse extends LoginResponse {
    public AuthTokenResponse(final UserAt userAt) {
        super(userAt);
    }

    @Override
    public String getToken(final UserAt user) {
        // 该方法已被覆盖，不会调用
        return TokenBuilderManager.of().getOrCreate(TokenType.JWT).accessOf(user);
    }
}
