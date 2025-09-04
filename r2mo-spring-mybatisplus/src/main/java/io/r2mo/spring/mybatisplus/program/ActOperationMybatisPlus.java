package io.r2mo.spring.mybatisplus.program;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.r2mo.base.program.ActOperationBase;

/**
 * @author lang : 2025-08-29
 */
public abstract class ActOperationMybatisPlus<T> extends ActOperationBase<T> {

    protected abstract <M extends BaseMapper<T>> M executor();

    @Override
    protected DBE<T> db() {
        return DBE.of(this.entityCls, this.executor());
    }
}
