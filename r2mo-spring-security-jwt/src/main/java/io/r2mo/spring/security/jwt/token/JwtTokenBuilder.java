package io.r2mo.spring.security.jwt.token;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderBase;
import io.r2mo.typed.webflow.Akka;
import io.r2mo.typed.webflow.AkkaOf;

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
    public Akka<String> accessOf(final String token) {
        if (!this.generator.tokenValidate(token)) {
            return null;
        }
        return AkkaOf.of(this.generator.tokenSubject(token));
    }

    @Override
    public Akka<String> accessOf(final UserAt userAt) {
        final MSUser logged = this.ensureAuthorized(userAt);
        return AkkaOf.of(this.generator.tokenGenerate(userAt.id().toString(), logged.token()));
    }

    @Override
    public Akka<String> refreshOf(final UserAt userAt) {
        return AkkaOf.of(this.refresher.tokenGenerate(userAt.id().toString()));
    }
}
