package io.r2mo.base.dbe.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.SourceReflect;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
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

        // 返回的 id 如果不是主实体的 id，则要移除过后切换成主实体的 id 信息
        // 旧代码：删除太早了，map 的时候会出错
        //        final DBNode nodeMajor = this.ref.find();
        //        if (nodeMajor.entity() != current.entity()) {
        //            final Kv<String, String> kv = nodeMajor.key();
        //            exchanged.remove(kv.value());
        //        }

        final DBNode nodeMajor = this.ref.find();
        minorSet.forEach(minor -> {
            final Class<?> minorCls = minor.getClass();
            final JObject serialized0 = SPI.V_UTIL.serializeJson(minor);

            final DBNode child = this.ref.findBy(minorCls);
            final JObject exchanged0 = DBFor.ofOut().exchange(serialized0, child, this.ref);


            // 拼接子实体，在 exchanged 中追加 exchanged0
            for (final String field : exchanged0.fieldNames()) {
                // FIX-DBE: 主实体检查（多表JOIN会碰上）
                /*
                 * 当主实体和子实体相同时，说明当前的 child 就是主实体（非主键实体）此时返回的 id 应该是主实体 id，而不应该是
                 * 主键实体 id，主键实体 id 无法直接返回单数据，因为主键实体 id 通常会导致多条数据，此时的 id 无法判别 JOIN
                 * 之后的结果，如果是 findMany 的模式，由于主实体会优先处理序列化，所以不会导致 id 的唯一问题，但是如果在单独
                 * 数据的增删改的时候，必须要保证这个 id 的唯一性。
                 */
                if (nodeMajor.entity() == child.entity()) {
                    // 此时的 child 是主实体
                    final Kv<String, String> kv = nodeMajor.key();
                    if (field.equals(kv.value())) {
                        // 只会执行一次（覆盖）
                        exchanged.put(field, exchanged0.get(field));
                    }
                }


                if (exchanged.containsKey(field)) {
                    continue;
                }
                exchanged.put(field, exchanged0.get(field));
            }
        });
        // 此处要使用主实体的 ID 做对应的值返回
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

    /**
     * FIX-DBE: 此处的主表不需要计算，因为采用了类似：
     * <pre>
     *     SELECT *, TRX.column AS alias FROM ???
     * </pre>
     * 的结构，如果出现了同名，那么主表的信息一定会在前边而且不会被覆盖掉，所以数据库自己就完成了主表和别名的区分，而且在这种模式
     * 之下不用去考虑计算谁的字段优先级高的情况，这样就导致了 id 列一定会在前边。
     *
     * @param row 数据库行数据
     *
     * @return 结果
     */
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
