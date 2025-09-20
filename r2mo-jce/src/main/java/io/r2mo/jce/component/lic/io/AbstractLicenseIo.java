package io.r2mo.jce.component.lic.io;

import io.r2mo.base.io.HStore;

/**
 * @author lang : 2025-09-20
 */
abstract class AbstractLicenseIo implements LicenseIo {
    protected final HStore store;

    AbstractLicenseIo(final HStore store) {
        this.store = store;
    }
}
