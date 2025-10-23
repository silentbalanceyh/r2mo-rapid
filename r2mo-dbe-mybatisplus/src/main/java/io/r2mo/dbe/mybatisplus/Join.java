package io.r2mo.dbe.mybatisplus;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lang : 2025-10-23
 */
@Data
@Accessors(fluent = true, chain = true)
public class Join {
    private static final String ID = "id";
    private Class<?> from;
    private String fromField;
    private Class<?> to;
    private String toField;

    public Join(final Class<?> from, final String fromField, final Class<?> to, final String toField) {
        this.from = from;
        this.fromField = fromField;
        this.to = to;
        this.toField = toField;
    }

    public static Join of(final Class<?> from, final Class<?> to) {
        return new Join(from, ID, to, ID);
    }

    public static Join of(final Class<?> from, final String fromField, final Class<?> to) {
        return new Join(from, fromField, to, ID);
    }

    public static Join of(final Class<?> from, final Class<?> to, final String fromField) {
        return new Join(from, ID, to, fromField);
    }

    public static Join of(final Class<?> from, final String fromField, final Class<?> to, final String toField) {
        return new Join(from, fromField, to, toField);
    }

}
