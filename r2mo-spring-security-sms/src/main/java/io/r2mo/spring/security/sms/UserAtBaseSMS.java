package io.r2mo.spring.security.sms;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.ServiceUserAtBase;
import io.r2mo.typed.enums.TypeLogin;

/**
 * @author lang : 2025-12-08
 */
public abstract class UserAtBaseSMS extends ServiceUserAtBase {

    @Override
    public TypeLogin loginType() {
        return TypeLogin.SMS;
    }

    @Override
    public boolean isMatched(final LoginRequest request, final UserAt userAt) {
        final SmsCaptchaConfig config = SpringUtil.getBean(SmsCaptchaConfig.class);
        return this.isMatched(request, userAt, config.expiredAt());
    }
}
