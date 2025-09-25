package io.r2mo.typed.vertx.spi;

import io.r2mo.spi.FactoryObject;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

/**
 * Vertx 实现类
 *
 * @author lang : 2025-09-25
 */
public class FactoryObjectVertx implements FactoryObject {
    @Override
    public JObject jsonObject() {
        return new JObjectVertx();
    }

    @Override
    public JObject jsonObject(final Object wrapType) {
        return null;
    }

    @Override
    public JObject jsonObject(final String jsonStr) {
        return null;
    }

    @Override
    public JArray jsonArray() {
        return null;
    }

    @Override
    public JArray jsonArray(final Object wrapType) {
        return null;
    }

    @Override
    public JArray jsonArray(final String jsonStr) {
        return null;
    }

    @Override
    public JUtil jsonUtil() {
        return null;
    }
}
