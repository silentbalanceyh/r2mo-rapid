package io.r2mo.spring.security.jwt.token;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.token.TokenBuilder;
import io.r2mo.typed.exception.web._401UnauthorizedException;

import java.util.Objects;

/**
 * @author lang : 2025-11-12
 */
public class TokenBuilderJwt implements TokenBuilder {

    private final JwtTokenGenerator generator;

    public TokenBuilderJwt() {
        this.generator = SpringUtil.getBean(JwtTokenGenerator.class);
    }

    @Override
    public String build(final UserAt userAt) {
        final MSUser logged = userAt.logged();
        if (Objects.isNull(logged)) {
            throw new _401UnauthorizedException("[ R2MO ] 无法构造 JWT Token，登录用户信息缺失");
        }
        return this.generator.tokenGenerate(userAt.id().toString(), logged.token());
    }
}
