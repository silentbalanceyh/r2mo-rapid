package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),    // 2025-09-17 13:51:38 ✅ 关键格式
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),  // 2025-09-17T13:51:38
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),    // 2025/09/17 13:51:38
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"), // 2025-09-17T13:51:38.123

        // —— 带偏移/时区 (Offset/Zoned) ——
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,                // 2025-09-07T16:00:00+08:00 / ...Z
        DateTimeFormatter.ISO_ZONED_DATE_TIME,                 // 2025-09-07T16:00:00+08:00[Asia/Shanghai]

        // —— Instant（UTC 瞬时）——
        DateTimeFormatter.ISO_INSTANT,                         // 2025-09-07T08:00:00Z

        // —— 补充常见自定义 ——
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"), // +08:00
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")    // +0800
    );

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
    }

    /** 清洗：去除 Unicode 空白、零宽字符、BOM 等常见脏字符，但保留有效空格 */
    private static String cleanDateTimeText(final String raw) {
        if (raw == null) return null;

        String s = raw;

        // 删除常见的"不可见"字符（这些通常是脏字符）
        s = s
            .replace("\uFEFF", "")  // ZERO WIDTH NO-BREAK SPACE (BOM)
            .replace("\u200B", "")  // ZERO WIDTH SPACE
            .replace("\u200C", "")  // ZERO WIDTH NON-JOINER
            .replace("\u200D", "")  // ZERO WIDTH JOINER
            .replace("\u202F", "")  // NARROW NO-BREAK SPACE
            .replace("\u2007", "")  // FIGURE SPACE
            .replace("\u2009", "")  // THIN SPACE
            .replace("\u200A", "")  // HAIR SPACE
            .replace("\u205F", "")  // MEDIUM MATHEMATICAL SPACE
            .replace("\u00A0", ""); // NO-BREAK SPACE

        // 只去除首尾的普通空白字符，保留中间的有效空格
        s = s.trim();

        return s;
    }

    @Override
    public LocalDateTime deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final String raw = parser.getText();
        final String text = cleanDateTimeText(raw);

        if (text == null || text.isEmpty()) {
            return null;
        }

        if (log.isDebugEnabled()) {
            log.debug("[ R2MO ] 解析日期时间: 原始值='{}', 清洗后='{}'", raw, text);
        }

        // 尝试所有支持的格式
        for (final DateTimeFormatter formatter : SUPPORTED_FORMATTERS) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("[ R2MO ] 尝试格式: {} 解析 '{}'", formatter.toString(), text);
                }

                final LocalDateTime result = LocalDateTime.parse(text, formatter);
                log.debug("[ R2MO ] 成功解析: '{}' -> {}", text, result);
                return result;

            } catch (final DateTimeParseException e) {
                if (log.isDebugEnabled()) {
                    log.debug("[ R2MO ] 格式 {} 解析失败: {}", formatter.toString(), e.getMessage());
                }
                // 继续尝试下一个格式
            }
        }

        // 如果所有格式都失败，记录详细错误信息
        final String supportedFormats = SUPPORTED_FORMATTERS.stream()
            .map(DateTimeFormatter::toString)
            .reduce((a, b) -> a + ", " + b)
            .orElse("NONE");

        log.error("[ R2MO ] 无法解析日期时间字符串: '{}' (原始: '{}')。支持的格式: {}",
            text, raw, supportedFormats);

        throw new IOException(String.format(
            "[ R2MO ] 无法解析日期时间字符串: '%s' (原始: '%s')。支持的格式: %s",
            text, raw, supportedFormats));
    }
}