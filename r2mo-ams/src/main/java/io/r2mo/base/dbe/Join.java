package io.r2mo.base.dbe;

import io.r2mo.base.program.R2Vector;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
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
    @Setter(AccessLevel.NONE)
    private String fromField;
    private Class<?> to;
    @Setter(AccessLevel.NONE)
    private String toField;
    // Jooq 拓展专用
    @Setter(AccessLevel.NONE)
    private R2Vector vFrom = new R2Vector();
    @Setter(AccessLevel.NONE)
    private R2Vector vTo = new R2Vector();

    public Join(final Class<?> from, final String fromField, final Class<?> to, final String toField) {
        this.from = from;
        this.fromField = Objects.isNull(fromField) ? this.ID() : fromField;
        this.to = to;
        this.toField = Objects.isNull(toField) ? this.ID() : toField;
    }

    public Join from(final Class<?> from, final String fromField) {
        this.from = from;
        this.fromField = Objects.isNull(fromField) ? this.ID() : fromField;
        return this;
    }

    public Join from(final Class<?> from) {
        this.from = from;
        this.fromField = this.ID();
        return this;
    }

    public Join from(final R2Vector from) {
        this.vFrom = from;
        return this;
    }

    public Join to(final R2Vector to) {
        this.vTo = to;
        return this;
    }

    public Join to(final Class<?> to) {
        this.to = to;
        this.toField = this.ID();
        return this;
    }

    public Join to(final Class<?> to, final String toField) {
        this.to = to;
        this.toField = Objects.isNull(toField) ? this.ID() : toField;
        return this;
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
