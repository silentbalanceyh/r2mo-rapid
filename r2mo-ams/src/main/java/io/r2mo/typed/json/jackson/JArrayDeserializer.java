package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;

import java.io.IOException;

/**
 * @author lang : 2025-08-28
 */
public class JArrayDeserializer extends JsonDeserializer<JArray> {
    @Override
    public JArray deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JacksonException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return SPI.A(node.toString());
    }
}
