package io.r2mo.vertx.jooq.shared.postgres;

import io.vertx.core.json.Json;
import io.vertx.core.spi.json.JsonCodec;
import org.jooq.Converter;
import org.jooq.JSON;

/**
 * @author jensklingsporn
 */
public class JSONToJsonPojoConverter<U> implements Converter<JSON, U> {

    private final Class<U> userType;
    private final JsonCodec jsonCodec;

    public JSONToJsonPojoConverter(final Class<U> userType, final JsonCodec jsonCodec) {
        this.userType = userType;
        this.jsonCodec = jsonCodec;
    }

    public JSONToJsonPojoConverter(final Class<U> userType) {
        this(userType, Json.CODEC);
    }

    @Override
    public U from(final JSON t) {
        return t == null || t.data().equals("null") ? null : this.jsonCodec.fromString(t.data(), this.userType);
    }

    @Override
    public JSON to(final U u) {
        return u == null ? null : JSON.valueOf(this.jsonCodec.toString(u));
    }

    @Override
    public Class<JSON> fromType() {
        return JSON.class;
    }

    @Override
    public Class<U> toType() {
        return this.userType;
    }
}
