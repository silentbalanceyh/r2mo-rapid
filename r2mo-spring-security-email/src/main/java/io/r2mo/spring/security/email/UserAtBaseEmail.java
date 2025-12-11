package io.r2mo.spring.security.email;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.ServiceUserAtBase;
import io.r2mo.typed.enums.TypeLogin;

/**
 * Email 验证抽象类
 *
 * @author lang : 2025-12-08
 */
public abstract class UserAtBaseEmail extends ServiceUserAtBase {

    @Override
    public TypeLogin loginType() {
        return TypeLogin.EMAIL;
    }

    @Override
    public boolean isMatched(final LoginRequest request, final UserAt userAt) {
        final EmailCaptchaConfig config = SpringUtil.getBean(EmailCaptchaConfig.class);
        return this.isMatched(request, userAt, config.expiredAt());
    }
}
