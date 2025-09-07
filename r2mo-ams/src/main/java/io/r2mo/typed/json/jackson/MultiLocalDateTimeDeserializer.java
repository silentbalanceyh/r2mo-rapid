package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * 支持多格式 + 时区感知 + Unicode 清洗 的 LocalDateTime 反序列化器
 * 使用方式：在 ObjectMapper 注册 Module 时替换默认的 LocalDateTimeDeserializer
 *
 * @author lang
 */
@Slf4j
public class MultiLocalDateTimeDeserializer extends LocalDateTimeDeserializer {

    // 覆盖常见无时区 & 带偏移/时区/Instant 的表达
    private static final List<DateTimeFormatter> SUPPORTED_FORMATTERS = Arrays.asList(
        // —— 无时区 (LocalDateTime) ——
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,                 // 2025-09-07T16:00:00[.SSS...]
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),

        // —— 带偏移/时区 (Offset/Zoned) ——
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,                // 2025-09-07T16:00:00+08:00 / ...Z
        DateTimeFormatter.ISO_ZONED_DATE_TIME,                 // 2025-09-07T16:00:00+08:00[Asia/Shanghai]

        // —— Instant（UTC 瞬时）——
        DateTimeFormatter.ISO_INSTANT,                         // 2025-09-07T08:00:00Z

