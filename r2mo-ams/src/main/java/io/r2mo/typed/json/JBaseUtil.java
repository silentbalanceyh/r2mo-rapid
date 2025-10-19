package io.r2mo.typed.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.r2mo.base.dbe.Database;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.json.jackson.BigNumberSerializer;
import io.r2mo.typed.json.jackson.DatabaseDeserializer;
import io.r2mo.typed.json.jackson.DatabaseSerializer;
import io.r2mo.typed.json.jackson.JArrayDeserializer;
import io.r2mo.typed.json.jackson.JArraySerializer;
import io.r2mo.typed.json.jackson.JObjectDeserializer;
import io.r2mo.typed.json.jackson.JObjectSerializer;
import io.r2mo.typed.json.jackson.MultiLocalDateTimeDeserializer;
import io.r2mo.typed.json.jackson.RefDeserializer;
import io.r2mo.typed.json.jackson.RefSerializer;
import io.r2mo.typed.json.jackson.StringDateDeserializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 工具类，辅助 JObject 和 JArray 的实现
 *
 * @author lang : 2025-09-07
 */
class JBaseUtil {


    private static final JsonMapper MAPPER = JsonMapper.builder()
        // 开启大小写敏感，防止属性混淆
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, false)
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, false)
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false)
        // 👇 关键：全局命名策略固定为 lowerCamelCase
        .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
        // 👇 建议：使用标准 Bean 命名推断（避免历史规则导致大小写异常）
        .configure(MapperFeature.USE_STD_BEAN_NAMING, true)
        .build();

    static {
        // 配置项：忽略空值
        MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

        // Non-standard JSON but we allow C style comments stream our JSON
        // 配置项：允许注释
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        // 配置项：关闭日期作为时间戳
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 启用特性排序 -> 序列化数据相同时结果一致
        MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        // 配置项：忽略未知的属性
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Big Decimal
        // 配置项：使用 BigDecimal 替代 float/double
        MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

        final List<Module> modules = jacksonModules();
        MAPPER.registerModules(modules.toArray(new Module[0]));
    }

    static JsonMapper jacksonMapper() {
        return MAPPER;
    }

    static List<Module> jacksonModules() {
        final List<Module> modules = new ArrayList<>();
        // 全局配置序列化返回 JSON 处理
        final JavaTimeModule moduleJavaTime = new JavaTimeModule();
        moduleJavaTime.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
        moduleJavaTime.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
        moduleJavaTime.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
        moduleJavaTime.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        moduleJavaTime.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        // 多格式反序列化
        moduleJavaTime.addDeserializer(LocalDateTime.class, new MultiLocalDateTimeDeserializer());
        moduleJavaTime.addDeserializer(Date.class, new StringDateDeserializer());
        modules.add(moduleJavaTime);


        // 特殊类型
        final SimpleModule moduleJson = new SimpleModule();
        moduleJson.addSerializer(JObject.class, new JObjectSerializer());
        moduleJson.addDeserializer(JObject.class, new JObjectDeserializer());
        moduleJson.addSerializer(JArray.class, new JArraySerializer());
        moduleJson.addDeserializer(JArray.class, new JArrayDeserializer());
        moduleJson.addSerializer(UUID.class, new UUIDSerializer());
        moduleJson.addDeserializer(UUID.class, new UUIDDeserializer());
        moduleJson.addSerializer(Ref.class, new RefSerializer());
        moduleJson.addDeserializer(Ref.class, new RefDeserializer());
        /*
         * 针对 Database 特殊反序列化和序列化，DBE 要使用，虽然不是所有的 DBE 都会用到这里的行为，但
         * 对于需要 DIY 底层处理的过程中，提供 DS / Database 的基本管理，让整个流程变成可控。
         */
        moduleJson.addSerializer(Database.class, new DatabaseSerializer());
        moduleJson.addDeserializer(Database.class, new DatabaseDeserializer());
        modules.add(moduleJson);
        return modules;
    }

    @SuppressWarnings("unchecked")
    static <T extends JBase> T parse(final String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        final String content = json.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            return (T) SPI.A(json);
        } else if (content.startsWith("{") && content.endsWith("}")) {
            return (T) SPI.J(json);
        } else {
            throw new IllegalStateException("[ R2MO ] 无法解析该 JSON 内容：" + json);
        }
    }
}
