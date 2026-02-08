package io.r2mo.vertx.jooq.generate.configuration;

import io.vertx.core.json.JsonArray;

public abstract class TypeOfJsonArray extends TypeOfJooqBase {

    @Override
    protected Class<?> withUserType() {
        return JsonArray.class;
    }

    @Override
    protected Class<?> withConverter() {
        return JooqJsonArrayConverter.class;
    }
}
