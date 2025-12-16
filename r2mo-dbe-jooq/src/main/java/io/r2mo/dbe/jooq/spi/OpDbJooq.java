package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.constant.OpType;
import io.r2mo.base.dbe.operation.OpDb;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.common.Compared;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.InsertValuesStepN;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.UpdateConditionStep;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-10-19
 */
@Slf4j
class OpDbJooq<T> extends AbstractDbJooq<T> implements OpDb<T> {
    private final QrManyJooq<T> qrMany;

    // SPI -> QrAnalyzer -> 也可以自己定义
    protected OpDbJooq(final Class<T> entityCls, final DSLContext context) {
        super(entityCls, context);
        this.qrMany = new QrManyJooq<>(entityCls, context);
    }

    @Override
    public T execute(final T entity, final OpType opType) {
        if (Objects.isNull(entity)) {
            return null;
        }
        this.setter.setPrimaryKey(entity, opType);

        // Execute
        return switch (opType) {
            case CREATE -> this.insert(entity);
            case UPDATE -> this.update(entity);
            case REMOVE -> this.remote(entity);
            case SAVE -> this.save(entity);
        };
    }

    @Override
    public List<T> execute(final List<T> entities, final OpType opType, final int batchSize) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }

        this.setter.setPrimaryKey(entities, opType);

        return switch (opType) {
            case CREATE -> this.insertBatch(entities);
            case UPDATE -> this.updateBatch(entities);
            case REMOVE -> this.remoteBatch(entities);
            case SAVE -> this.saveBatch(entities);
        };
    }

    // 删除 / 批量删除
    private T remote(final T remove) {
        final Object id = this.meta.keyPrimary(remove);
        final Condition condition = this.analyzer().where(this.meta.keyPrimary(), id);
        final int rows = this.executor().deleteFrom(this.meta.table())
            .where(condition)
            .execute();
        this.logInfo("{} --> 删除数据，影响行数：`{}`", this.meta.tableName(), rows);
        return remove;
    }

    private List<T> remoteBatch(final List<T> removes) {
        final List<Object> ids = this.meta.keyPrimary(removes);
        final Condition condition = this.analyzer().whereIn(this.meta.keyPrimary(), ids);
        final int rows = this.executor().deleteFrom(this.meta.table())
            .where(condition)
            .execute();
        this.logInfo("{} --> 批量删除数据，影响行数：`{}`", this.meta.tableName(), rows);
        return removes;
    }


    // 保存 / 批量保存
    private T save(final T entity) {
        final Record record = this.setter.createRecord(entity);
        final InsertOnDuplicateSetMoreStep<?> insertStep = this.executor()
            .insertInto(this.meta.table())
            .set(record)
            .onDuplicateKeyUpdate()
            .set(record);
        final int rows = insertStep.execute();
        this.logInfo("{} --> 保存数据，影响行数：`{}`", this.meta.tableName(), rows);
        return entity;
    }

    private List<T> saveBatch(final List<T> entities) {
        final List<Object> ids = this.meta.keyPrimary(entities);
        final String primaryKey = this.meta.keyPrimary();


        final Condition condition = this.analyzer().whereIn(this.meta.keyPrimary(), ids);
        final List<T> stored = this.qrMany.findMany(condition);


        final Compared<T> compared = R2MO.elementDiff(stored, entities, primaryKey);
        final List<T> processed = new ArrayList<>();
        processed.addAll(this.insertBatch(compared.queueC()));
        processed.addAll(this.updateBatch(compared.queueU()));


        final int rows = processed.size();
        this.logInfo("{} --> 批量保存数据，影响行数：`{}`", this.meta.tableName(), rows);
        return processed;
    }

    // 插入 / 批量插入
    public T insert(final T entity) {
        final InsertSetMoreStep<?> insertStep = this.executor()
            .insertInto(this.meta.table())
            .set(this.setter.createRecord(entity));
        final int rows = insertStep.execute();
        this.logInfo("{} --> 插入数据，影响行数：`{}`", this.meta.tableName(), rows);
        return entity;
    }

    private List<T> insertBatch(final List<T> entities) {
        this.setter.setPrimaryKey(entities);
        final InsertSetStep<?> insertStep = this.executor().insertInto(this.meta.table());
        InsertValuesStepN<?> insertValuesStepN = null;
        for (final T pojo : entities) {
            insertValuesStepN = insertStep.values(this.setter.createRecord(pojo).intoArray());
        }
        if (Objects.isNull(insertValuesStepN)) {
            return new ArrayList<>();
        }
        final int rows = insertValuesStepN.execute();
        this.logInfo("{} --> 批量插入数据，影响行数：`{}`", this.meta.tableName(), rows);
        return entities;
    }

    // 更新 / 批量更新
    @SuppressWarnings("all")
    public T update(final T entity) {
        final int rows = this.updateStep(entity).execute();
        this.logInfo("{} --> 更新数据，影响行数：`{}`", this.meta.tableName(), rows);
        return entity;
    }

    private List<T> updateBatch(final List<T> entities) {
        final List<Query> batchOps = new ArrayList<>();
        entities.stream().map(this::updateStep).forEach(batchOps::add);
        final int[] rows = this.executor().batch(batchOps).execute();
        final long updated = Arrays.stream(rows).filter(value -> 1 == value).count();
        this.logInfo("{} --> 批量更新数据，影响行数：`{}/{}`", this.meta.tableName(), updated, rows.length);
        return entities;
    }

    private void logInfo(final String message, final Object... args) {
        log.info("[ R2MO ] ( Jooq ) " + message, args);
    }

    @SuppressWarnings("all")
    private UpdateConditionStep updateStep(final T entity) {
        Objects.requireNonNull(entity);
        org.jooq.Record record = this.executor().newRecord(this.meta.table(), entity);
        UniqueKey<?> pk = this.meta.table().getPrimaryKey();
        Objects.requireNonNull(pk,
            "[ R2MO ] ( Jooq ) 实体类 " + this.meta.entityCls().getName() + " 未定义主键，无法执行更新操作。");
        final Set<Condition> conditions = new HashSet<>();
        for (TableField<?, ?> tableField : pk.getFields()) {
            record.touched(tableField, false);
            final Condition condition = ((TableField<org.jooq.Record, Object>) tableField).eq(record.get(tableField));
            conditions.add(condition);
        }
        final Condition where = DSL.and(conditions);
        Map<String, Object> valuesToUpdate =
            Arrays.stream(record.fields())
                .collect(HashMap::new, (m, f) -> m.put(f.getName(), f.getValue(record)), HashMap::putAll);
        return this.executor().update(this.meta.table()).set(valuesToUpdate).where(where);
    }
}
