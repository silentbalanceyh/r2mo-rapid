package io.r2mo.spring.common.webflow;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

import java.io.Serializable;

/**
 * @author lang : 2025-09-04
 */
public class PostResponse implements Serializable {

    protected void readFrom(final Object source) {
        final CopyOptions copyOptions = new CopyOptions()
            .ignoreNullValue()
            .ignoreError();
        BeanUtil.copyProperties(source, this, copyOptions);
    }
}
