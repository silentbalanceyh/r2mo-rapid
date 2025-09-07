package io.r2mo.typed.hutool.spi;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
class JUtilImpl implements JUtil {
    private static final YAMLMapper YAML = new YAMLMapper();
    private static JUtil INSTANCE;

    private JUtilImpl() {
    }

    static JUtil getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new JUtilImpl();
        }
        return INSTANCE;
    }

    /**
     * 拆包
     */
    @SuppressWarnings("unchecked")
    static <T> T boxOut(final Object value) {
        if (value instanceof final JObject jsonJ) {
            final JSONObject data = jsonJ.data();
            Objects.requireNonNull(data, "[ R2MO ] 根据约定，内置对象不可以为空！");
            return (T) data;
        } else if (value instanceof final JArray jsonA) {
            final JSONArray data = jsonA.data();
            Objects.requireNonNull(data, "[ R2MO ] 根据约定，内置数组不可以为空！");
            return (T) data;
        } else {
            // 其他对象不转换
            return (T) value;
        }
    }

    /**
     * 封包
     */
    @SuppressWarnings("unchecked")
    static <T extends JBase> T boxIn(final Object value) {
        if (value instanceof final JSONObject jsonJ) {
            return (T) new JObjectImpl(jsonJ);
        } else if (value instanceof final JSONArray jsonA) {
            return (T) new JArrayImpl(jsonA);
        }
        return null;
    }

    @Override
    public JObject toJObject(final Object value) {
        if (value instanceof final JSONObject jsonO) {
            return new JObjectImpl(jsonO);
        } else if (value instanceof final JObject jsonO) {
            return jsonO;
        }
        return null;
    }

    @Override
    public JArray toJArray(final Object value) {
        if (value instanceof final JSONArray jsonA) {
            return new JArrayImpl(jsonA);
        } else if (value instanceof final JArray jsonA) {
            return jsonA;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Collection<E> toCollection(final Object value) {
        // 1. 根据实现来处理
        final List<E> list = new ArrayList<>();
        if (value instanceof final JSONArray jsonA) {
            jsonA.stream().map(item -> (E) item).forEach(list::add);
        } else if (value instanceof final JArray jsonA) {
            final JSONArray jsonArray = jsonA.data();
            jsonArray.stream().map(item -> (E) item).forEach(list::add);
        }
        return list;
    }

    @Override
    public String toYaml(final JBase json) {
        if (Objects.isNull(json)) {
            return null;
        }
        final Object jsonObject = JSONUtil.parse(json.encode());
        try {
            return YAML.writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonObject);
        } catch (final Exception ex) {
            throw new RuntimeException("[ R2MO ] 转换YAML字符串失败！", ex);
        }
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
    public boolean isJObject(final Object value) {
        if (value instanceof JObject) {
            return true;
        }
        return value instanceof JSONObject;
    }

    @Override
    public boolean isJArray(final Object value) {
        if (value instanceof JArray) {
            return true;
        }
        return value instanceof JSONArray;
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
