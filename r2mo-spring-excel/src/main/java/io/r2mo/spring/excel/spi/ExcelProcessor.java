package io.r2mo.spring.excel.spi;

import io.r2mo.spring.common.webflow.attachment.MultipartProcessor;

/**
 * @author lang : 2025-09-09
 */
class ExcelProcessor<T> implements MultipartProcessor<T> {

    private final Class<T> entityCls;

    ExcelProcessor(final Class<T> entityCls) {
        this.entityCls = entityCls;
    }
}
