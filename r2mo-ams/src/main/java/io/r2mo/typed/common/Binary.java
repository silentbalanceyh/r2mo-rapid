package io.r2mo.typed.common;

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
public class Binary implements Serializable {

    private final InputStream stream;
    private long length = 0;

    public Binary(final InputStream stream) {
        this.stream = stream;
    }

    public InputStream in() {
        return this.stream;
    }

    public Binary length(final long length) {
        this.length = length;
        return this;
    }

    public long length() {
        return this.length;
    }
}
