package io.r2mo.base.dbe;

import io.r2mo.base.program.R2Vector;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

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
    // Jooq 拓展专用
    private R2Vector vFrom = new R2Vector();
    private R2Vector vTo = new R2Vector();

    public Join(final Class<?> from, final String fromField, final Class<?> to, final String toField) {
        this.from = from;
        this.fromField = Objects.isNull(fromField) ? this.ID() : fromField;
        this.to = to;
        this.toField = Objects.isNull(toField) ? this.ID() : toField;
    }

    protected String ID() {
        return ID;
    }

    public static Join of(final Class<?> from, final Class<?> to) {
        return new Join(from, null, to, null);
    }

    public static Join of(final Class<?> from, final String fromField, final Class<?> to) {
        return new Join(from, fromField, to, null);
    }

    public static Join of(final Class<?> from, final Class<?> to, final String fromField) {
        return new Join(from, null, to, fromField);
    }

    public static Join of(final Class<?> from, final String fromField, final Class<?> to, final String toField) {
        return new Join(from, fromField, to, toField);
    }

}
