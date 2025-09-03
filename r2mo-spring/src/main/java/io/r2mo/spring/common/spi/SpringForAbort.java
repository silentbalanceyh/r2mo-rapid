package io.r2mo.spring.common.spi;

import io.r2mo.base.web.FailOr;
import io.r2mo.base.web.ForAbort;
import io.r2mo.spring.common.exception.FailOrSpring;

/**
 * @author lang : 2025-09-03
 */
class SpringForAbort implements ForAbort {

    @Override
    public FailOr failContainer() {
        return ForAbort.CC_SKELETON.pick(FailOrSpring::new, FailOrSpring.class.getName());
    }
}
