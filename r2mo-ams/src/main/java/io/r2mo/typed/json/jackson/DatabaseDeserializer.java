package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.json.JObject;

import java.io.IOException;

/**
 * @author lang : 2025-08-28
 */
public class DatabaseDeserializer extends JsonDeserializer<Database> {
    @Override
    public Database deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JacksonException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final JObject json = JObjectDeserializer.parseNode(node, (ObjectMapper) parser.getCodec());
        return Database.createDatabase(json);
    }
}
