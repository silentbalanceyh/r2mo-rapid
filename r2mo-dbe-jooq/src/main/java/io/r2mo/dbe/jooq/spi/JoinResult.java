package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBAlias;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.program.R2Vector;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableField;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    JObject toResponse(final Record record) {
        final Field<?>[] fields = record.fields();
        final JObject result = SPI.J();
        final String alias = this.ref.seekAlias(this.ref.find().entity());
        for (final Field<?> field : fields) {
            // 提取值
            final Object value = record.get(field);


            final TableField<?, ?> tableField = (TableField<?, ?>) field;
            final Class<?> metaCls = this.ref.seekTypeByColumn(field.getName());

            // 检索属性名
            final DBNode found = this.ref.findBy(metaCls);
            final String vProperty = found.vProperty(field.getName());


            // 是否包含别名处理，如果有别名处理，优先考虑别名填充，只要表匹配，就填充一次
            final Map<String, String> aliasMap = this.aliasOut.get(found.table());
            if (Objects.nonNull(aliasMap) && aliasMap.containsKey(vProperty)) {
                final String aliasName = aliasMap.get(vProperty);
                result.put(aliasName, value);
            }


            // 共享字段要主表先填充 / Left 表先处理
            final String currentAlias = Objects.requireNonNull(tableField.getTable()).getUnqualifiedName().last();
            if (alias.equals(currentAlias)) {
                // 主表字段，直接填
                this.setValue(result, vProperty, value, found);
                continue;
            }

            // 非主表字段，检查是否已经存在
            if (!result.containsKey(vProperty)) {
                // 主表字段，直接填
                this.setValue(result, vProperty, value, found);
            }
        }
        return result;
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
