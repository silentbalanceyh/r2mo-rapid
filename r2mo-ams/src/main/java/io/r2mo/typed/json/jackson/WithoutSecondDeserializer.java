package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lang : 2025-09-25
 */
public class WithoutSecondDeserializer extends JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER_WITH_SEC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_NO_SEC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public LocalDateTime deserialize(final JsonParser p, final DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        final String text = p.getText();
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, FORMATTER_WITH_SEC);
        } catch (final Exception ex) {
            return LocalDateTime.parse(text, FORMATTER_NO_SEC);
        }
    }
}
