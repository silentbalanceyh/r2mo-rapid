package io.r2mo.base.dbe.operation;

import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.Optional;

/**
 * 新增 JOIN 接口，用于双表或多表查询的抽象层，带 As 是抽象层方法
 *
 * @author lang : 2025-10-23
 */
public interface OpJoin<CONDITION> {

    // --------------- 查询专用方法
    JArray findMany(CONDITION condition);

    JObject findOne(CONDITION condition);

    JObject findPage(QQuery query);

    JObject findById(Serializable id);

    Optional<Long> count(CONDITION condition);

    /*
     * Join 的写方法只支持单表操作，不支持多表，多表情况复杂，使用场景有限，后期版本考虑支持
     */
    // -------------- 写专用方法
    JObject create(JObject latest);

    Boolean removeById(Serializable id);

    Boolean removeBy(CONDITION condition);
    // === Update（按 ID）===

    /** 按主键 ID 更新（核心泛型版本，返回更新后的实体） */

    JObject updateById(Serializable id, JObject latest);

    JObject update(CONDITION condition, JObject latest);

}
