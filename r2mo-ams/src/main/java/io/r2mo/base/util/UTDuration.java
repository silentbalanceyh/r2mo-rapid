package io.r2mo.base.util;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Duration 解析工具类
 * 支持 ISO-8601 标准格式 (如 "P1DT12H30M5S") 和简单格式 (如 "10s", "10d", "6m", "2h", "100ms")
 * 以及 Spring Boot 风格的格式 (如 "10s", "10d", "6m", "2h", "100ms", "24h")
 *
 * @author lang : 2025-11-12
 */
class UTDuration {

    // 正则表达式：匹配数字和单位 (支持 s, m, h, d, ms, us, µs, ns)
    private static final Pattern SIMPLE_DURATION_PATTERN = Pattern.compile("^([0-9]+)(s|m|h|d|ms|us|µs|ns)$");

    /**
     * 将字符串解析为 Duration 对象
     * 支持以下格式：
     * 1. ISO-8601 标准格式: "PnDTnHnMn.nS" (例如 "P1DT12H30M5S", "PT10M", "P2D")
     * 2. 简单格式: "<number><unit>" (例如 "10s", "6m", "2h", "10d", "100ms", "500us", "1µs", "100ns")
     * 单位支持: s (秒), m (分钟), h (小时), d (天), ms (毫秒), us/µs (微秒), ns (纳秒)
     *
     * @param durationStr 格式如 "P1DT12H30M5S", "10s", "10d", "100ms" 的字符串
     *
     * @return 对应的 Duration 对象
     * @throws IllegalArgumentException 如果字符串格式不正确或解析失败
     */
    static Duration parseToDuration(final String durationStr) {
        if (durationStr == null || durationStr.trim().isEmpty()) {
            throw new IllegalArgumentException("[ R2MO ] Duration 字符串不可为空，无法解析：" + durationStr);
        }

        final String trimmedStr = durationStr.trim();

        // 1. 尝试使用 ISO-8601 标准解析器 (PT10S, P1D, P1DT2H, etc.)
        try {
            return Duration.parse(trimmedStr);
        } catch (final DateTimeParseException isoException) {
            // ISO 格式解析失败，继续尝试简单格式
        }

        // 2. 尝试使用自定义简单格式解析器
        final Matcher matcher = SIMPLE_DURATION_PATTERN.matcher(trimmedStr.toLowerCase());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("[ R2MO ] 无效的持续时间格式：" + durationStr + "。支持的格式包括 ISO-8601（例如 PT10S、P1D）和简单格式（例如 10s、10d、100ms）");
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
            case "ms" -> ChronoUnit.MILLIS; // 毫秒
            case "us", "µs" -> ChronoUnit.MICROS; // 微秒 (Unicode mu)
            case "ns" -> ChronoUnit.NANOS;
            default ->
                // 理论上不会到达这里，因为正则表达式已经限制了单位
                throw new IllegalArgumentException("[ R2MO ] 持续时间字符串中包含意外的单位：" + durationStr);
        };

        // 使用 number 和 unit 创建 Duration
        return Duration.of(number, unit);
    }
}