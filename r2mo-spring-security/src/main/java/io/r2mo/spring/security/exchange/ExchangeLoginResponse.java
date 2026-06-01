package io.r2mo.spring.security.exchange;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.auth.LoginResponse;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityExchange;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * 第三方集成 Token 交换响应
 * 根据配置的 tokenType（AES/JWT）生成对应的 access token
 *
 * @author lang : 2026-05-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExchangeLoginResponse extends LoginResponse {
    private String clientId;

    public ExchangeLoginResponse(final UserAt userAt) {
        super(userAt);
        this.clientId = userAt.logged().getUsername();
    }

    @Override
    public String getToken(final UserAt user) {
        final ConfigSecurity config = SpringUtil.getBean(ConfigSecurity.class);
        final ConfigSecurityExchange exchange = config.getExchange();
        final TokenType tokenType = Objects.isNull(exchange) ? TokenType.AES : exchange.getTokenType();
        return TokenBuilderManager.of().getOrCreate(tokenType)
            .accessOf(user).get();
    }

    @Override
    public String getRefreshToken(final UserAt user) {
        final ConfigSecurity config = SpringUtil.getBean(ConfigSecurity.class);
        final ConfigSecurityExchange exchange = config.getExchange();
        if (Objects.isNull(exchange)) {
            return null;
        }
        final TokenType tokenType = exchange.getTokenType();
        return TokenBuilderManager.of().getOrCreate(tokenType)
            .refreshOf(user).get();
    }
}
