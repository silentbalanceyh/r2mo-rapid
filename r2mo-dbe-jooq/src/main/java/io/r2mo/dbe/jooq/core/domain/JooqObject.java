package io.r2mo.dbe.jooq.core.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.r2mo.SourceReflect;
import io.r2mo.base.dbe.constant.OpType;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author lang : 2025-10-19
 */
public class JooqObject {
    private final JooqMeta meta;
    private final DSLContext dsl;

    public JooqObject(final JooqMeta meta, final DSLContext dsl) {
        this.meta = meta;
        this.dsl = dsl;
    }

    public <T> Record createRecord(final T entity) {
        final org.jooq.Record record = this.dsl.newRecord(this.meta.table(), entity);
        final int size = record.size();
        for (int i = 0; i < size; i++) {
            if (record.get(i) == null) {
                @SuppressWarnings("unchecked") final Field<Object> field = (Field<Object>) record.field(i);
                if (Objects.isNull(field)) {
                    continue;
                }
                if (!field.getDataType().nullable() && !field.getDataType().identity()) {
                    record.set(field, DSL.defaultValue());
                }
            }
        }
        return record;
    }

    public <T> void setPrimaryKey(final List<T> entities) {
        for (final T entity : entities) {
            this.setPrimaryKey(entity, OpType.CREATE);
        }
    }

    public <T> void copyFrom(final T target, final T updated) {
        if (Objects.isNull(target) || Objects.isNull(updated)) {
            return;
        }
        final Set<String> pKeySet = this.meta.keyPrimaryN();
        BeanUtil.copyProperties(updated, target, CopyOptions.create()
            .ignoreNullValue()
            .ignoreError()
            .setIgnoreProperties(pKeySet.toArray(new String[0]))
        );
    }

    @SuppressWarnings("unchecked")
    public Condition whereId(final Serializable id) {
        final UniqueKey<?> pKey = this.meta.table().getPrimaryKey();
        Objects.requireNonNull(pKey,
            "[ R2MO ] 实体类 " + this.meta.entityCls().getName() + " 未定义主键，无法执行 whereId！");
        final TableField<? extends Record, ?>[] pKeyFields = pKey.getFieldsArray();
        final Condition condition;
        if (1 == pKeyFields.length) {
            // 🔑 单主键情况 - 让 jOOQ 自动处理类型转换
            final TableField<? extends Record, Object> singleField = (TableField<? extends Record, Object>) pKeyFields[0];
            condition = singleField.eq(id);
        } else {
            // 多主键
            condition = DSL.row(pKeyFields).equal((Record) id);
        }
        return condition;
    }

    public <T> void setPrimaryKey(final List<T> entities, final OpType type) {
        entities.forEach(entity -> this.setPrimaryKey(entity, type));
    }

    public <T> void setPrimaryKey(final T entity, final OpType type) {
        if (OpType.CREATE != type) {
            return;
        }

        // 提取主键值
        final String primaryKey = this.meta.keyPrimary();
        if (Objects.isNull(primaryKey)) {
            return;
        }
        final Field<?> field = this.meta.findColumn(primaryKey);
        final Object ifSet = SourceReflect.value(entity, primaryKey);


        /*
         * FIX-DBE：主键自动填充，此处旧版的逻辑有问题，正常模式下应该是当主键为空时才进行填充
         * 1. 如果主键类型是 String，则生成 UUID 字符串
         * 2. 如果主键类型是 UUID，则生成 UUID 对象
         * 这种场景主要在于已经提供了主键值的情况下，不应该覆盖原有值，典型如 Excel 导入流程
         **/
        if (Objects.isNull(ifSet)) {
            if (field.getType() == String.class) {
                SourceReflect.value(entity, primaryKey, UUID.randomUUID().toString());
            } else if (field.getType() == UUID.class) {
                SourceReflect.value(entity, primaryKey, UUID.randomUUID());
            }
        }
    }
}
