package io.r2mo.base.dbe.common;

import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

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
class DBForAlias implements DBFor {
    @Override
    public JObject exchange(final JObject request,
                            final DBNode current, final DBRef ref) {
        final JObject processed = request.copy();
        T.doExchange(current, ref, alias -> {

            // 正式的别名
            final String aliasName = alias.alias();
            final String overwrite = alias.name();
            final Object value = processed.get(aliasName);
            processed.put(overwrite, value);
            log.debug("[ R2MO ] (In) 替换的表名别名：[ {} ] -> [ {} ]，对应的值：[ {} ]", aliasName, overwrite, value);
        });
        return processed;
    }
}
