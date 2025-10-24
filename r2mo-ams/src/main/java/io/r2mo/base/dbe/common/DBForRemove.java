package io.r2mo.base.dbe.common;

import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.json.JObject;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-10-24
 */
class DBForRemove implements DBFor {
    @Override
    public JObject exchange(final JObject request, final DBNode current, final DBRef ref) {
        // 辅助实体的删除条件，此时的辅助实体有可能是 JOIN 的主实体
        final Set<Kv<String, String>> join = ref.seekJoinOn(current.entity());
        final JObject condition = SPI.J();
        if (Objects.isNull(join)) {
            // current 是主实体
            ref.seekJoinOn().forEach(kv -> {
                final String value = kv.value();
                final String valueColumn = current.vColumn(value);
                condition.put(valueColumn, request.get(value));
            });
        } else {
            /*
             * FIX-DBE: 双向检查模式，保证最终一定可以删除数据本身，此处直接使用 join 信息的左右值分别在当前节点中去提取列
             * 能提取证明是对的，不能提取就是方向反了。
             */
            join.forEach(kv -> {
                final String key = kv.key();
                final String keyColumn = current.vColumn(key);
                if (Objects.nonNull(keyColumn)) {
                    // 如果 keyColumn 不为空
                    final String value = kv.value();
                    condition.put(keyColumn, request.get(value));
                } else {
                    // keyColumn 为空的时候，说明方向反了
                    final String value = kv.value();
                    final String valueColumn = current.vColumn(value);
                    condition.put(valueColumn, request.get(value));
                }
            });
        }
        return condition;
    }
}
