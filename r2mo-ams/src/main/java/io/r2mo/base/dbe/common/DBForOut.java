package io.r2mo.base.dbe.common;

import io.r2mo.typed.json.JObject;

/**
 * @author lang : 2025-10-24
 */
class DBForOut implements DBFor {
    @Override
    public JObject exchange(final JObject response, final DBNode current, final DBRef ref) {
        final JObject processed = response.copy();
        T.doExchange(current, ref, alias -> {
            // 正式的别名
            final String aliasName = alias.alias();
            // 正式的别名
            final String overwrite = alias.name();
            final Object value = processed.get(overwrite);
            processed.put(aliasName, value);
        });
        return processed;
    }
}
