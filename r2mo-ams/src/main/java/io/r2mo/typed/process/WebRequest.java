package io.r2mo.typed.process;

import java.io.Serializable;

/**
 * @author lang : 2025-08-28
 */
public interface WebRequest<T> extends Serializable {

    String X_APP_ID = "X-App-Id";

    String X_TENANT_ID = "X-Tenant-Id";

    default T data() {
        return null;
    }
}
