package io.r2mo.spring.security.jwt;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityJwt;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @author lang : 2025-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JwtLoginResponse extends LoginResponse {
    private static final TokenBuilderManager MANAGER = TokenBuilderManager.of();
    private static final ConfigSecurity CONFIG = SpringUtil.getBean(ConfigSecurity.class);
    private String tokenType = "Bearer";
    private long expiresIn;

    public JwtLoginResponse(final UserAt userAt) {
        super(userAt);
        // 覆盖父类的 id 字段，不返回用户 ID 信息
        this.setId(null);
        // 获取 JWT 配置中的过期时间
        final ConfigSecurityJwt jwt = CONFIG.getJwt();
        Objects.requireNonNull(jwt, "[ R2MO ] JWT 配置不可为空！");
        this.expiresIn = jwt.msExpiredAt() / 1000;
    }

    @Override
    public String getToken(final UserAt userAt) {
        return MANAGER.getOrCreate(TokenType.JWT).accessOf(userAt).v();
    }

    @Override
    public String getRefreshToken(final UserAt userAt) {
        return MANAGER.getOrCreate(TokenType.JWT).refreshOf(userAt).v();
    }
}
