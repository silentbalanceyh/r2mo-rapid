package io.r2mo.spring.common.spi;

import io.r2mo.base.web.ForLocale;

import java.text.MessageFormat;

/**
 * @author lang : 2025-09-03
 */
class SpringForLocale implements ForLocale {
    @Override
    public String format(final String messageKey, final Object... messageArgs) {
        return MessageFormat.format(messageKey, messageArgs);
    }
}
