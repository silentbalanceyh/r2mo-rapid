package io.r2mo.base.dbe.join;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.program.R2Vector;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <pre>
 *     参考 SQL 语句
 * SELECT
 *     emp.employee_id,
 *     emp.name,
 *     dept.department_name
 * FROM
 *     employees AS emp
 * LEFT JOIN
 *     departments AS dept ON emp.department_id = dept.department_id;
 * WHERE
 *     dept.department_name = 'Engineering';
 * </pre>
 *
 * @author lang : 2025-10-22
 */
@Slf4j
@Data
@Accessors(chain = true, fluent = true)
public class DBNode implements Serializable {
    private Class<?> entity;    // 实体名 / 或 Dao 名        = Department
    private String table;       // 表名                     = departments
    private String key;         // 主键字段                 = id

    public String name() {
        return this.entity.getName();
    }

    /**
     * 包含了两个哈希表
     * <pre>
     *     1. 字段映射哈希表：field = fieldJson
     *     2. 字段列映射哈希表：field = fieldColumn
     * </pre>
     */
    private R2Vector vector;    // 用于处理字段匿名等各种操作
    private ConcurrentMap<String, Class<?>> types = new ConcurrentHashMap<>();

    public DBNode put(final String field, final Class<?> type) {
        if (StrUtil.isEmpty(field) || Objects.isNull(type)) {
            log.warn("[ R2MO ] 字段无法添加：{} of `{}`", field, type);
            return this;
        }
        this.types.put(field, type);
        return this;
    }
}
