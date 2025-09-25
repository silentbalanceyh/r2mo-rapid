package io.r2mo.base.web.i18n;

import io.r2mo.base.web.ForLocale;

import java.text.MessageFormat;

/**
 * @author lang : 2025-09-03
 */
public class ForLocaleCommon implements ForLocale {

    @Override
    public String format(final String messageKey, final Object... messageArgs) {
        return MessageFormat.format(messageKey, messageArgs);
    }
}
