package io.r2mo.spring.weco;

import io.r2mo.jaas.auth.CaptchaArgs;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.xync.weco.WeCoSession;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * WeCoSession 的 Spring Cache 实现
 * <p>依赖 io.r2mo.spring.cache.SpringCacheManager 的特性，通过命名规则设置 TTL。</p>
 *
 * @author lang
 */
@Component
@SPID
public class WeCoSessionSpring implements WeCoSession {

    @Override
    public void save(final String cacheKey, final String statusOr, final Duration expiredAt) {
        final CaptchaArgs captchaArgs = CaptchaArgs.of(TypeLogin.ID_WECHAT, expiredAt);
        final Kv<String, String> generated = Kv.create(cacheKey, statusOr);
        UserCache.of().authorize(generated, captchaArgs);
    }

    @Override
    public String get(final String cacheKey, final Duration expiredAt) {
        final CaptchaArgs captchaArgs = CaptchaArgs.of(TypeLogin.ID_WECHAT, expiredAt);
        return UserCache.of().authorize(cacheKey, captchaArgs);
    }
}
