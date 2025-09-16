package io.r2mo.io.common;

import io.r2mo.io.modeling.TransferParameter;

/**
 * @author lang : 2025-09-16
 */
interface BuilderPre<T> {

    T waitFor(final TransferParameter parameter);
}
