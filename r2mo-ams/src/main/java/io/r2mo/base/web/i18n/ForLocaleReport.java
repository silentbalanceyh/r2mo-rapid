package io.r2mo.base.web.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * @author lang : 2025-11-13
 */
public class ForLocaleReport {

    // ------- 打印
    private static final Pattern KEY_PATTERN_ERROR = Pattern.compile("^E[0-9]{5}$");

    private static final Pattern KEY_PATTERN_INFO = Pattern.compile("^I[0-9]{5}$");

    public static Map<String, String> getMapError() {
        return getMap(ForLocaleCommon.BASE_FAIL, KEY_PATTERN_ERROR);
    }

    public static Map<String, String> getMapInfo() {
        return getMap(ForLocaleCommon.BASE_INFO, KEY_PATTERN_INFO);
    }

    /**
     * 获取所有 ENNNNN 格式的异常消息映射（来自 MessageFail 资源文件）
     *
     * @return Map<异常码, 国际化消息>，例如 {"E80601": "事件开始缺失"}
     */
    private static Map<String, String> getMap(final String filename, final Pattern pattern) {
        final Map<String, String> errorMap = new HashMap<>();
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle(
                filename,
                ForLocaleCommon.DEFAULT_LOCALE,
                ForLocaleCommon.COMBINED
            );

            for (final String key : bundle.keySet()) {
                if (pattern.matcher(key).matches()) {
                    try {
                        final String value = bundle.getString(key);
                        errorMap.put(key, value);
                    } catch (final Exception ignored) {
                        // 忽略个别 key 读取失败的情况
                    }
                }
            }
        } catch (final MissingResourceException ignored) {
            // 若 MessageFail.properties 不存在，返回空 map
        }
        return errorMap;
    }
}
