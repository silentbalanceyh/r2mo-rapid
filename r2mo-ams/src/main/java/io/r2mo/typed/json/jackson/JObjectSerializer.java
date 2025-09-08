package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.r2mo.typed.json.JObject;

import java.io.IOException;
import java.util.Map;

public class JObjectSerializer extends JsonSerializer<JObject> {
    @Override
    public void serialize(final JObject value, final JsonGenerator jGen,
                          final SerializerProvider provider)
        throws IOException {
        jGen.writeStartObject();
        this.serializeRecursive(value.toMap(), jGen, provider);
        jGen.writeEndObject();
    }

    @SuppressWarnings("unchecked")
    private void serializeRecursive(final Map<String, Object> map, final JsonGenerator jGen,
                                    final SerializerProvider provider)
        throws IOException {
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            jGen.writeFieldName(entry.getKey());
            final Object val = entry.getValue();

            if (val instanceof JObject) {
                jGen.writeStartObject();
                this.serializeRecursive(((JObject) val).toMap(), jGen, provider);
                jGen.writeEndObject();
            } else if (val instanceof Map) {
                jGen.writeStartObject();
                // 假设 Map 里面可能还有 JObject
                this.serializeRecursive((Map<String, Object>) val, jGen, provider);
                jGen.writeEndObject();
            } else {
                jGen.writeObject(val);
            }
        }
    }
}
