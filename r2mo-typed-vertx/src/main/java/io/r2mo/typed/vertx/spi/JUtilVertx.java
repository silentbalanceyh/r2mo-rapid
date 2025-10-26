package io.r2mo.typed.vertx.spi;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-09-25
 */
class JUtilVertx implements JUtil {
    private static final YAMLMapper MAPPER_YAML = new YAMLMapper();
    private static JUtil INSTANCE;

    static JUtil getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new JUtilVertx();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    static <T> T boxOut(final Object value) {
        if (value instanceof final JObject jsonJ) {
            final JsonObject data = jsonJ.data();
            Objects.requireNonNull(data, "[ R2MO ] 根据约定，内置对象不可以为空！");
            return (T) data;
        } else if (value instanceof final JArray jsonA) {
            final JsonArray data = jsonA.data();
            Objects.requireNonNull(data, "[ R2MO ] 根据约定，内置数组不可以为空！");
            return (T) data;
        } else if (value instanceof LocalDateTime ||
            value instanceof LocalDate ||
            value instanceof LocalTime
        ) {
            // 其他对象不转换
            if (value instanceof final LocalDateTime ldt) {
                return (T) R2MO.parse(ldt).toInstant();
            } else if (value instanceof final LocalDate ld) {
                return (T) R2MO.parse(ld).toInstant();
            } else {
                return (T) R2MO.parse((LocalTime) value).toInstant();
            }
        } else {
            return (T) value;
        }
    }

    /**
     * 封包
     */
    @SuppressWarnings("unchecked")
    static <T extends JBase> T boxIn(final Object value) {
        if (value instanceof final JsonObject jsonJ) {
            return (T) new JObjectVertx(jsonJ);
        } else if (value instanceof final JsonArray jsonA) {
            return (T) new JArrayVertx(jsonA);
        }
        return null;
    }

    @Override
    public boolean isJObject(final Object value) {
        if (value instanceof JObject) {
            return true;
        }
        return value instanceof JsonObject;
    }

    @Override
    public boolean isJArray(final Object value) {
        if (value instanceof JArray) {
            return true;
        }
        return value instanceof JsonArray;
    }

    @Override
    public JObject valueJObject(final JObject jsonJ, final String field) {
        return R2MO.valueT(jsonJ, field, JObjectVertx::new);
    }

    @Override
    public JArray valueJArray(final JObject jsonJ, final String field) {
        return R2MO.valueT(jsonJ, field, JArrayVertx::new);
    }

    @Override
    public JObject valueJObject(final JObject jsonJ) {
        return R2MO.valueT(jsonJ, JObjectVertx::new);
    }

    @Override
    public JArray valueJArray(final JArray jsonJ) {
        return R2MO.valueT(jsonJ, JArrayVertx::new);
    }

    @Override
    public JObject toJObject(final Object value) {
        if (value instanceof final JsonObject jsonO) {
            return new JObjectVertx(jsonO);
        } else if (value instanceof final JObject jsonO) {
            return jsonO;
        }
        return null;
    }

    @Override
    public JArray toJArray(final Object value) {
        if (value instanceof final JsonArray jsonA) {
            return new JArrayVertx(jsonA);
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
        if (value instanceof final JsonArray jsonA) {
            jsonA.stream().map(item -> (E) item).forEach(list::add);
        } else if (value instanceof final JArray jsonA) {
            final JsonArray jsonArray = jsonA.data();
            jsonArray.stream().map(item -> (E) item).forEach(list::add);
        }
        return list;
    }

    @Override
    public String toYaml(final JBase json) {
        if (Objects.isNull(json)) {
            return null;
        }
        final Object jsonObject = Json.decodeValue(json.encode());
        try {
            return MAPPER_YAML.writerWithDefaultPrettyPrinter()
                .writeValueAsString(jsonObject);
        } catch (final Exception e) {
            throw new RuntimeException("[ R2MO ] 转换YAML字符串失败！", e);
        }
    }
}
