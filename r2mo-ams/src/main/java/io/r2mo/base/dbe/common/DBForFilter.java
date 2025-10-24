package io.r2mo.base.dbe.common;

import io.r2mo.typed.common.Kv;
import io.r2mo.typed.json.JObject;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-10-24
 */
class DBForFilter implements DBFor {
    @Override
    public JObject exchange(final JObject request, final DBRef ref) {
        // 主实体的主键移除
        final JObject processed = request.copy();
        final DBNode first = ref.findPrimary();
        final String vPrimary = first.key().value();
        processed.remove(vPrimary);

        this.removeJoined(processed, ref, first);
        // 辅助实体的主键移除
        final Set<DBNode> childSet = ref.findByExclude(first.entity());
        childSet.forEach(child -> {
            final String cPrimary = child.key().value();
            processed.remove(cPrimary);

            this.removeJoined(processed, ref, child);
        });
        return processed;
    }

    private void removeJoined(final JObject processed, final DBRef ref,
                              final DBNode node) {

        // 连接键值的移除
        final Set<Kv<String, String>> joined = ref.seekJoinOn(node.entity());
        if (Objects.isNull(joined)) {
            // 当前实体是主实体
            ref.seekJoinOn().forEach(kv -> {
                final String joinValue = kv.key();
                processed.remove(joinValue);
            });
        } else {
            joined.forEach(kv -> {
                final String joinKey = kv.value();
                processed.remove(joinKey);
            });
        }
    }
}
