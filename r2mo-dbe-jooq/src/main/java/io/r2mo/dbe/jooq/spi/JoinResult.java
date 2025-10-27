package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBAlias;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.program.R2Vector;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.TableField;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-26
 */
class JoinResult {
    private static final Cc<String, JoinResult> CC_RESULT = Cc.openThread();
    private final DBRef ref;
    private final Map<String, Map<String, String>> aliasOut = new HashMap<>();

    private JoinResult(final DBRef ref) {
        this.ref = ref;
        this.ref.findAlias().forEach(alias -> {
            final DBAlias aliasObj = this.ref.findAlias(alias);
            final Map<String, String> vector = this.aliasOut.computeIfAbsent(aliasObj.table(), k -> new HashMap<>());
            vector.put(aliasObj.name(), alias);
            this.aliasOut.put(aliasObj.table(), vector);
        });
    }

    static JoinResult of(final DBRef ref) {
        return CC_RESULT.pick(() -> new JoinResult(ref), String.valueOf(ref.hashCode()));
    }

    JArray toResponse(final Result<Record> result) {
        final JArray array = SPI.A();
        for (final Record record : result) {
            final JObject item = this.toResponse(record);
            array.add(item.data());
        }
        return array;
    }

    /**
     * 结果转换必须要指出的点
     * <pre>
     *     1. 主实体的字段优先填充，辅助实体的字段后续填充
     *     2. 如果主实体和辅助实体存在同名字段，则以主实体的字段为准，辅助实体的字段忽略
     *     3. 但是重名时若提供了别名设置，那么谁的别名谁负责
     *     4. 别名相同的情况下，直接构造时就会抛错！
     * </pre>
     *
     * @param record 数据记录集
     *
     * @return JObject
     */
    JObject toResponse(final Record record) {
        final Field<?>[] fields = record.fields();
        final JObject result = SPI.J();
        final ConcurrentMap<String, Set<Field<?>>> grouped = new ConcurrentHashMap<>();
        for (final Field<?> field : fields) {
            final Kv<String, String> kv = this.findAlias(field);
            grouped.computeIfAbsent(kv.key(), k -> ConcurrentHashMap.newKeySet()).add(field);
        }

        // 主实体优先处理
        final DBNode found = this.ref.find();
        this.setValue(result, record, grouped, found);

        // 辅助实体后续处理
        this.ref.findByExclude(found.entity()).forEach(each ->
            this.setValue(result, record, grouped, each));
        return result;
    }

    private Kv<String, String> findAlias(final Field<?> field) {
        if (field instanceof final TableField<?, ?> tableField) {
            final Name name = Objects.requireNonNull(tableField.getTable()).getUnqualifiedName();
            return Kv.create(name.last(), field.getName());
        }
        // 比较广泛的处理方法
        final String name = field.getName();
        if (name.contains(".")) {
            return Kv.create(
                name.substring(0, name.indexOf(".")),
                name.substring(name.indexOf(".") + 1)
            );
        }
        throw new _501NotSupportException("[ R2MO ] 无法解析 JOOQ Field 的别名信息: " + field);
    }

    private void setValue(final JObject result, final Record record, final ConcurrentMap<String, Set<Field<?>>> grouped,
                          final DBNode found) {
        // 提取主实体字段
        final String prefix = this.ref.seekAlias(found.entity());
        final Map<String, String> aliasMap = this.aliasOut.get(found.table());
        final Set<Field<?>> fieldSet = grouped.get(prefix);
        fieldSet.forEach(field -> {
            // 提取值
            final Object value = record.get(field);
            final Kv<String, String> kv = this.findAlias(field);
            final String vProperty = found.vProperty(kv.value());
            if (Objects.nonNull(aliasMap) && aliasMap.containsKey(vProperty)) {
                // 别名填充后，主实体就不会填充属性了，辅助实体就可以填充
                final String aliasName = aliasMap.get(vProperty);
                result.put(aliasName, value);
            } else {
                // 主表属性直接填充
                if (!result.containsKey(vProperty)) {
                    this.setValue(result, vProperty, value, found);
                }
            }
        });
    }

    private void setValue(final JObject result, final String vProperty, final Object value,
                          final DBNode found) {
        final R2Vector vector = found.vector();
        if (vector.hasMapping()) {
            final String vPropertyOut = vector.mapTo(vProperty);
            result.put(vPropertyOut, value);
        } else {
            // 主表字段，直接填
            result.put(vProperty, value);
        }
    }
}
