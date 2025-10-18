package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.json.JObject;

import java.io.IOException;

/**
 * @author lang : 2025-08-28
 */
public class DatabaseSerializer extends JsonSerializer<Database> {
    @Override
    public void serialize(final Database database, final JsonGenerator jGen,
                          final SerializerProvider provider) throws IOException {
        final JObject serialized = database.toJObject();
        jGen.writeObject(serialized.encode());
    }
}
