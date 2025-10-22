package io.r2mo.base.program;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.typed.common.Kv;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-18
 */
@Data
public class R2Mapping implements Serializable {
    /**
     * 由于可以重复，所以此处必须是这种结构才能真正被使用起来，此处的 mapping 是基础映射表，里面包含了 key = value 的基本映射关系，
     * 如果 key 重复则直接存放在另外的变量中！序列化时只考虑 mapping 变量！
     */
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final ConcurrentMap<String, String> mapping = new ConcurrentHashMap<>();

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final ConcurrentMap<String, String> revert = new ConcurrentHashMap<>();
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final List<Kv<String, String>> extension = new ArrayList<>();

    public void setMapping(final String name, final String nameTo) {
        this.mapping.put(name, nameTo);
        this.revert.put(nameTo, name);
    }

    public void setMapping(final ConcurrentMap<String, String> mapping) {
        this.setMapping(mapping, true);
    }

    public void setMapping(final ConcurrentMap<String, String> mapping, final boolean isClean) {
        if (Objects.isNull(mapping)) {
            return;
        }
        if (isClean) {
            this.mapping.clear();
            this.revert.clear();
        }

        for (final Map.Entry<String, String> entry : mapping.entrySet()) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            this.mapping.put(k, v);         // 正向填充
            if (Objects.isNull(v)) {
                continue;
            }
            if (this.revert.containsKey(k)) {
                // 扩展填充
                this.extension.add(Kv.create(k, v));
            }
            this.revert.put(v, k);      // 逆向填充
        }
    }

    public ConcurrentMap<String, String> mapBy() {
        return this.revert;
    }

    public String mapBy(final String key) {
        // 若没有映射关系则返回原始 key
        return this.mapBy(key, key);
    }

    public String mapBy(final String key, final String defaultValue) {
        return this.revert.getOrDefault(key, defaultValue);
    }

    public ConcurrentMap<String, String> mapTo() {
        return this.mapping;
    }

    public String mapTo(final String key) {
        // 若没有映射关系则返回原始 key
        return this.mapTo(key, key);
    }

    public String mapTo(final String key, final String defaultValue) {
        return this.mapping.getOrDefault(key, defaultValue);
    }
}
