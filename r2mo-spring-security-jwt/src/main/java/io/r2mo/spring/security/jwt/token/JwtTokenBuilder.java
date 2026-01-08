package io.r2mo.spring.security.jwt.token;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderBase;

/**
 * @author lang : 2025-11-12
 */
public class JwtTokenBuilder extends TokenBuilderBase {

    private final JwtTokenGenerator generator;
    private final JwtTokenRefresher refresher;

    public JwtTokenBuilder() {
        this.generator = SpringUtil.getBean(JwtTokenGenerator.class);
        this.refresher = SpringUtil.getBean(JwtTokenRefresher.class);
    }

    @Override
    public String accessOf(final String token) {
        if (!this.generator.tokenValidate(token)) {
            return null;
        }
        return this.generator.tokenSubject(token);
    }

    @Override
    public String accessOf(final UserAt userAt) {
        final MSUser logged = this.ensureAuthorized(userAt);
        return this.generator.tokenGenerate(userAt.id().toString(), logged.token());
    }

    @Override
    public String refreshOf(final UserAt userAt) {
        return this.refresher.tokenGenerate(userAt.id().toString());
    }
}
