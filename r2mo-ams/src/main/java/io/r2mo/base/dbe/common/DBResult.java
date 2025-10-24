package io.r2mo.base.dbe.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.SourceReflect;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-10-24
 */
public class DBResult {

    private final DBRef ref;

    private DBResult(final DBRef ref) {
        this.ref = ref;
    }

    private static final Cc<String, DBResult> CC_RESULT = Cc.openThread();

    public static DBResult of(final DBRef ref) {
        final String cachedKey = String.valueOf(ref.hashCode());
        return CC_RESULT.pick(() -> new DBResult(ref), cachedKey);
    }

    public JObject build(final Object major, final Set<Object> minorSet,
                         final DBNode current) {
        final JObject serialized = SPI.V_UTIL.serializeJson(major);
        final JObject exchanged = DBFor.ofOut().exchange(serialized, current, this.ref);

        minorSet.forEach(minor -> {
            final Class<?> minorCls = minor.getClass();
            final JObject serialized0 = SPI.V_UTIL.serializeJson(minor);

            final DBNode child = this.ref.findBy(minorCls);
            final JObject exchanged0 = DBFor.ofOut().exchange(serialized0, child, this.ref);

            // 拼接子实体，在 exchanged 中追加 exchanged0
            exchanged0.fieldNames().forEach(field -> {
                if (!exchanged.containsKey(field)) {
                    exchanged.put(field, exchanged0.get(field));
                }
            });
        });
        return exchanged;
    }

    public JArray build(final List<Map<String, Object>> rows) {
        final JArray array = SPI.A();
        rows.stream().map(this::build)
            .filter(Objects::nonNull)
            // 过滤之后解开 JObject / JArray 的封装
            .map(JBase::data)
            .forEach(array::add);
        return array;
    }

    public JObject build(final Map<String, Object> row) {
        final JObject record = SPI.J();
        for (final String column : row.keySet()) {
            if (this.ref.isAlias(column)) {
                // 别名处理，直接启用别名完成所有的事
                record.put(column, row.get(column));
                continue;
            }
            final Object value = row.get(column);


            // 列转属性，但还需要处理 Class<?> 的提取
            final Class<?> metaCls = this.ref.seekTypeByColumn(column);


            // 只有实现类可以这样检索
            final DBNode found = this.ref.findBy(metaCls);
            final String vProperty = found.vProperty(column);


            // 此处由于做的是 Json 的序列化，所以还需要计算一次
            final Field field = SourceReflect.fieldN(metaCls, vProperty);
            Objects.requireNonNull(field, "[ R2MO ] 此处属性必然不为空！");
            final JsonProperty jProperty = field.getDeclaredAnnotation(JsonProperty.class);
            if (Objects.isNull(jProperty)) {
                record.put(vProperty, value);
            } else {
                record.put(jProperty.value(), value);
            }
        }
        return record;
    }
}
