package io.r2mo.typed.hutool.spi;

import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
class JUtilImpl implements JUtil {
    private static JUtil INSTANCE;

    private JUtilImpl() {
    }

    static JUtil getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new JUtilImpl();
        }
        return INSTANCE;
    }

    @Override
    public boolean isEmpty(final JArray jsonA) {
        if (Objects.isNull(jsonA)) {
            return true;
        }
        return jsonA.isEmpty();
    }

    @Override
    public boolean isEmpty(final JObject jsonJ) {
        if (Objects.isNull(jsonJ)) {
            return true;
        }
        return jsonJ.isEmpty();
    }

    @Override
    public JObject valueJObject(final JObject jsonJ, final String field) {
        if (Objects.isNull(jsonJ)) {
            return new JObjectImpl();
        }
        JObject value = null;
        if (jsonJ.containsKey(field)) {
            value = jsonJ.getJObject(field);
        }
        if (Objects.isNull(value)) {
            value = new JObjectImpl();
        }
        return value;
    }

    @Override
    public JArray valueJArray(final JObject jsonJ, final String field) {
        if (Objects.isNull(jsonJ)) {
            return new JArrayImpl();
        }
        JArray value = null;
        if (jsonJ.containsKey(field)) {
            value = jsonJ.getJArray(field);
        }
        if (Objects.isNull(value)) {
            value = new JArrayImpl();
        }
        return value;
    }
}
