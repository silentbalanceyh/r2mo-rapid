package io.r2mo.spring.security.extension.handler;

import io.r2mo.base.web.FailOr;
import io.r2mo.spring.common.exception.FailOrSpring;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-11-11
 */
class SecurityFailure {

    static Cc<String, FailOr> CCT_FAILURE = Cc.openThread();

    static FailOr of() {
        return CCT_FAILURE.pick(FailOrSpring::new, FailOrSpring.class.getName());
    }
}
