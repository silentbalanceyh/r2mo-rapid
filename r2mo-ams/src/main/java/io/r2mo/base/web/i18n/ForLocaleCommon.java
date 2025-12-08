package io.r2mo.base.web.i18n;

import io.r2mo.base.web.ForLocale;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 国际化消息处理器
 *
 * <p>支持三种模式：</p>
 * <ul>
 *     <li><b>自由模式（formatInfo）</b>：
 *         <pre>
 *         +--------------------------+
 *         |   调用 formatInfo(key)   |
 *         +--------------------------+
 *                    |
 *                    v
 *           是否存在缓存记录？
 *               /        \
 *             是          否
 *             |            |
 *             v            v
 *   命中缓存=存在 → 调用 formatI18n
 *   命中缓存=不存在 → 尝试查找资源文件
 *                                |
 *                      找到 → 缓存=true → 格式化返回
 *                      找不到 → 缓存=false → 返回原始 key
 *         </pre>
 *     </li>
 *
 *     <li><b>国际化模式（formatI18n）</b>：
 *         <pre>
 *         +-----------------------------+
 *         |   调用 formatI18n(message)  |
 *         +-----------------------------+
 *                       |
 *                       v
 *               从 MessageInfo 查找
 *                       |
 *          找到 → 格式化返回
 *          找不到 → 返回原始 message
 *         </pre>
 *     </li>
 *
 *     <li><b>异常模式（formatFail）</b>：
 *         <pre>
 *         +----------------------------+
 *         | 调用 formatFail(code)    |
 *         +----------------------------+
 *                       |
 *                       v
 *           {@code code < 0} ? 使用 MessageFail
 *                    : 使用 MessageInfo
 *                       |
 *                       v
 *                转换为 Exxxxx / Ixxxxx
 *                       |
 *          找到 → 格式化返回
 *          找不到 → 返回原始 key
 *         </pre>
 *     </li>
 * </ul>
 *
 * <p>支持两种占位符：</p>
 * <ul>
 *     <li>MessageFormat 风格：<code>{0}, {1}</code></li>
 *     <li>SLF4J 风格：<code>{}</code></li>
 * </ul>
 *
 * <p><b>默认使用中文 (Locale.CHINA)</b></p>
 *
 * @author lang
 * @since 2025-09-26
 */
public class ForLocaleCommon implements ForLocale {
    static final String BASE_INFO = "MessageInfo";
    static final String BASE_FAIL = "MessageFail";
    static final ResourceBundle.Control COMBINED =
        new ForLocaleBundle(ResourceBundle.Control.TTL_NO_EXPIRATION_CONTROL, false);
    /** 默认语言环境：中文 */
    static final Locale DEFAULT_LOCALE = Locale.CHINA;

    private static final Pattern SLF4J_PATTERN = Pattern.compile("\\{}");
    /** 缓存 messageKey 是否存在于 i18n 文件 */
    private static final ConcurrentHashMap<String, Boolean> CACHE_I18N = new ConcurrentHashMap<>();


    /**
     * 将 code 转换成资源文件中的 key
     * - 负数 → E + abs(code)
     * - 正数 → I + code
     */
    private static String toKey(final int code) {
        if (code < 0) {
            return "E" + String.format("%05d", Math.abs(code));
        } else {
            return "I" + String.format("%05d", code);
        }
    }

    /**
     * 从资源文件中读取国际化信息
     */
    private static String lookup(final String baseName, final String key, final Locale locale) {
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle(baseName,
                locale != null ? locale : DEFAULT_LOCALE, COMBINED);
            return bundle.getString(key);
        } catch (final MissingResourceException ex) {
            return null;
        }
    }

    /**
     * 格式化模板，支持 {} 与 {0}
     */
    private static String formatPattern(final String pattern, final Object... args) {
        if (pattern == null) {
            return null;
        }
        if (args == null || args.length == 0) {
            return pattern;
        }

        if (pattern.contains("{}")) {
            final StringBuilder sb = new StringBuilder();
            final Matcher matcher = SLF4J_PATTERN.matcher(pattern);
            int index = 0;
            while (matcher.find()) {
                matcher.appendReplacement(sb, "{" + (index++) + "}");
            }
            matcher.appendTail(sb);
            return MessageFormat.format(sb.toString(), args);
        }

        return MessageFormat.format(pattern, args);
    }

    /**
     * 直接国际化模式
     * - 从 MessageInfo.properties 中查找 messageKey
     * - 如果不存在则返回原始 messageKey
     */
    @Override
    public String formatI18n(final String messageKey, final Object... messageArgs) {
        if (messageKey == null) {
            return null;
        }
        return this.formatI18n(BASE_INFO, messageKey, messageArgs);
    }

    @Override
    public String formatI18n(final String filename, final String messageKey, final Object... messageArgs) {
        if (messageKey == null) {
            return null;
        }
        final String pattern = lookup(filename, messageKey, DEFAULT_LOCALE);
        final String template = pattern != null ? pattern : messageKey;
        return formatPattern(template, messageArgs);
    }

    /**
     * 自由模式
     * - 优先查找国际化
     * - 使用缓存避免重复查找
     * - 找不到时直接返回原始 messageKey
     */
    @Override
    public String formatInfo(final String messageKey, final Object... messageArgs) {
        if (messageKey == null) {
            return null;
        }
        final Boolean cached = CACHE_I18N.get(messageKey);
        if (cached != null && cached) {
            // 缓存命中，直接走国际化
            return this.formatI18n(messageKey, messageArgs);
        }

        final String pattern = lookup(BASE_INFO, messageKey, DEFAULT_LOCALE);
        if (pattern != null) {
            CACHE_I18N.put(messageKey, true);
            return formatPattern(pattern, messageArgs);
        }

        CACHE_I18N.put(messageKey, false);
        return formatPattern(messageKey, messageArgs);  // Fix issue
    }

    /**
     * 异常模式
     * - 负数 → MessageFail.properties
     * - 正数 → MessageInfo.properties
     */
    @Override
    public String formatFail(final int code, final Object... messageArgs) {
        final String base = code < 0 ? BASE_FAIL : BASE_INFO;
        final String key = toKey(code);
        final String pattern = lookup(base, key, DEFAULT_LOCALE);
        final String template = pattern != null ? pattern : key;
        return formatPattern(template, messageArgs);
    }

}
