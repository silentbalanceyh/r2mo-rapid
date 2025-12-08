package io.r2mo.spring.security.email;

import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.spring.security.auth.ServiceUserAtBase;
import io.r2mo.typed.enums.TypeLogin;

import java.util.Objects;

/**
 * Email 验证抽象类
 *
 * @author lang : 2025-12-08
 */
public abstract class EmailServiceUserAtBase extends ServiceUserAtBase {

    @Override
    public TypeLogin loginType() {
        return TypeLogin.EMAIL;
    }

    @Override
    public boolean isMatched(final LoginRequest request, final UserAt userAt) {
        final EmailCaptchaConfig config = SpringUtil.getBean(EmailCaptchaConfig.class);
        final CaptchaArgs captchaArgs = CaptchaArgs.of(this.loginType(), config.expiredAt());
        final String id = request.getId();
        final String codeStored = UserCache.of().authorize(id, captchaArgs);
        if (Objects.isNull(codeStored)) {
            return false;
        }
        final String code = request.getCredential();
        return codeStored.equals(code);
    }
}
