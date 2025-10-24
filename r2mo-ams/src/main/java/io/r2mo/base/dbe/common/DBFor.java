package io.r2mo.base.dbe.common;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._501NotSupportException;
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

    static DBFor ofFilter() {
        return CCT_DB_FOR.pick(DBForFilter::new, DBForFilter.class.getName());
    }

    default JObject exchange(final JObject request, final DBNode current, final DBRef ref) {
        throw new _501NotSupportException("[ R2MO ] 当前 DBFor 未实现此主方法！");
    }

    /**
     * 特殊的 exchange 方法，在 updatedBy 的过程中，直接将最新的数据作为 inputJ 传入，然后要删除掉
     * 不可更新的字段
     * <pre>
     *     1. 主实体的主键不可更新
     *     2. 辅助实体的主键不可更新
     *     3. Joined 键不可更新
     * </pre>
     *
     * @param inputJ 输入的数据
     * @param ref    引用
     *
     * @return 处理后
     */
    default JObject exchange(final JObject inputJ, final DBRef ref) {
        return inputJ;
    }

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
