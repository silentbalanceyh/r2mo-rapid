package io.r2mo.spring.security.jwt.token;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.token.TokenBuilder;

/**
 * @author lang : 2025-11-12
 */
public class JwtTokenBuilderRefresh implements TokenBuilder {
    private final JwtTokenRefresher refresher;

    public JwtTokenBuilderRefresh() {
        this.refresher = SpringUtil.getBean(JwtTokenRefresher.class);
    }

    @Override
    public String build(final UserAt userAt) {
        return this.refresher.tokenGenerate(userAt.id().toString());
    }
}
