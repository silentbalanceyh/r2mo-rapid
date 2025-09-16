package io.r2mo.typed.domain.extension;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.r2mo.spi.SPI;
import io.r2mo.typed.domain.builder.BuilderOf;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JUtil;

/**
 * @author lang : 2025-09-16
 */
public abstract class AbstractBuilder<T> implements BuilderOf<T> {
    protected static final JUtil UT = SPI.V_UTIL;

    @Override
    public T create() {
        throw new _501NotSupportException("[ R2MO ] 未实现无参构造函数！");
    }

    @Override
    public void updateOverwrite(final T target, final Object source) {
        BeanUtil.copyProperties(source, target, new CopyOptions()
            .ignoreNullValue()
            .ignoreError()
        );
    }
}
