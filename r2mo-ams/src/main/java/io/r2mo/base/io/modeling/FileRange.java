package io.r2mo.base.io.modeling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件范围描述
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileRange {
    private Long start;     // 起始字节位置
    private Long end;       // 结束字节位置
    private Long length;    // 范围长度

    public static FileRange of(final Long start, final Long end) {
        return FileRange.builder()
            .start(start)
            .end(end)
            .length(end - start)
            .build();
    }

    public static FileRange of(final String header) {
        if (header == null || !header.startsWith("bytes=")) {
            return null;
        }

        try {
            final String[] ranges = header.substring(6).split("-");
            final Long start = Long.valueOf(ranges[0]);
            final Long end = ranges.length > 1 && !ranges[1].isEmpty() ?
                Long.valueOf(ranges[1]) : null;

            return FileRange.builder()
                .start(start)
                .end(end)
                .length(end != null ? end - start : null)
                .build();
        } catch (final Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "bytes=" + this.start + "-" + (this.end != null ? this.end : "");
    }
}
