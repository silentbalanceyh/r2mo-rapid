package io.r2mo.jce.component.lic;

import io.r2mo.jce.constant.AlgLicense;

/**
 * @author lang : 2025-09-20
 */
class LicenseServiceEd25519 extends AbstractLicenseService {

    LicenseServiceEd25519() {
        super(AlgLicense.ED25519);
    }
}
