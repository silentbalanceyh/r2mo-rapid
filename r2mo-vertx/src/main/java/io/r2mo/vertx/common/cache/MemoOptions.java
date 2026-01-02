package io.r2mo.vertx.common.cache;

import io.vertx.core.Vertx;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

/**
 * 由于参数维度过多，所以设置单独参数设置
 *
 * @author lang : 2026-01-01
 */
@Data
@Accessors(chain = true, fluent = true)
public class MemoOptions<K, V> implements Serializable {
    private Vertx vertx;
    private Class<K> classK;
    private Class<V> classV;
    private Duration duration = Duration.ofNanos(0);        // 超时时间
    private int size = 0;                                   // 最大缓存数量，0 表示不限制

    public boolean isOk() {
        return Objects.nonNull(this.classK)
            && Objects.nonNull(this.classV)
            && 0 <= this.size;
    }

    public String keyCache(final Class<?> caller) {
        return caller.getName() + "@"
            + this.classK.getName() + "=" + this.classV.getName()
            + "S=" + this.size
            + "D=" + this.duration.hashCode();
    }
}
