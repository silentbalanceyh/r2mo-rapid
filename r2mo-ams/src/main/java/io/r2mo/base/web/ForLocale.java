package io.r2mo.base.web;

/**
 * @author lang : 2025-09-03
 */
public interface ForLocale {

    String formatInfo(String messagePattern, Object... messageArgs);

    String formatI18n(String messageKey, Object... messageArgs);

    String formatI18n(String filename, String messageKey, Object... messageArgs);

    String formatFail(int code, Object... messageArgs);
}
