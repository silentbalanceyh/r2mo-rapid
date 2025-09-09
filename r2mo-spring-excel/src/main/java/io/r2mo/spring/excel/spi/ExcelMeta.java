package io.r2mo.spring.excel.spi;

import io.r2mo.spring.common.webflow.attachment.MultipartMeta;

/**
 * @author lang : 2025-09-09
 */
class ExcelMeta<T> implements MultipartMeta<T> {

    private final Class<T> entityCls;

    ExcelMeta(final Class<T> entityCls) {
        this.entityCls = entityCls;
    }


    @Override
    public Class<T> entityCls() {
        return null;
    }

    @Override
    public String[] fieldList() {
        return new String[0];
    }
}
