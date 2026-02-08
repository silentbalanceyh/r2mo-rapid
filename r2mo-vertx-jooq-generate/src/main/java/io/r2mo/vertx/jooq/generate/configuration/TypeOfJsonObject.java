package io.r2mo.vertx.jooq.generate.configuration;

import io.vertx.core.json.JsonObject;

public abstract class TypeOfJsonObject extends TypeOfJooqBase {

    @Override
    protected Class<?> withUserType() {
        return JsonObject.class;
    }

    @Override
    protected Class<?> withConverter() {
        return JooqJsonObjectConverter.class;
    }
}
