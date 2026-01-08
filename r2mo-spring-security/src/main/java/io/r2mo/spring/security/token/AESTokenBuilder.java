package io.r2mo.spring.security.token;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.token.TokenBuilderBase;

public class AESTokenBuilder extends TokenBuilderBase {
    private final AESTokenGenerator generator;
    private final AESTokenRefresher refresher;

    public AESTokenBuilder() {
        this.generator = SpringUtil.getBean(AESTokenGenerator.class);
        this.refresher = SpringUtil.getBean(AESTokenRefresher.class);
    }

    @Override
    public String accessOf(final UserAt userAt) {
        final MSUser logged = this.ensureAuthorized(userAt);
        return this.generator.tokenGenerate(userAt.id().toString(), logged.token());
    }

    @Override
    public String accessOf(final String token) {
        if (!this.generator.tokenValidate(token)) {
            return null;
        }
        return this.generator.tokenSubject(token);
    }

    @Override
    public String refreshOf(final UserAt userAt) {
        return this.refresher.tokenGenerate(userAt.id().toString());
    }
}
