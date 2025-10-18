package io.r2mo.vertx.jooq;

import io.r2mo.SourceReflect;
import io.r2mo.base.program.R2Vector;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import org.jooq.Table;

import java.util.Objects;

/**
 * @author lang : 2025-10-18
 */
public class JooqMetaAsync {
    private final Class<?> daoCls;
    private final JooqMeta metadata;

    public JooqMetaAsync vector(final R2Vector vector) {
        this.metadata.vector(vector);
        return this;
    }

    public JooqMetaAsync(final Class<?> daoCls) {
        this.daoCls = daoCls;
        // 提取表名
        final Table<?> table = SourceReflect.value(daoCls, "table");
        // 提取实体类名
        final Class<?> entityCls = SourceReflect.value(daoCls, "type");
        Objects.requireNonNull(entityCls, "[ R2MO ] 无法从 DAO 类中提取实体类信息：" + daoCls.getName());
        this.metadata = JooqMeta.of(entityCls, table);
    }

    public Class<?> entityClass() {
        return Objects.requireNonNull(this.metadata).entityCls();
    }

    public Table<?> entityTable() {
        return Objects.requireNonNull(this.metadata).table();
    }
}
