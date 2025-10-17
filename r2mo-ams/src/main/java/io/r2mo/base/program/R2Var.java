package io.r2mo.base.program;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author lang : 2025-10-17
 */
@Data
@Accessors(fluent = true)
public class R2Var implements Serializable {
    private String name;
    private String alias;
    private Class<?> type;
    private Object value;
    @Getter(AccessLevel.NONE)
    private Object valueDefault;

    public Class<?> type() {
        return Objects.requireNonNull(this.type);
    }

    @SuppressWarnings("unchecked")
    public <T> T value() {
        if (Objects.isNull(this.value)) {
            return (T) this.valueDefault;
        }
        return (T) this.value;
    }
}
