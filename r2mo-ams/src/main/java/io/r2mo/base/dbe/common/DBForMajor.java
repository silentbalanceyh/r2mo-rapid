package io.r2mo.base.dbe.common;

import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 主 JOIN 实体转换器，一般用于
 * <pre>
 *     1. 添加过程中的主键 JOIN 实体
 *     2. 别名计算 / 替换
 *        主键 UUID 值提取，后期可扩展到 String 或
 * </pre>
 *
 * @author lang : 2025-10-24
 */
@Slf4j
class DBForMajor implements DBFor {
    @Override
    public JObject exchange(final JObject request,
                            final DBNode current, final DBRef ref) {
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
            final String overwrite = alias.name();
            final Object value = request.get(aliasName);
            request.put(overwrite, value);
            log.debug("[ R2MO ] 替换的表名别名：[ {} ] -> [ {} ]，对应的值：[ {} ]", aliasName, overwrite, value);
        }

        return request;
    }
}
