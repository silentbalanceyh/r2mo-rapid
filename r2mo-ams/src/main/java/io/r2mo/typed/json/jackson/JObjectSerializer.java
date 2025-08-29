package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.r2mo.typed.json.JObject;

import java.io.IOException;

/**
 * @author lang : 2025-08-28
 */
public class JObjectSerializer extends JsonSerializer<JObject> {
    @Override
    public void serialize(final JObject value, final JsonGenerator jGen, final SerializerProvider provider)
        throws IOException {
        jGen.writeObject(value.toMap());
    }
}
