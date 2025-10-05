package io.r2mo.typed.vertx.spi;

import cn.hutool.core.util.StrUtil;
import io.r2mo.spi.FactoryObject;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

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
        if (Objects.isNull(wrapType)) {
            return new JObjectVertx();
        }
        if (wrapType instanceof final JsonObject wrapJ) {
            return new JObjectVertx(wrapJ);
        }
        return new JObjectVertx();
    }

    @Override
    public JObject jsonObject(final String jsonStr) {
        if (StrUtil.isBlank(jsonStr)) {
            return new JObjectVertx();
        }
        return new JObjectVertx(jsonStr);
    }

    @Override
    public JArray jsonArray() {
        return new JArrayVertx();
    }

    @Override
    public JArray jsonArray(final Object wrapType) {
        if (Objects.isNull(wrapType)) {
            return new JArrayVertx();
        }
        if (wrapType instanceof final JsonArray wrapA) {
            return new JArrayVertx(wrapA);
        }
        return new JArrayVertx();
    }

    @Override
    public JArray jsonArray(final String jsonStr) {
        if (StrUtil.isBlank(jsonStr)) {
            return new JArrayVertx();
        }
        return new JArrayVertx(jsonStr);
    }

    @Override
    public JUtil jsonUtil() {
        return JUtilVertx.getInstance();
    }
}