        // —— 补充常见自定义 ——
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"), // +08:00
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")    // +0800
        // 如需要日期-only，可再加：DateTimeFormatter.ofPattern("yyyy-MM-dd")
    );

    private final ZoneId defaultZoneId;
    private final ZoneId targetZoneId;
    private final boolean convertToTargetZone;

    public MultiLocalDateTimeDeserializer() {
        this(ZoneId.systemDefault(), ZoneId.systemDefault(), true);
    }

    public MultiLocalDateTimeDeserializer(final ZoneId targetZoneId) {
        this(targetZoneId, ZoneId.systemDefault(), true);
    }

    public MultiLocalDateTimeDeserializer(final ZoneId targetZoneId,
                                          final ZoneId defaultZoneId,
                                          final boolean convertToTargetZone) {
        super(DateTimeFormatter.ISO_LOCAL_DATE_TIME); // 仅作父类默认值
        this.targetZoneId = targetZoneId;
        this.defaultZoneId = defaultZoneId;
        this.convertToTargetZone = convertToTargetZone;
    }

    /** 清洗：去除 Unicode 空白、零宽字符、BOM 等常见脏字符 */
    private static String cleanDateTimeText(final String raw) {
        if (raw == null) return null;

        // Java 11+ 可用 strip()；为兼容性，这里等价处理
        String s = raw.trim(); // 先粗清理 ASCII 空白

        // 删除常见“不可见”字符
        s = s
            .replace("\uFEFF", "")  // ZERO WIDTH NO-BREAK SPACE (BOM)
            .replace("\u200B", "")  // ZERO WIDTH SPACE
            .replace("\u200C", "")  // ZERO WIDTH NON-JOINER
            .replace("\u200D", "")  // ZERO WIDTH JOINER
            .replace("\u00A0", "")  // NO-BREAK SPACE
            .replace("\u202F", "")  // NARROW NO-BREAK SPACE
            .replace("\u2007", ""); // FIGURE SPACE

        // 再次去除所有 Unicode 空白（严格版）
        final StringBuilder sb = new StringBuilder(s.length());
        s.codePoints().forEach(cp -> {
            if (!Character.isWhitespace(cp)) {
                // 某些格式控制字符（Cf）也去掉
                if (Character.getType(cp) != Character.FORMAT) {
                    sb.appendCodePoint(cp);
                }
            }
        });
        return sb.toString();
    }

    /* ========================= 私有工具方法 ========================= */

    /** 输入里是否出现偏移/时区线索（Z、+/-HH...） */
    private static boolean looksOffsetOrZone(final String s) {
        final int t = s.indexOf('T');
        if (t >= 0) {
            final String tail = s.substring(t + 1);
            return tail.contains("Z") || tail.contains("+") || tail.matches(".*-\\d{2}(:?\\d{2})?.*");
        }
        // 没有 T 的情况（少见）
        return s.endsWith("Z") || s.contains("+") || s.matches(".*-\\d{2}(:?\\d{2})?.*");
    }

    /** 便于日志观察“看不见”的字符 */
    private static String dumpCodePoints(final String s) {
        if (s == null) return "null";
        final StringBuilder sb = new StringBuilder(s.length() * 6);
        s.codePoints().forEach(cp -> sb.append(String.format("\\u%04X ", cp)));
        return sb.toString().trim();
    }

    private static String summarizeFormats() {
        return SUPPORTED_FORMATTERS.stream()
            .map(DateTimeFormatter::toString)
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
    }

    @Override
    public LocalDateTime deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final String raw = parser.getText();
        final String text = cleanDateTimeText(raw);

        if (text == null || text.isEmpty()) {
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("解析前原始值: '{}', 检查点设置: {}", raw, dumpCodePoints(raw));
            log.debug("解析用文本: '{}'", text);
        }

        final boolean maybeHasZone = looksOffsetOrZone(text);

        for (final DateTimeFormatter f : SUPPORTED_FORMATTERS) {
            try {
                if (maybeHasZone) {
                    // 优先尝试按 Zoned/Offset/Instant 解析
                    try {
                        final ZonedDateTime zdt = ZonedDateTime.parse(text, f);
                        return this.convertZoneResult(zdt);
                    } catch (final DateTimeParseException e1) {
                        // 可能是 Offset 或 Instant
                        return this.convertOffsetLike(text, f);
                    }
                } else {
                    // 无时区 -> 当作 defaultZoneId 的本地时间，再按需转到 targetZoneId
                    final LocalDateTime ldt = LocalDateTime.parse(text, f);
                    return this.convertLocalResult(ldt);
                }
            } catch (final DateTimeParseException ignore) {
                // 尝试下一个 formatter
            }
        }

        // 全部失败，抛出更友好的错误，并打印 codepoints 辅助定位“看不见”的字符
        final String supportedFormats = summarizeFormats();
        final String msg = String.format(
            "[ R2MO ] 无法解析日期时间字符串: '%s'（codepoints: %s）。支持的格式: %s",
            raw, dumpCodePoints(raw), supportedFormats
        );
        log.error(msg);
        throw new IOException(msg);
    }

    /** ZonedDateTime 结果按需转换 */
    private LocalDateTime convertZoneResult(final ZonedDateTime zdt) {
        if (this.convertToTargetZone) {
            return zdt.withZoneSameInstant(this.targetZoneId).toLocalDateTime();
        }
        return zdt.toLocalDateTime();
    }

    /** LocalDateTime 结果按需转换（视为 defaultZoneId） */
    private LocalDateTime convertLocalResult(final LocalDateTime ldt) {
        if (this.convertToTargetZone) {
            return ldt.atZone(this.defaultZoneId)
                .withZoneSameInstant(this.targetZoneId)
                .toLocalDateTime();
        }
        return ldt;
    }

    /**
     * 兼容 Offset/Instant 路径：
     * - OffsetDateTime：含偏移但无显式区域
     * - Instant：ISO_INSTANT（UTC 瞬时）
     */
    private LocalDateTime convertOffsetLike(final String text, final DateTimeFormatter f) {
        // 1) 先试 OffsetDateTime
        try {
            final OffsetDateTime odt = OffsetDateTime.parse(text, f);
            return this.convertZoneResult(odt.toZonedDateTime());
        } catch (final DateTimeParseException ignored) {
            // 继续
        }
        // 2) 再试 Instant（ISO_INSTANT）
        Instant instant = null;
        try {
            instant = Instant.from(f.parse(text));
        } catch (final Exception ignored) {
            // 可能并非 Instant 格式
        }
        if (instant != null) {
            final ZonedDateTime zdt = instant.atZone(ZoneOffset.UTC);
            return this.convertZoneResult(zdt);
        }
        // 两者都不是，则抛给上层让其尝试下一个 formatter
        throw new DateTimeParseException("[ R2MO ] 不是偏移量/即时时间", text, 0);
    }
}
