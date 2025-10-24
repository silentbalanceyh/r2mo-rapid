package io.r2mo.base.dbe.common;

import cn.hutool.core.util.StrUtil;
import io.r2mo.SourceReflect;
import io.r2mo.base.program.R2Mapping;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.common.Kv;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 此数据结构可用于 JOIN，也可独立使用，用来描述基本数据信息，其中包括
 * <pre>
 *     Java 类
 *     - entity / dao：主类和辅助类
 * </pre>
 *
 * @author lang : 2025-10-22
 */
@Slf4j
@Data
@Accessors(chain = true, fluent = true)
@ToString
public class DBNode implements Serializable {
    // ----------------------- 实体相关 ------------------------
    private Class<?> entity;                            // 实体名                   = Department
    private Class<?> dao;                               // DAO 名                  = DepartmentDAO
    private ConcurrentMap<String, Class<?>> types = new ConcurrentHashMap<>();

    // ----------------------- 二者共享 ------------------------
    /**
     * 特殊结构，里面包含两个核心
     * <pre>
     *     1. field -> fieldJson 的双向哈希
     *        {@link R2Mapping} 结构
     *     2. field -> Column 的双向哈希
     *        {@link R2Mapping} 结构
     * </pre>
     * 有了这个结构之后，旧版中的 f2c / c2f 就不再需要了，所以检索流程也变得简单许多
     */
    private R2Vector vector = new R2Vector();


    // ----------------------- 数据表相关 ------------------------
    private String table;                               // 表名                    = departments
    // column -> field
    private Kv<String, String> key;


    // ----------------------- 创建和构造 ------------------------

    private DBNode() {
    }

    public static DBNode of(final Class<?> daoClass, final R2Vector vector) {
        final DBNode node = new DBNode();


        /*
         * 此处设置 Dao 而不是设置 Entity 的主要原因
         * - Entity 可以在后期补全相关设置，而 Dao 类不可以，只能第一时间设置
         * - Dao 类中可通过运算提取 Entity 信息，反之则不行
         * 因此实体信息只能在后期来加载，加载过程中还需要完善 field = Class 的相关信息
         */
        node.dao(daoClass);


        /*
         * 如果传入的为 null，则需要创建一个空的 R2Vector 对象，R2Vector 对象的核心目的是存储映射关系，
         * 映射关系的存储包括
         * - 1. 字段映射 field -> fieldJson
         * - 2. 列映射   field -> column
         */
        node.vector(Objects.isNull(vector) ? new R2Vector() : vector);
        return node;
    }

    public static DBNode of() {
        return new DBNode();
    }

    // ------------------------ 读取专用方法 ---------------------

    public Class<?> entity() {
        if (Objects.nonNull(this.dao)) {
            return this.dao;
        }
        return this.entity;
    }

    public Kv<String, String> vId() {
        return Kv.create(this.table, this.key.value());
    }

    public String vColumn(final String field) {
        return Objects.requireNonNull(this.vector).mapToColumn(field);
    }

    public String vProperty(final String column) {
        return Objects.requireNonNull(this.vector).mapByColumn(column);
    }

    public Object vPrimary(final Object instance) {
        final String pkProperty = this.key.value();
        return SourceReflect.value(instance, pkProperty);
    }

    public void vPrimary(final Object instance, final Object value) {
        final String pkProperty = this.key.value();
        SourceReflect.value(instance, pkProperty, value);
    }

    // ------------------------ 绑定和设置 ----------------------
    public DBNode types(final String field, final Class<?> type) {
        if (StrUtil.isEmpty(field) || Objects.isNull(type)) {
            log.warn("[ R2MO ] 字段无法添加：{} of `{}`", field, type);
            return this;
        }
        this.types.put(field, type);
        return this;
    }
}
