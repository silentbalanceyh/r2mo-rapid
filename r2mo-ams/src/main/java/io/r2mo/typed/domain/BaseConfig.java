package io.r2mo.typed.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 局限于配置数据处理（动态和扩展配置，业务数据通常不做类似信息）
 *
 * @author lang : 2025-12-07
 */
public abstract class BaseConfig implements Serializable {
    /** 扩展属性（不在数据库中） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnore
    private final Map<String, Object> extension = new HashMap<>();

    @JsonAnySetter            // 反序列化时：任何未匹配到属性的键都会进这里
    public void putExtension(final String key, final Object value) {
        this.extension.put(key, value);
    }

    @JsonAnyGetter            // 序列化时：把 otherProps 的键值“摊平”到顶层 JSON
    public Map<String, Object> getExtension() {
        return this.extension;
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtension(final String key) {
        return (T) this.extension.get(key);
    }

    public boolean hasExtension(final String key) {
        return this.extension.containsKey(key);
    }

    public void cleanExtension() {
        this.extension.clear();
    }
}
