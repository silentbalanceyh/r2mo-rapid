package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.r2mo.SourceReflect;

import java.io.IOException;

/**
 * @author lang : 2025-08-28
 */
public class ClassDeserializer extends JsonDeserializer<Class<?>> {
    @Override
    public Class<?> deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JacksonException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return SourceReflect.clazz(node.asText());
    }
}
