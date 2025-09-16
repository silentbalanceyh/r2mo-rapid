package io.r2mo.io.common;

import io.r2mo.io.modeling.TransferRequest;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.domain.builder.BuilderOf;
import io.r2mo.typed.exception.web._501NotSupportException;

/**
 * @author lang : 2025-09-16
 */
abstract class AbstractBuilderPre implements BuilderOf<TransferRequest> {

    @Override
    public void updateRef(final TransferRequest target, final Ref ref) {
        throw new _501NotSupportException("[ R2MO ] 不可直接构造 Request 对象！/ updateRef ");
    }

    @Override
    public TransferRequest create() {
        throw new _501NotSupportException("[ R2MO ] 不可直接构造 Request 对象！/ create ");
    }

    @Override
    public <R> void updateConditional(final TransferRequest target, final R source) {
        throw new _501NotSupportException("[ R2MO ] 不可直接构造 Request 对象！/ updateConditional ");
    }

    @Override
    public void updateOverwrite(final TransferRequest target, final Object source) {
        throw new _501NotSupportException("[ R2MO ] 不可直接构造 Request 对象！/ updateOverwrite ");
    }
}
