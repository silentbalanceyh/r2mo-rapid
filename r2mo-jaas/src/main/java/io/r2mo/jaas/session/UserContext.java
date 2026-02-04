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
public interface UserContext extends Serializable {

    UUID id();

    MSUser logged();

    MSEmployee employee(UUID empId);

    JObject data();

    boolean isOk();

    class Deserializer extends JsonDeserializer<UserContext> {
        private final static JUtil UT = SPI.V_UTIL;

        @Override
        public UserContext deserialize(final JsonParser parser, final DeserializationContext deserializationContext) throws IOException, JacksonException {
            final JsonNode node = parser.getCodec().readTree(parser);
            final JObject data = JBase.parse(node.toString());
            final MSUser user = UserUtil.fromJson(data);
            if (Objects.isNull(user)) {
                return null;
            }
            final UserContextImpl context = new UserContextImpl(user.getId());
            context.logged(user);
            // employees
            final JObject employeeA = UT.valueJObject(data, "employees");
            employeeA.fieldNames().forEach(field -> {
                final JObject empJ = employeeA.getJObject(field);
                final MSEmployee emp = UT.deserializeJson(empJ, MSEmployee.class);
                context.employee(emp);
            });
            return context;
        }
    }

    class Serializer extends JsonSerializer<UserContext> {
        @Override
        public void serialize(final UserContext userContext, final JsonGenerator jgen,
                              final SerializerProvider serializerProvider) throws IOException {
            final JObject data = userContext.data();
            jgen.writeObject(data.toMap());
        }

        /**
         * 重写此方法以支持带类型信息的序列化（Spring Data Redis 必需）
         */
        @Override
        @SuppressWarnings("Duplicates")
        public void serializeWithType(final UserContext value, final JsonGenerator gen, final SerializerProvider serializers, final TypeSerializer typeSer) throws IOException {
            // 1. 开始写入类型前缀（如 {"@class":"io.r2mo.jaas.session.UserContextImpl"}）
            // JsonToken.START_OBJECT 表示我们要序列化为一个 JSON 对象
            final WritableTypeId typeIdDef = typeSer.typeId(value, JsonToken.START_OBJECT);
            typeSer.writeTypePrefix(gen, typeIdDef);

            // 2. 写入内容数据
            // 注意：必须把 data() 里的字段平铺写入，不能直接 writeObject(map)
            // 否则会变成 {"@class":"...", "key1":"val1"} 这种正确的结构
            // 如果直接 writeObject(map)，会变成 {"@class":"...", { "key1":"val1" }} 这种错误结构
            final JObject data = value.data();
            if (data != null) {
                final Map<String, Object> map = data.toMap();
                for (final Map.Entry<String, Object> entry : map.entrySet()) {
                    gen.writeObjectField(entry.getKey(), entry.getValue());
                }
            }

            // 3. 写入类型后缀（闭合对象，即 "}"）
            typeSer.writeTypeSuffix(gen, typeIdDef);
        }
    }
}
