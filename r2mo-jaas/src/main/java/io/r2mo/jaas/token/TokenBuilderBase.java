package io.r2mo.jaas.token;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.typed.exception.web._401UnauthorizedException;

import java.util.Objects;
import java.util.function.Function;

public abstract class TokenBuilderBase implements TokenBuilder {

    protected String ensureAuthorized(final UserAt userAt, final Function<MSUser, String> fieldFn) {
        final MSUser logged = this.ensureAuthorized(userAt);
        final String fieldValue = fieldFn.apply(logged);
        if (StrUtil.isEmpty(fieldValue)) {
            throw new _401UnauthorizedException("[ R2MO ] 用户未授权，缺失必要信息");
        }
        return fieldValue;
    }

    protected MSUser ensureAuthorized(final UserAt userAt) {
        if (Objects.isNull(userAt)) {
            throw new _401UnauthorizedException("[ R2MO ] 用户信息缺失");
        }
        if (Objects.isNull(userAt.logged())) {
            throw new _401UnauthorizedException("[ R2MO ] 登录用户信息缺失");
        }
        return userAt.logged();
    }
}
