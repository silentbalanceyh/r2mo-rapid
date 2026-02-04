package io.r2mo.jaas.session;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-11-10
 */
public interface UserAt extends Serializable {

    String ID_USER = "id";
    String ID_EMPLOYEE = "employeeId";

    UUID id();

    MSUser logged();

    MSEmployee employee();

    JObject data();

    @SuppressWarnings("all")
    boolean isOk();

    class Deserializer extends JsonDeserializer<UserAt> {
        private final static JUtil UT = SPI.V_UTIL;

        @Override
        public UserAt deserialize(final JsonParser parser,
                                  final DeserializationContext deserializationContext) throws IOException, JacksonException {
            final JsonNode node = parser.getCodec().readTree(parser);
            final JObject data = JBase.parse(node.toString());
            final MSUser user = UserUtil.fromJson(data);
            if (Objects.isNull(user)) {
                return null;
            }
            // employeeId -> id
            final JObject processed = ((JObject) data.copy()).put(ID_USER, data.get(ID_EMPLOYEE));
            final MSEmployee employee = UT.deserializeJson(processed, MSEmployee.class);
            final UserAtLogged userAt = new UserAtLogged(user.getId());
            userAt.logged(user).employee(employee);
            return userAt;
        }
    }

    class Serializer extends JsonSerializer<UserAt> {
        @Override
        public void serialize(final UserAt userAt, final JsonGenerator jgen,
                              final SerializerProvider serializerProvider) throws IOException {
            final JObject data = userAt.data();
            jgen.writeObject(data.toMap());
        }

        /**
         * 必须重写此方法以支持 Redis 序列化时的类型信息写入
         */
        @Override
        @SuppressWarnings("Duplicates")
        public void serializeWithType(final UserAt value, final JsonGenerator gen, final SerializerProvider serializers, final TypeSerializer typeSer) throws IOException {
            // 1. 告诉 TypeSerializer 我们要写一个 Object，并开始写入类型前缀（例如：{"@class":"..."）
            final WritableTypeId typeIdDef = typeSer.typeId(value, JsonToken.START_OBJECT);
            typeSer.writeTypePrefix(gen, typeIdDef);

            // 2. 写入对象的内容字段
            // 注意：不能直接调用上面的 serialize()，因为它会写入 data.toMap()，这会生成一个新的完整 JSON 对象（{...}），
            // 导致嵌套结构错误（变成 {"@class":"...", {...}}）。
            // 我们需要将 map 中的字段“平铺”写入当前已经打开的对象中。
            final JObject data = value.data();
            if (data != null) {
                final Map<String, Object> map = data.toMap();
                for (final Map.Entry<String, Object> entry : map.entrySet()) {
                    gen.writeObjectField(entry.getKey(), entry.getValue());
                }
            }

            // 3. 写入类型后缀（闭合对象，例如：}）
            typeSer.writeTypeSuffix(gen, typeIdDef);
        }
    }
}
