package io.r2mo.typed.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.r2mo.typed.common.Ref;

import java.io.IOException;
import java.util.UUID;

/**
 * @author lang : 2025-09-17
 */
public class RefDeserializer extends JsonDeserializer<Ref> {

    @Override
    public Ref deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        // 检查是否为 null
        if (p.getCurrentToken().isStructStart()) {
            String refType = null;
            UUID refId = null;

            // 读取对象字段
            while (p.nextToken() != com.fasterxml.jackson.core.JsonToken.END_OBJECT) {
                final String fieldName = p.currentName();
                p.nextToken(); // 移动到字段值

                switch (fieldName) {
                    case Ref.REF_TYPE:
                        refType = p.getValueAsString();
                        break;
                    case Ref.REF_ID:
                        final String idStr = p.getValueAsString();
                        if (idStr != null && !idStr.isEmpty()) {
                            try {
                                refId = UUID.fromString(idStr);
                            } catch (final IllegalArgumentException e) {
                                // 无效的 UUID，保持为 null
                            }
                        }
                        break;
                }
            }

            return Ref.of(refType, refId);
        } else if (p.getCurrentToken().isScalarValue()) {
            // 处理字符串形式的 Ref（如果需要）
            return null;
        } else {
            // 其他情况返回 null
            return null;
        }
    }
}
