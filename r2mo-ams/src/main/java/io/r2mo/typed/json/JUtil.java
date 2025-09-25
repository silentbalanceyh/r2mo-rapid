package io.r2mo.typed.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.r2mo.function.Fn;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
public interface JUtil {
    // -------------- 判断函数
    default boolean isEmpty(final JArray jsonA) {
        if (Objects.isNull(jsonA)) {
            return true;
        }
        return jsonA.isEmpty();
    }

    default boolean isEmpty(final JObject jsonJ) {
        if (Objects.isNull(jsonJ)) {
            return true;
        }
        return jsonJ.isEmpty();
    }

    default boolean isNotEmpty(final JArray jsonA) {
        return !this.isEmpty(jsonA);
    }

    default boolean isNotEmpty(final JObject jsonJ) {
        return !this.isEmpty(jsonJ);
    }

    boolean isJObject(Object value);

    boolean isJArray(Object value);


    // -------------- 提取函数
    JObject valueJObject(JObject jsonJ, String field);

    JArray valueJArray(JObject jsonJ, String field);

    JObject valueJObject(JObject jsonJ);

    JArray valueJArray(JArray jsonJ);

    // -------------- 转换函数
    JObject toJObject(Object value);

    JArray toJArray(Object value);

    <E> Collection<E> toCollection(Object value);

    String toYaml(JBase json);

    // -------------- 序列化反序列化

    default <T, R extends JBase> R serializeJson(final T target) {
        if (Objects.isNull(target)) {
            // 无法判断返回类型
            return null;
        }
        // 使用自定义的 Mapper 进行转换
        final String jsonStr = Fn.jvmOr(() -> JBase.jackson().writeValueAsString(target));

        // 根据字符串转换成对应的 JBase
        return JBase.parse(jsonStr);
    }

    default <T> T deserializeJson(final JObject json, final Class<T> clazz) {
        if (Objects.isNull(json) || Objects.isNull(clazz)) {
            return null;
        }

        // JObject 转换成 Json 字符串
        final String jsonStr = json.encode();


        // 使用自定义的 Mapper 进行转换
        return Fn.jvmOr(() -> JBase.jackson().readValue(jsonStr, clazz));
    }

    default <T> List<T> deserializeJson(final JArray json, final Class<T> clazz) {
        if (Objects.isNull(json) || Objects.isNull(clazz)) {
            return List.of();
        }


        // JArray 转换成 Json 字符串
        final String jsonStr = json.encode();

        final JsonMapper mapper = JBase.jackson();
        // 创建 TypeReference<UT> 来处理泛型列表
        final JavaType elementType = mapper.getTypeFactory().constructType(clazz);
        final JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, elementType);

        return Fn.jvmOr(() -> mapper.readValue(jsonStr, listType));
    }
}
