package io.r2mo.spring.security.exchange;

import io.r2mo.spring.security.config.ConfigSecurity;
import io.r2mo.spring.security.extension.RequestUri;

import java.util.Set;

/**
 * 第三方集成 Token 交换端点 URI 注册
 */
public class ExchangeRequestUri implements RequestUri {
    @Override
    public Set<String> ignores(final ConfigSecurity security) {
        if (!security.isExchange()) {
            return Set.of();
        }
        return Set.of("/auth/exchange:POST");
    }

    @Override
    public Set<String> noCache(final ConfigSecurity security) {
        if (!security.isExchange()) {
            return Set.of();
        }
        return Set.of("/auth/exchange");
    }

    @Override
    public Set<String> noRedirect(final ConfigSecurity security) {
        return this.noCache(security);
    }
}
