package io.r2mo.typed.json;

import java.util.Collection;
import java.util.List;

/**
 * @author lang : 2025-08-28
 */
public interface JUtil {
    // -------------- 判断函数
    boolean isEmpty(JArray jsonA);

    boolean isEmpty(JObject jsonJ);

    boolean isJObject(Object value);

    boolean isJArray(Object value);


    // -------------- 提取函数
    JObject valueJObject(JObject jsonJ, String field);

    JArray valueJArray(JObject jsonJ, String field);


    // -------------- 转换函数
    JObject toJObject(Object value);

    JArray toJArray(Object value);

    <E> Collection<E> toCollection(Object value);

    String toYaml(JBase json);


    // -------------- 序列化反序列化
    <T, R extends JBase> R serializeJson(T target);

    <T> T deserializeJson(JObject json, Class<T> clazz);

    <T> List<T> deserializeJson(JArray json, Class<T> clazz);
}
