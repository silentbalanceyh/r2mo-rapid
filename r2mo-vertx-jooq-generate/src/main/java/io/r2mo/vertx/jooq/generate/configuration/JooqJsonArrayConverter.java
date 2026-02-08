package io.r2mo.vertx.jooq.generate.configuration;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.json.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Converter;

import java.util.Objects;

@Slf4j
public class JooqJsonArrayConverter implements Converter<String, JsonArray> {

    @Override
    public JsonArray from(final String databaseArray) {
        // 数据库为 null --> null
        if (Objects.isNull(databaseArray)) {
            return null;
        }
        // 数据库有值如 "" --> JsonArray
        if (StrUtil.isEmpty(databaseArray)) {
            return new JsonArray();
        }
        // 尝试转换
        try {
            return new JsonArray(databaseArray);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            return new JsonArray();
        }
    }

    @Override
    public String to(final JsonArray objects) {
        if (Objects.isNull(objects)) {
            return null;
        }
        return objects.encode();
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public Class<JsonArray> toType() {
        return JsonArray.class;
    }
}
