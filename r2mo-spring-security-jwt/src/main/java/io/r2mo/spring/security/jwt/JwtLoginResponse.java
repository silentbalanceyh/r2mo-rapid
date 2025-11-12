package io.r2mo.spring.security.jwt;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.enums.TypeToken;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spi.SPI;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityJwt;
import io.r2mo.spring.security.token.TokenBuilder;
import io.r2mo.spring.security.token.TokenBuilderManager;
import io.r2mo.typed.json.JObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author lang : 2025-11-12
 */
@Data
public class JwtLoginResponse implements Serializable {
    private String token;
    private String tokenType = "Bearer";
    private long expiresIn;
    private String refreshToken;
    private JObject user;

    private static final TokenBuilderManager MANAGER = TokenBuilderManager.of();
    private static final ConfigSecurity CONFIG = SpringUtil.getBean(ConfigSecurity.class);

    public JwtLoginResponse(final UserAt userAt) {
        // 获取 JWT 配置中的过期时间
        final ConfigSecurityJwt jwt = CONFIG.getJwt();
        Objects.requireNonNull(jwt, "[ R2MO ] JWT 配置不可为空！");
        // 需要构造到 token 中的信息，从数据中提取
        final TokenBuilder generator = MANAGER.getOrCreate(TypeToken.JWT);
        this.token = generator.build(userAt);
        // 需要构造到 refresh token 中的信息，从数据中提取
        final TokenBuilder refresher = MANAGER.getOrCreate(TypeToken.JWT_REFRESH);
        this.token = refresher.build(userAt);
        this.expiresIn = jwt.msExpiredAt() / 1000;
        // 构造用户基础信息
        final MSUser logged = userAt.logged();
        final JObject userJ = SPI.J();
        this.user = userJ.put(logged.token());
    }
}
