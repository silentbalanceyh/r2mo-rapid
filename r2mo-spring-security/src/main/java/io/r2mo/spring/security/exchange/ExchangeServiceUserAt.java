package io.r2mo.spring.security.exchange;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.ServiceUserAtBase;
import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.config.ConfigSecurityExchange;
import io.r2mo.spring.security.exception._80243Exception401UserNotFound;
import io.r2mo.typed.enums.TypeLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

/**
 * 第三方集成 Token 交换的认证提供者
 * Bean 名称 "UserAt/EXCHANGE" 由 ServiceFactory 查找
 *
 * @author lang : 2026-05-06
 */
@Slf4j
@Component("UserAt/EXCHANGE")
public class ExchangeServiceUserAt extends ServiceUserAtBase {

    public ExchangeServiceUserAt() {
        super(TypeLogin.EXCHANGE);
    }

    @Override
    public UserAt findUser(final String clientId) {
        final ConfigSecurityExchange exchange = this.exchangeConfig();
        if (Objects.isNull(exchange) || !exchange.isEnabled()) {
            throw new _80243Exception401UserNotFound.Unauthorized("第三方集成未启用", clientId);
        }
        if (!exchange.getClientId().equals(clientId)) {
            throw new _80243Exception401UserNotFound.Unauthorized("客户端标识不存在", clientId);
        }
        log.info("[ R2MO ] 第三方 Token 交换: clientId = {}", clientId);
        final MSUser user = new MSUser();
        user.setId(UUID.nameUUIDFromBytes(clientId.getBytes()));
        user.setUsername(clientId);
        return this.userAtEphemeral(user);
    }

    /**
     * 直接比较 clientSecret，不走 PasswordEncoder
     * LoginRequest.credential 由 ExchangeLoginRequest.setClientSecret -> setCredential 设置
     */
    @Override
    public boolean isMatched(final LoginRequest request, final UserAt userAt) {
        final ConfigSecurityExchange exchange = this.exchangeConfig();
        if (Objects.isNull(exchange)) {
            return false;
        }
        return exchange.getClientSecret().equals(request.getCredential());
    }

    private ConfigSecurityExchange exchangeConfig() {
        final ConfigSecurity config = cn.hutool.extra.spring.SpringUtil.getBean(ConfigSecurity.class);
        return config.getExchange();
    }
}
