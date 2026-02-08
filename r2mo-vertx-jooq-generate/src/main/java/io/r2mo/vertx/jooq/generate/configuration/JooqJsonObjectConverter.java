package io.r2mo.vertx.jooq.generate.configuration;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Converter;

import java.util.Objects;

@Slf4j
public class JooqJsonObjectConverter implements Converter<String, JsonObject> {

    @Override
    public JsonObject from(final String databaseObject) {
        // 数据库为 null --> null
        if (Objects.isNull(databaseObject)) {
            return null;
        }
        // 数据库有值如 "" --> JsonObject
        if (StrUtil.isEmpty(databaseObject)) {
            return new JsonObject();
        }
        // 尝试转换
        try {
            return new JsonObject(databaseObject);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            return new JsonObject();
        }
    }

    @Override
    public String to(final JsonObject entries) {
        if (Objects.isNull(entries)) {
            return null;
        }
        return entries.encode();
    }

    @Override
    @org.jetbrains.annotations.NotNull
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    @org.jetbrains.annotations.NotNull
    public Class<JsonObject> toType() {
        return JsonObject.class;
    }
}
