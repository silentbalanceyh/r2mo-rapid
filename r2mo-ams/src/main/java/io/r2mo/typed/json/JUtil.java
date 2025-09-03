package io.r2mo.typed.json;

import java.util.Map;

/**
 * @author lang : 2025-08-28
 */
public interface JUtil {

    boolean isEmpty(JArray jsonA);

    boolean isEmpty(JObject jsonJ);

    JObject valueJObject(JObject jsonJ, String field);

    JArray valueJArray(JObject jsonJ, String field);

    JObject toJObject(Map<String, Object> map);
}
