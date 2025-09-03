package io.r2mo.spring.common.spi;

import io.r2mo.base.web.ForFailure;
import io.r2mo.base.web.ForLocale;
import io.r2mo.base.web.ForStatus;
import io.r2mo.spi.FactoryWeb;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-09-02
 */
public class SpringFactoryWeb implements FactoryWeb {
    private static final Cc<String, ForStatus> CCT_STATUS = Cc.openThread();
    private static final Cc<String, ForLocale> CCT_LOCALE = Cc.openThread();
    private static final Cc<String, ForFailure> CCT_FAILURE = Cc.openThread();

    @Override
    public ForStatus ofStatus() {
        return CCT_STATUS.pick(SpringForStatus::new, SpringForStatus.class.getName());
    }

    @Override
    public ForLocale ofLocale() {
        return CCT_LOCALE.pick(SpringForLocale::new, SpringForLocale.class.getName());
    }

    @Override
    public ForFailure ofFailure() {
        return CCT_FAILURE.pick(SpringForFailure::new, SpringForFailure.class.getName());
    }
}
