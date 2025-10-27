package io.r2mo.dbe.jooq.core.domain;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.exception.web._501NotSupportException;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-18
 */
@Slf4j
class JooqField {
    private R2Vector vector;
    // 这个变量是锁定的，不会改动
    private final ConcurrentMap<String, Field<?>> fieldColumn = new ConcurrentHashMap<>();
    // 这个变量是会变化的，会随着 vector 的变化而追加
    private final ConcurrentMap<String, Class<?>> fieldType = new ConcurrentHashMap<>();

    JooqField() {
    }

    void vector(final R2Vector vector) {
        this.vector = vector;
    }

    /*
     * 此处已经绑定过两个核心变量，所以不再考虑在 vector 过程设置过程中来进行二次绑定
     */
    void put(final java.lang.reflect.Field field, final Field<?> column) {
        this.fieldColumn.put(field.getName(), column);
        this.fieldType.put(field.getName(), field.getType());
    }

    Field<?>[] findColumn(final String... fields) {
        return Arrays.stream(fields)
            .map(this::findColumn)
            .filter(Objects::nonNull)
            .toArray(Field[]::new);
    }

    ConcurrentMap<String, Field<?>> fieldColumns() {
        return this.fieldColumn;
    }

    Field<?> findColumn(final String fieldOr) {
        final String columnName = this.nameOfColumn(fieldOr);
        if (Objects.isNull(columnName)) {
            throw new _501NotSupportException("[ R2MO ] 无法找到对应的 Column 名称: 输入字段 = " + fieldOr);
        }
        log.debug("[ R2MO ] 寻找 JOOQ Column: 输入字段 = {}, 目标 Column = {}", fieldOr, columnName);
        Field<?> found;
        if (fieldOr.equals(columnName)) {
            // 直接等于，双保险
            /*
             * 如果计算的 columnName 和 fieldOr 一样，说明 fieldOr 本身就是 column 名称
             */
            found = this.fieldColumn.get(fieldOr);
        } else {
            // 不相等，等价于要提取实际值 name
            final String field = this.vector.mapByColumn(columnName);
            found = this.fieldColumn.get(field);
        }
        if (Objects.isNull(found)) {
            // 未找到对应的 Column，直接使用
            found = DSL.field(DSL.name(columnName));
        }
        return found;
    }

    private String nameOfColumn(final String fieldOr) {
        final String targetField;
        if (this.vector.hasMapping()) {
            // 映射模式，先转换 fieldJson -> field -> column
            /*
             * 参数 field 是输入的 field 字段，本身做过映射处理
             *
             * - field = 输入字段
             * - value = 实际字段
             *
             * 双检查模型
             */
            final String field = this.fieldBy(fieldOr);
            targetField = this.columnBy(field);
        } else {
            // 无映射模式，直接返回 column 名称
            targetField = this.columnBy(fieldOr);
        }
        return targetField;
    }

    private String fieldBy(final String field) {
        String fieldField;
        if (this.vector.hasMapping()) {
            /*
             * 情况1：field 本身就是 fieldJson，直接模式，所以这种情况既不需要从 mapTo / mapBy 中计算
             *       field = FIELD_JSON -> mapBy -> FIELD
             */
            fieldField = this.vector.mapBy(field);
            if (StrUtil.isEmpty(fieldField)) {
                // 回退、还原
                fieldField = field;
            }
        } else {
            /*
             * 情况2：field 本身就是 FIELD，直接模式
             */
            fieldField = field;
        }
        return fieldField;
    }

    ConcurrentMap<String, Class<?>> fieldType() {
        if (Objects.nonNull(this.vector)) {
            /*
             * 延迟填充，避免重复，此处的 typeMap 会包含两种
             * 1）field -> Class<?>
             * 2）column -> Class<?>
             * 所以需要进行二次过滤，根据过滤信息来提取最终的数据信息
             */
            this.fieldColumn.forEach((field, columnField) -> {
                final String columnName = this.vector.mapTo(field);
                if (StrUtil.isNotEmpty(columnName)) {
                    this.fieldType.put(columnName, columnField.getType());
                }
            });
        }
        return this.fieldType;
    }

    /**
     * 此处的 field 一定是 FIELD 字段, 不能是 FIELD_JSON
     *
     * @param field 输入字段
     *
     * @return 返回对应的 COLUMN 字段
     */
    private String columnBy(final String field) {
        final String columnField;
        /*
         * 提取 column -> field 的映射关系，查看这个 field 是不是 column
         * 如果已经是 column 了，直接返回即可
         */
        final ConcurrentMap<String, String> mapByColumn = this.vector.mapByColumn();
        if (mapByColumn.containsKey(field)) {


            /*
             * 情况1：field 本身就是 column，直接模式，所以这种情况既不需要从 mapToColumn / mapByColumn 中计算
             *       field = COLUMN
             */
            columnField = field;
        } else {


            /*
             * 情况2：field 本身不是 column，而是 field 字段，这种情况要从 mapToColumn 中计算得到最终的 COLUMN
             *       field = FIELD -> mapToColumn -> COLUMN
             */
            columnField = this.vector.mapToColumn(field);
        }
        return columnField;
    }
}
