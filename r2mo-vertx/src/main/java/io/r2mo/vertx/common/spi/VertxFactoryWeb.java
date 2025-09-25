package io.r2mo.vertx.common.spi;

import io.r2mo.base.web.ForAbort;
import io.r2mo.base.web.ForLocale;
import io.r2mo.base.web.ForStatus;
import io.r2mo.base.web.i18n.ForLocaleCommon;
import io.r2mo.spi.FactoryWeb;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._501NotSupportException;

/**
 * @author lang : 2025-09-25
 */
public class VertxFactoryWeb implements FactoryWeb {
    private static final Cc<String, ForStatus> CCT_STATUS = Cc.openThread();
    private static final Cc<String, ForLocale> CCT_LOCALE = Cc.openThread();

    @Override
    public ForStatus ofStatus() {
        return CCT_STATUS.pick(VertxForStatus::new, VertxForStatus.class.getName());
    }

    @Override
    public ForLocale ofLocale() {
        return CCT_LOCALE.pick(ForLocaleCommon::new, ForLocaleCommon.class.getName());
    }

    @Override
    public ForAbort ofAbort() {
        throw new _501NotSupportException("[ R2MO ] 由于 Vertx 采用了特殊的 Request/Response 结构，不支持此接口！");
    }
}
