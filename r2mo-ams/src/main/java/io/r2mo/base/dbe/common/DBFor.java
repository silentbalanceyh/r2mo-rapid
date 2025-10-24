package io.r2mo.base.dbe.common;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;

import java.util.Set;
import java.util.function.Consumer;

/**
 * @author lang : 2025-10-24
 */
public interface DBFor {
    Cc<String, DBFor> CCT_DB_FOR = Cc.openThread();

    static DBFor ofAlias() {
        return CCT_DB_FOR.pick(DBForAlias::new, DBForAlias.class.getName());
    }

    static DBFor ofOut() {
        return CCT_DB_FOR.pick(DBForOut::new, DBForOut.class.getName());
    }

    JObject exchange(JObject request, DBNode current, DBRef ref);

    interface T {
        static void doExchange(final DBNode current, final DBRef ref,
                               final Consumer<DBAlias> consumer) {
            // 执行表名序列化
            final Set<String> aliasSet = ref.findAlias();
            final String table = current.table();

            for (final String aliasName : aliasSet) {
                final DBAlias alias = ref.findAlias(aliasName);
                // 过滤处理
                if (!table.equals(alias.table())) {
                    // 表名不同，别名不在当前对象中，直接跳过
                    continue;
                }

                // 正式的别名
                consumer.accept(alias);
            }
        }
    }
}
