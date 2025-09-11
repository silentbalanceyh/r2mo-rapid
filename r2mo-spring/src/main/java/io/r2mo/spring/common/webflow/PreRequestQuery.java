package io.r2mo.spring.common.webflow;

import io.r2mo.spi.SPI;
import io.r2mo.typed.annotation.Identifiers;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.constant.DefaultField;
import io.r2mo.typed.json.JObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-09
 */
class PreRequestQuery {
    private static final Cc<String, PreRequestQuery> CCT_APPLY = Cc.openThread();

    private PreRequestQuery() {
    }

    static PreRequestQuery of() {
        return CCT_APPLY.pick(PreRequestQuery::new);
    }

    JObject withScope(final PreRequestContext context, final JObject condition, final Class<?> clazz) {
        final Identifiers identifiers = clazz.getDeclaredAnnotation(Identifiers.class);
        if (Objects.isNull(identifiers)) {
            return condition;
        }
        if (identifiers.ifApp()) {
            final UUID appId = context.appId(true);
            condition.put(DefaultField.APP_ID, appId);
        }
        if (identifiers.ifTenant()) {
            final UUID tenantId = context.tenantId(true);
            condition.put(DefaultField.TENANT_ID, tenantId);
        }
        return condition;
    }

    JObject withMapN(final PreRequestContext context, final Class<?> clazz, final Object... kv) {
        if ((kv.length & 1) != 0) {
            throw new IllegalArgumentException("[ R2MO ] -> 传入的键值对参数必须为偶数个！");
        }
        final Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            final String key = String.valueOf(kv[i]);
            final Object value = kv[i + 1];
            map.put(key, value);
        }
        final JObject condition = SPI.J().put(map);
        this.withScope(context, condition, clazz);
        return condition;
    }
}
