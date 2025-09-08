package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.r2mo.SourceReflect;
import io.r2mo.base.dbe.constant.OpType;
import io.r2mo.base.dbe.operation.OpDb;
import io.r2mo.dbe.common.operation.AbstractDbOperation;
import io.r2mo.dbe.mybatisplus.core.domain.BaseEntity;
import org.apache.ibatis.executor.BatchResult;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author lang : 2025-08-28
 */
class OpDbImpl<T, M extends BaseMapper<T>> extends AbstractDbOperation<QueryWrapper<T>, T, M> implements OpDb<T> {
    OpDbImpl(final Class<T> entityCls, final M m) {
        super(entityCls, m);
    }

    @Override
    public T execute(final T entity, final OpType opType) {
        if (Objects.isNull(entity)) {
            return null;
        }
        // Execute
        switch (opType) {
            case CREATE -> this.executor().insert(entity);
            case UPDATE -> this.executor().updateById(entity);
            case REMOVE -> this.executor().deleteById(entity);
            case SAVE -> this.executor().insertOrUpdate(entity);
        }
        return entity;
    }

    @Override
    public List<T> execute(final List<T> entities, final OpType opType, final int batchSize) {
        if (Objects.isNull(entities) || entities.isEmpty()) {
            return new ArrayList<>();
        }
        // Insert issue
        this.batchInsert(entities, opType);
        // Execute
        return switch (opType) {
            case CREATE -> this.buildResult(this.executor().insert(entities, batchSize));
            case UPDATE -> this.buildResult(this.executor().updateById(entities, batchSize));
            case REMOVE -> this.batchRemove(entities);
            case SAVE -> this.buildResult(this.executor().insertOrUpdate(entities, batchSize));
        };
    }

    @SuppressWarnings("all")
    private void batchInsert(final List<T> entities, final OpType opType) {
        // Fix Issue success id
        if (OpType.CREATE == opType) {
            for (final T entity : entities) {
                // 直接实体
                if (entity instanceof final BaseEntity baseEntity) {
                    if (Objects.isNull(baseEntity.getId())) {
                        baseEntity.setId(UUID.randomUUID());
                    }
                }
                // LinkedHashMap
                if (entity instanceof final LinkedHashMap baseMap) {
                    if (Objects.isNull(baseMap.get("id"))) {
                        baseMap.put("id", UUID.randomUUID());
                    }
                }
            }
        }
    }

    private List<T> batchRemove(final List<T> entities) {
        final List<Object> ids = new ArrayList<>();
        for (final T entity : entities) {
            // 第一规范提取
            if (entity instanceof final BaseEntity baseEntity) {
                ids.add(baseEntity.getId());
                continue;
            }

            // 第二规范提取
            final Field field = SourceReflect.fieldN(this.entityCls(), "id");
            if (Objects.isNull(field)) {
                continue;
            }
            final Object value = SourceReflect.value(entity, field.getName(), this.entityCls());
            if (Objects.nonNull(value)) {
                ids.add(value);
            }
        }
        this.executor().deleteByIds(ids);
        return entities;
    }

    @SuppressWarnings("unchecked")
    private List<T> buildResult(final List<BatchResult> batchResults) {
        final List<T> results = new ArrayList<>();
        batchResults.stream()
            .filter(item -> !item.getParameterObjects().isEmpty())
            .map(item -> item.getParameterObjects().get(0))
            // unchecked
            .map(item -> (T) item)
            .forEach(results::add);
        return results;
    }
}
