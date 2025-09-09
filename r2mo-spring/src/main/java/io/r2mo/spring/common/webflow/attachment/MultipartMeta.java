package io.r2mo.spring.common.webflow.attachment;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 基础配置数据
 *
 * @author lang : 2025-09-09
 */
public interface MultipartMeta<T> {

    Class<T> entityCls();

    String[] fieldList();

    default Type fieldType(final String field) {
        return String.class;
    }

    default JObject config() {
        return SPI.J();
    }

    default MultipartMeta<T> configure(final String field, final Object value) {
        return this;
    }

    default MultipartMeta<T> configure(final Map<String, Object> map) {
        return this;
    }
}
