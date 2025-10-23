package io.r2mo.base.dbe.join;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.program.R2Vector;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
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
@ToString
public class DBNode implements Serializable {
    private Class<?> entity;    // 实体名                   = Department
    private Class<?> dao;       // DAO 名                  = DepartmentDAO
    private String table;       // 表名                     = departments
    private String key;         // 主键字段                 = id

    private DBNode() {
    }

    public String name() {
        /*
         * FIX-DBE: 此处有可能出现 entity 为 null 的情况，导致 NPE 异常，所以修改成双模式，若 entity 为 null 则返回 dao 名称
         * 旧代码：this.entity.getName();
         **/
        return Optional.ofNullable(this.entity).map(Class::getName).orElse(
            Optional.ofNullable(this.dao).map(Class::getName).orElse(null)
        );
    }

    public static DBNode of(final Class<?> daoClass, final R2Vector vector) {
        final DBNode node = new DBNode();
        node.dao(daoClass);
        node.vector(vector);
        return node;
    }

    public static DBNode of() {
        return new DBNode();
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
