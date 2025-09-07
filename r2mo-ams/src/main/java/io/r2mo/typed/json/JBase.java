package io.r2mo.typed.json;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.jackson.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lang : 2025-08-28
 */
public interface JBase extends Serializable {

    static List<Module> modules() {
        final List<Module> modules = new ArrayList<>();
        // 全局配置序列化返回 JSON 处理
        final JavaTimeModule moduleJavaTime = new JavaTimeModule();
        moduleJavaTime.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
        moduleJavaTime.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
        moduleJavaTime.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
        moduleJavaTime.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        moduleJavaTime.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        moduleJavaTime.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        moduleJavaTime.addDeserializer(Date.class, new StringDateDeserializer());
        modules.add(moduleJavaTime);


        // 特殊类型
        final SimpleModule moduleJson = new SimpleModule();
        moduleJson.addSerializer(JObject.class, new JObjectSerializer());
        moduleJson.addDeserializer(JObject.class, new JObjectDeserializer());
        moduleJson.addSerializer(JArray.class, new JArraySerializer());
        moduleJson.addDeserializer(JArray.class, new JArrayDeserializer());
        modules.add(moduleJson);


        return modules;
    }

    @SuppressWarnings("unchecked")
    static <T> T parse(final String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        final String content = json.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            return (T) SPI.SPI_OBJECT.jsonArray(json);
        } else if (content.startsWith("{") && content.endsWith("}")) {
            return (T) SPI.SPI_OBJECT.jsonObject(json);
        } else {
            throw new IllegalStateException("[ R2MO ] 无法解析该 JSON 内容：" + json);
        }
    }

    boolean isEmpty();

    <T> T data();

    String encode();

    String encodePretty();

    String encodeYaml();
}
