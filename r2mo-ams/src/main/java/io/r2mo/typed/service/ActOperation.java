package io.r2mo.typed.service;

import io.r2mo.typed.common.Pagination;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 特殊用法而已，如果遇到其他的后期来扩展
 *
 * @author lang : 2025-09-04
 */
public interface ActOperation<T> {
    // -------------- 按ID处理 --------------
    // POST /???/entity
    ActResponse<T> create(T entity);

    // PUT /???/entity/{id}
    ActResponse<T> updateById(Serializable id, T entity);

    // DELETE /???/entity/{id}
    ActResponse<Boolean> removeById(Serializable id);

    // GET /???/entity/{id}
    ActResponse<T> findById(Serializable id);

    // --------------- 按条件查询所有 --------------
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

    // --------------- 特殊条件查询 --------------
    // 导出：POST /???/entity/export
    ActResponse<List<T>> findMany(JObject criteria);

    ActResponse<List<T>> findMany(Map<String, Object> criteria);

    ActResponse<T> findOne(JObject criteria);

    ActResponse<T> findOne(Map<String, Object> criteria);

    // --------------- 基础聚集和统计 ------------
    ActResponse<Long> count(JObject criteria);

    ActResponse<Long> count(Map<String, Object> criteria);

    // --------------- 其他常用查询 --------------
    // 分页：POST /???/entity/search
    ActResponse<Pagination<T>> findPage(JObject query);

    // 导入：POST /???/entity/import
    ActResponse<List<T>> saveBatch(List<T> entities);
}
