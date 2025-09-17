package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.r2mo.typed.common.Ref;

import java.io.IOException;

/**
 * @author lang : 2025-09-17
 */
public class RefSerializer extends JsonSerializer<Ref> {

    @Override
    public void serialize(final Ref ref, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (ref == null) {
            gen.writeNull();
            return;
        }

        gen.writeStartObject();
        gen.writeStringField(Ref.REF_TYPE, ref.refType());
        if (ref.refId() != null) {
            gen.writeStringField(Ref.REF_ID, ref.refId().toString());
        } else {
            gen.writeNullField(Ref.REF_ID);
        }
        gen.writeEndObject();
    }
}
