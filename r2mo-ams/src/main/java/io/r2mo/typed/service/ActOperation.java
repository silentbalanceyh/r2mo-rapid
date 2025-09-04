package io.r2mo.typed.service;

import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.List;

/**
 * @author lang : 2025-09-04
 */
public interface ActOperation<T> {
    // POST /???/entity
    ActResponse<T> create(T entity);

    // PUT /???/entity/{id}
    ActResponse<T> updateById(Serializable id, T entity);

    // DELETE /???/entity/{id}
    ActResponse<Boolean> removeById(Serializable id);

    // GET /???/entity/{id}
    ActResponse<T> findById(Serializable id);

    // GET /???/entity-all
    default ActResponse<List<T>> findAll() {
        return this.findAll(null, null);
    }

    // GET /???/entity-all
    default ActResponse<List<T>> findAll(final Serializable appId) {
        return this.findAll(appId, null);
    }

    // GET /???/entity-all
    ActResponse<List<T>> findAll(Serializable appId, Serializable tenantId);

    // 导出：POST /???/entity/export
    ActResponse<List<T>> findBy(JObject criteria);

    // 分页：POST /???/entity/search
    ActResponse<Pagination<T>> findPage(JObject query);

    // 导入：POST /???/entity/import
    ActResponse<List<T>> saveBatch(List<T> entities);
}
