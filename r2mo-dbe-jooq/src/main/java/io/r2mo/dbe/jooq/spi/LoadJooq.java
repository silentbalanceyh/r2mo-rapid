package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBLoadBase;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.program.R2Vector;
import io.r2mo.dbe.jooq.core.domain.JooqMap;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.common.Kv;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.UniqueKey;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author lang : 2025-10-24
 */
@SPID(DBLoad.DEFAULT_SPID_META)
public class LoadJooq extends DBLoadBase {

    @Override
    protected void setupTable(final DBNode node, final Class<?> entityCls) {
        final Table<?> table = LoadREF.of().loadTable(entityCls);
        // 设置表名称
        /*
         * - table
         * - key ( primaryKey = primaryColumn )
         * - field -> column
         * - column -> field
         */
        node.table(table.getName());
        // FIX-DBE: 设置 DAO 类，此步骤很重要
        final Class<?> daoCls = LoadREF.of().loadClass(entityCls);
        if (daoCls != entityCls) {
            node.dao(daoCls);
        }

        final R2Vector vectorRef = node.vector();
        // 设置 field -> column 映射关系
        final ConcurrentMap<String, String> mapping = JooqMap.build(table, entityCls);
        vectorRef.mappingColumn(mapping);
        // 最终计算 primaryKey = primaryColumn
        final UniqueKey<?> primaryKey = table.getPrimaryKey();
        if (Objects.isNull(primaryKey)) {
            return;
        }
        // 目前只考虑单字段主键（不考虑符合主键）
        if (1 < primaryKey.getFields().size()) {
            return;
        }
        final Field<?> pkField = primaryKey.getFields().getFirst();
        mapping.forEach((field, column) -> {
            if (column.equalsIgnoreCase(pkField.getName())) {
                node.key(Kv.create(field, column));
            }
        });
    }

    @Override
    protected Class<?> setupBefore(final Class<?> daoCls) {
        return LoadREF.of().loadClass(daoCls);
    }
}
