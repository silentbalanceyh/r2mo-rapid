package io.r2mo.spring.security.auth;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.spring.security.config.ConfigSecurity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * 通用，OTP、邮箱、短信等几种模式都可支持的 Token 响应
 *
 * @author lang : 2025-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthTokenResponse extends LoginResponse {
    private static ConfigSecurity CONFIG;

    public AuthTokenResponse(final UserAt userAt) {
        super(userAt);
    }

    private TokenType determineTokenType() {
        if (Objects.isNull(CONFIG)) {
            CONFIG = SpringUtil.getBean(ConfigSecurity.class);
        }
        // 根据配置决定使用哪种 Token 类型
        // 这里可以根据实际需求进行更复杂的逻辑判断
        return CONFIG.getTokenType();
    }

    @Override
    public String getToken(final UserAt user) {
        // 该方法已被覆盖，不会调用
        return TokenBuilderManager.of().getOrCreate(this.determineTokenType()).accessOf(user);
    }
}
