package io.r2mo.spring.security.weco;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.ServiceUserAtBase;
import io.r2mo.typed.enums.TypeLogin;

/**
 * @author lang : 2025-12-11
 */
public abstract class UserAtBaseWeChat extends ServiceUserAtBase {

    @Override
    public TypeLogin loginType() {
        return TypeLogin.ID_WECHAT;
    }

    @Override
    public boolean isMatched(LoginRequest request, final UserAt userAt) {
        return true;
    }
}
