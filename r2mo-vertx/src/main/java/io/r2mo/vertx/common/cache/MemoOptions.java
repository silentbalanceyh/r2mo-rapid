package io.r2mo.vertx.common.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
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
    /**
     * 此处的 type 一般为缓存的类型标识，其中类型标识会根据不同的 caller 来决定，caller 典型如{@link MemoAt} 的实现类，每个实现类都会
     * 对应不同的缓存类型，如 EhCache、Caffeine、Redis 等，所以有了 type 字段来区分不同的缓存实现，就可以完整保证缓存的基础唯一性。关于
     * caller 在不同场景之下的使用。
     */
    @Setter(AccessLevel.NONE)
    private final Class<?> caller;
    private String name;
    private Class<K> classK;
    private Class<V> classV;
    private Duration duration = Duration.ofNanos(0);        // 超时时间
    private int size = 0;                                   // 最大缓存数量，0 表示不限制
    @JsonIgnore
    private JsonObject extension = new JsonObject();        // 扩展参数
    @JsonIgnore
    private Object configuration;

    public MemoOptions(final Class<?> caller) {
        Objects.requireNonNull(caller, "[ R2MO ] MemoOptions 构造时，caller 不可为空！");
        this.caller = caller;
    }

    @SuppressWarnings("unchecked")
    public <C> C configuration() {
        return (C) this.configuration;
    }

    // ----------------------- 构造具有不同指纹的 MemoOptions ---------------------

    /**
     * 基于当前配置衍生一个新的配置实例，仅修改过期时间。
     * <p>
     * 场景：同一个缓存定义（相同的 Key/Value 类型和 Size），但在不同业务场景下需要不同的过期时间。
     *
     * @param expiredAs 过期时间 (单位：秒)。如果 <= 0 则表示不过期 (Duration.ZERO)。
     * @return 新的 MemoOptions 实例 (副本)
     */
    public <K1, V1> MemoOptions<K1, V1> of(final long expiredAs) {
        final Duration expired = expiredAs > 0 ? Duration.ofSeconds(expiredAs) : Duration.ZERO;
        return this.of(expired);
    }

    @SuppressWarnings("all")
    public <K1, V1> MemoOptions<K1, V1> of(final Duration expiredAs) {
        final MemoOptions<K1, V1> next = new MemoOptions<>(this.caller);

        // 1. 复制基础属性 (保持身份一致性)
        next.name = this.name;
        next.classK = (Class<K1>) this.classK;
        next.classV = (Class<V1>) this.classV;
        next.size = this.size;
        next.extension = this.extension;    // 追加 extension 中的配置到环境中

        // 2. 处理时间转换 (Seconds -> Duration)
        next.duration = Objects.isNull(expiredAs) ? Duration.ZERO : expiredAs;

        return next;
    }

    /**
     * 生成缓存指纹（唯一标识 ID），指纹数据中：
     *
     * @return 缓存唯一键
     */
    public String fingerprint() {
        // 1. 处理类型可能为 null 的情况 (适配 Redis/Caffeine 泛型模式)
        final String kType = this.classK == null ? "*" : this.classK.getName();
        final String vType = this.classV == null ? "*" : this.classV.getName();

        // 2. 处理调用者和缓存名称 (防止 name 为空)
        final String callerName = this.caller.getName();
        final String name = Objects.isNull(this.name) ? "R2MO_CACHE_DEFAULT" : this.name;

        // 4. 组装指纹
        // 格式示例: io.myapp.UserService://user_cache@java.lang.String=io.myapp.User/S=1000/D=60000
        return callerName + "://" + name + "@" + kType + "=" + vType;
    }
}
