package io.r2mo.base.util;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Duration 解析工具类
 * <p>
 * 修复：支持纯数字 "0" 的解析。
 * </p>
 *
 * @author lang : 2025-11-12
 */
class UTDuration {

    // 正则表达式：匹配数字和单位 (支持 s, m, h, d, ms, us, µs, ns)
    private static final Pattern SIMPLE_DURATION_PATTERN = Pattern.compile("^([0-9]+)(s|m|h|d|ms|us|µs|ns)$");

    static Duration parseToDuration(final String durationStr) {
        if (durationStr == null || durationStr.trim().isEmpty()) {
            throw new IllegalArgumentException("[ R2MO ] Duration 字符串不可为空，无法解析：" + durationStr);
        }

        final String trimmedStr = durationStr.trim();

        // =========================================================
        // 1. 特殊处理 "0" (常见配置，表示 0 或 永不过期)
        // =========================================================
        if ("0".equals(trimmedStr)) {
            return Duration.ZERO;
        }

        // =========================================================
        // 2. 尝试使用 ISO-8601 标准解析器
        // =========================================================
        try {
            return Duration.parse(trimmedStr);
        } catch (final DateTimeParseException isoException) {
            // ISO 格式解析失败，继续尝试简单格式
        }

        // =========================================================
        // 3. 尝试使用自定义简单格式解析器 (带单位)
        // =========================================================
        final Matcher matcher = SIMPLE_DURATION_PATTERN.matcher(trimmedStr.toLowerCase());

        if (!matcher.matches()) {
            // 友好的错误提示
            throw new IllegalArgumentException("[ R2MO ] 无效的持续时间格式：" + durationStr +
                "。支持的格式包括：\n" +
                "1. 纯数字 0\n" +
                "2. 简单格式 (如 10s, 10d, 100ms)\n" +
                "3. ISO-8601 (如 PT10S)");
        }

        final String numberStr = matcher.group(1);
        final String unitStr = matcher.group(2);

        final long number;
        try {
            number = Long.parseLong(numberStr);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("[ R2MO ] 持续时间字符串中的数值无效：" + durationStr, e);
        }

        final ChronoUnit unit = switch (unitStr) {
            case "s" -> ChronoUnit.SECONDS;
            case "m" -> ChronoUnit.MINUTES;
            case "h" -> ChronoUnit.HOURS;
            case "d" -> ChronoUnit.DAYS;
            case "ms" -> ChronoUnit.MILLIS;
            case "us", "µs" -> ChronoUnit.MICROS;
            case "ns" -> ChronoUnit.NANOS;
            default -> throw new IllegalArgumentException("[ R2MO ] 持续时间字符串中包含意外的单位：" + durationStr);
        };

        return Duration.of(number, unit);
    }
}