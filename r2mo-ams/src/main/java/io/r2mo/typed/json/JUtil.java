package io.r2mo.typed.json;

import java.util.Collection;

/**
 * @author lang : 2025-08-28
 */
public interface JUtil {

    boolean isEmpty(JArray jsonA);

    boolean isEmpty(JObject jsonJ);

    boolean isJObject(Object value);

    boolean isJArray(Object value);

    JObject valueJObject(JObject jsonJ, String field);

    JArray valueJArray(JObject jsonJ, String field);

    JObject toJObject(Object value);

    JArray toJArray(Object value);

    <E> Collection<E> toCollection(Object value);

    String toYaml(JBase json);
}
