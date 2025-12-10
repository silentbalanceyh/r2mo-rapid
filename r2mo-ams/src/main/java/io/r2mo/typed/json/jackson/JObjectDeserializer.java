package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;

import java.io.IOException;
import java.util.Map;

/**
 * @author lang : 2025-08-28
 */
public class JObjectDeserializer extends JsonDeserializer<JObject> {

    static JObject parseNode(final JsonNode node, final ObjectMapper codec) {
        if (node == null || node.isNull()) {
            return SPI.J();
        }

        if (node.isObject()) {
            final JObject obj = SPI.J();

            // 使用 Iterator 替代 properties() 方法
            for (final Map.Entry<String, JsonNode> entry : node.properties()) {
                final String key = entry.getKey();
                final JsonNode child = entry.getValue();

                if (child.isObject()) {
                    obj.put(key, parseNode(child, codec));
                } else if (child.isArray()) {
                    obj.put(key, parseArray((ArrayNode) child, codec));
                } else if (child.isTextual()) {
                    obj.put(key, child.asText());
                } else if (child.isNumber()) {
                    obj.put(key, child.numberValue());
                } else if (child.isBoolean()) {
                    obj.put(key, child.booleanValue());
                } else {
                    obj.put(key, child.toString());
                }
            }
            return obj;
        }

        if (node.isArray()) {
            final JObject wrapper = SPI.J();
            wrapper.put("array", parseArray((ArrayNode) node, codec));
            return wrapper;
        }

        // 处理根节点为文本的情况
        if (node.isTextual()) {
            final String textValue = node.asText();
            if (isJsonString(textValue)) {
                try {
                    final JsonNode nestedNode = codec.readTree(textValue);
                    return parseNode(nestedNode, codec);
                } catch (final Exception e) {
                    final JObject obj = SPI.J();
                    obj.put("_value", textValue);
                    return obj;
                }
            } else {
                final JObject obj = SPI.J();
                obj.put("_value", textValue);
                return obj;
            }
        }

        // 其他基本类型
        final JObject obj = SPI.J();
        obj.put("_value", node.toString());
        return obj;
    }

    private static JArray parseArray(final ArrayNode arrayNode, final ObjectMapper codec) {
        final JArray arr = SPI.A();
        for (final JsonNode element : arrayNode) {
            if (element.isObject()) {
                arr.add(parseNode(element, codec));
            } else if (element.isArray()) {
                arr.add(parseArray((ArrayNode) element, codec));
            } else if (element.isTextual()) {
                arr.add(element.asText());
            } else if (element.isNumber()) {
                arr.add(element.numberValue());
            } else if (element.isBoolean()) {
                arr.add(element.booleanValue());
            } else {
                arr.add(element.toString());
            }
        }
        return arr;
    }

    private static boolean isJsonString(final String str) {
        if (str == null || str.length() < 2) {
            return false;
        }
        final String trimmed = str.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
            (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }

    @Override
    public JObject deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return parseNode(node, (ObjectMapper) parser.getCodec());
    }
}