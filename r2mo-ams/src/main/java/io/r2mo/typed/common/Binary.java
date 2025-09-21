package io.r2mo.typed.common;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 特殊结果处理，用于
 * <pre>
 *     1. {@link InputStream} 提取
 *     2. 尺寸提取 -> Content-Length
 * </pre>
 *
 * @author lang : 2025-09-20
 */
@Data
@Accessors(fluent = true)
public class Binary implements Serializable {
    @Setter(AccessLevel.NONE)
    private final InputStream stream;
    private long length = 0;
    private String mime = "application/octet-stream";

    public Binary(final InputStream stream) {
        this.stream = stream;
    }

    public InputStream stream() {
        return this.stream;
    }
}
