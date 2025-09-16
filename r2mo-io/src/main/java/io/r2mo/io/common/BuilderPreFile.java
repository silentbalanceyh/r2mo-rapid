package io.r2mo.io.common;

import io.r2mo.io.modeling.TransferParameter;
import io.r2mo.io.modeling.TransferRequest;

/**
 * @author lang : 2025-09-16
 */
class BuilderPreFile extends AbstractBuilderPre implements BuilderPre<TransferRequest> {

    @Override
    public TransferRequest waitFor(final TransferParameter parameter) {
        return null;
    }
}
