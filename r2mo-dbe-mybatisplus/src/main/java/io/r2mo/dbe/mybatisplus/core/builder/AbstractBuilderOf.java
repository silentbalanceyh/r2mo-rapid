package io.r2mo.dbe.mybatisplus.core.builder;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.r2mo.SourceReflect;
import io.r2mo.dbe.mybatisplus.core.domain.BaseEntity;
import io.r2mo.dbe.mybatisplus.core.domain.BaseProp;
import io.r2mo.spi.SPI;
import io.r2mo.typed.domain.builder.BuilderOf;

import java.util.UUID;

/**
 * @author lang : 2025-09-12
 */
public abstract class AbstractBuilderOf<T extends BaseEntity> implements BuilderOf<T> {
    private final Class<T> entityCls;

    protected AbstractBuilderOf() {
        this.entityCls = SourceReflect.classT0(this.getClass());
    }

    @Override
    public void updateOverwrite(final T target, final Object source) {
        BeanUtil.copyProperties(source, target, new CopyOptions()
            .ignoreNullValue()
            .ignoreError()
        );
    }

    @Override
    public T create() {
        return SourceReflect.instance(this.entityCls);
    }

    protected <R extends BaseEntity> void updateShared(final T target, final R source) {
        BaseProp.copyFull(target, source);
        BaseProp.setCode(target);
        target.setCMetadata(SPI.J());
        target.setId(UUID.randomUUID());
    }
}
