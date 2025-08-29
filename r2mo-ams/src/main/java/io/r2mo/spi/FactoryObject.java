package io.r2mo.spi;

import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

/**
 * @author lang : 2025-08-28
 */
public interface FactoryObject {

    JObject jsonObject();

    JObject jsonObject(Object wrapType);

    JObject jsonObject(String jsonStr);

    JArray jsonArray();

    JArray jsonArray(Object wrapType);

    JArray jsonArray(String jsonStr);

    JUtil jsonUtil();
}
