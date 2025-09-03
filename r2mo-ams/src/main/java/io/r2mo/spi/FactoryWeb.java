package io.r2mo.spi;

import io.r2mo.base.web.ForAbort;
import io.r2mo.base.web.ForLocale;
import io.r2mo.base.web.ForStatus;

/**
 * @author lang : 2025-08-28
 */
public interface FactoryWeb {

    ForStatus ofStatus();

    ForLocale ofLocale();

    ForAbort ofAbort();
}
