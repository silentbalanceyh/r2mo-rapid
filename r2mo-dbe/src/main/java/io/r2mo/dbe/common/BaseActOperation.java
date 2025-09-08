package io.r2mo.dbe.common;

import io.r2mo.SourceReflect;
import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.service.ActOperation;
import io.r2mo.typed.service.ActResponse;
import io.r2mo.typed.service.ActState;

import java.io.Serializable;
import java.util.*;

/**
 * @author lang : 2025-09-04
 */
public abstract class BaseActOperation<T> implements ActOperation<T> {

    protected final Class<T> entityCls;

    public BaseActOperation() {
        this.entityCls = SourceReflect.classT0(this.getClass());
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
            return ActResponse.success(entity, ActState.SUCCESS_201_CREATED);
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
            return ActResponse.success();
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
            // 204 数据已经被删除（无数据）
            return ActResponse.success(false, ActState.SUCCESS_204_NO_DATA);
        }
    }

    @Override
    public ActResponse<T> findById(final Serializable id) {
        final Optional<T> queried = this.db().findOne(id);
        // 200 查询到数据
        return queried.map(ActResponse::success)
            // 204 无数据
            .orElseGet(ActResponse::success);
    }

    @Override
    public ActResponse<List<T>> findAll(final Serializable appId, final Serializable tenantId) {
        final Map<String, Object> condition = new HashMap<>();
        if (Objects.nonNull(appId)) {
            condition.put("appId", appId);
        }
        if (Objects.nonNull(tenantId)) {
            condition.put("tenantId", tenantId);
        }
        final List<T> queried;
        if (condition.isEmpty()) {
            queried = this.db().findAll();
        } else {
            queried = this.db().findMany(condition);
        }
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
            return ActResponse.success(new ArrayList<>(), ActState.SUCCESS_204_NO_DATA);
        }
        final List<T> saved = this.db().save(entities);
        return ActResponse.success(saved);
    }
}
