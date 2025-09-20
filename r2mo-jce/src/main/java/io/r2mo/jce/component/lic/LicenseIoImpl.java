package io.r2mo.jce.component.lic;

import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.component.lic.domain.LicenseLocation;

import java.io.InputStream;

/**
 * @author lang : 2025-09-20
 */
public class LicenseIoImpl implements LicenseIo {
    @Override
    public InputStream writeTo(final LicenseLocation location, final LicenseFile fileData) {
        return null;
    }

    @Override
    public LicenseFile readIn(final LicenseLocation location) {
        return null;
    }

    @Override
    public LicenseData verify(final LicenseLocation location, final LicenseFile fileData) {
        return null;
    }
}
