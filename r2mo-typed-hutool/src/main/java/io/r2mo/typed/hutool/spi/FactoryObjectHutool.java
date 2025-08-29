package io.r2mo.typed.hutool.spi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import io.r2mo.spi.FactoryObject;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.util.Objects;

/**
 * 实现类
 *
 * @author lang : 2025-08-28
 */
public class FactoryObjectHutool implements FactoryObject {
    @Override
    public JObject jsonObject() {
        return new JObjectImpl();
    }

    @Override
    public JObject jsonObject(final Object wrapType) {
        if (Objects.isNull(wrapType)) {
            return new JObjectImpl();
        }
        if (wrapType instanceof final JSONObject wrapJ) {
            return new JObjectImpl(wrapJ);
        }
        return new JObjectImpl();
    }

    @Override
    public JObject jsonObject(final String jsonStr) {
        if (StrUtil.isBlank(jsonStr)) {
            return new JObjectImpl();
        }
        return new JObjectImpl(jsonStr);
    }

    @Override
    public JArray jsonArray() {
        return new JArrayImpl();
    }

    @Override
    public JArray jsonArray(final Object wrapType) {
        if (Objects.isNull(wrapType)) {
            return new JArrayImpl();
        }
        if (wrapType instanceof final JSONArray wrapA) {
            return new JArrayImpl(wrapA);
        }
        return new JArrayImpl();
    }

    @Override
    public JArray jsonArray(final String jsonStr) {
        if (StrUtil.isBlank(jsonStr)) {
            return new JArrayImpl();
        }
        return new JArrayImpl(jsonStr);
    }

    @Override
    public JUtil jsonUtil() {
        return JUtilImpl.getInstance();
    }
}
