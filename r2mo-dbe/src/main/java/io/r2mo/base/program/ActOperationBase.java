package io.r2mo.base.program;

import io.r2mo.dbe.common.DBE;
import io.r2mo.dbe.common.DBETool;
import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.service.ActOperation;
import io.r2mo.typed.service.ActResponse;
import io.r2mo.typed.service.ActState;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author lang : 2025-09-04
 */
public abstract class ActOperationBase<T> implements ActOperation<T> {

    protected final Class<T> entityCls;

    @SuppressWarnings("unchecked")
    public ActOperationBase() {
        final Type genericType = this.getClass().getGenericSuperclass();
        if (genericType instanceof final ParameterizedType parameterizedType) {
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (0 < actualTypeArguments.length) {
                this.entityCls = (Class<T>) actualTypeArguments[0];
            } else {
                throw new IllegalStateException("[ R2MO ] 泛型定义长度不对！");
            }
        } else {
            throw new IllegalStateException("[ R2MO ] 泛型类型获取失败！");
        }
    }

    protected abstract DBE<?, T, ?> db();

    /*
     * 提供 CRUD 专用的 BaseController 所需的核心方法，有了此核心方法则可以直接针对实体类进行 CRUD 相关操作，
     * 不仅如此还可以通过生成的模式划定 CRUD 的范围
     */
    // POST /???/entity
    @Override
    public ActResponse<T> create(final T entity) {
        // 1. 提取统一标识符
        final Map<String, Object> condition = DBETool.getIdentifier(entity);
        final Optional<T> queried = this.db().findOne(condition);
        // 2. 如果存在则拒绝创建
        if (queried.isPresent()) {
            // 201
            return ActResponse.success201(entity);
        } else {
            // 200
            final T created = this.db().create(entity);
            return ActResponse.success(created);
        }
    }


    @Override
    public ActResponse<T> updateById(final Serializable id, final T entity) {
        final Optional<T> queried = this.db().findOne(id);
        if (queried.isPresent()) {
            // 200 更新
            final T updated = this.db().update(entity);
            return ActResponse.success(updated);
        } else {
            // 204 无数据
            return ActResponse.success204();
        }
    }

    @Override
    public ActResponse<Boolean> removeById(final Serializable id) {
        final Optional<T> queried = this.db().findOne(id);
        if (queried.isPresent()) {
            // 200 删除
            final Boolean removed = this.db().removeBy(id);
            return ActResponse.success(removed);
        } else {
            // 210 数据已不存在
            return ActResponse.of(false, ActState.SUCCESS_210_GONE);
        }
    }

    @Override
    public ActResponse<T> findById(final Serializable id) {
        final Optional<T> queried = this.db().findOne(id);
        // 200 查询到数据 / 204 无数据
        return queried.map(ActResponse::success).orElseGet(ActResponse::success204);
    }

    @Override
    public ActResponse<List<T>> findAll() {
        final List<T> queried = this.db().findAll();
        return ActResponse.success(queried);
    }

    @Override
    public ActResponse<List<T>> findBy(final JObject criteria) {
        final List<T> queried = this.db().findMany(criteria);
        return ActResponse.success(queried);
    }

    @Override
    public ActResponse<Pagination<T>> findPage(final JObject query) {
        final Pagination<T> queried = this.db().findPage(query);
        return ActResponse.success(queried);
    }

    @Override
    public ActResponse<List<T>> saveBatch(final List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return ActResponse.of(new ArrayList<>(), ActState.SUCCESS_204_NO_DATA);
        }
        final List<T> saved = this.db().save(entities);
        return ActResponse.success(saved);
    }
}
