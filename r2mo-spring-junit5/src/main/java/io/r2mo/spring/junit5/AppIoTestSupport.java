package io.r2mo.spring.junit5;

import io.r2mo.io.common.HFS;

/**
 * @author lang : 2025-09-07
 */
public abstract class AppIoTestSupport {
    
    protected HFS fs() {
        return HFS.of();
    }
}
