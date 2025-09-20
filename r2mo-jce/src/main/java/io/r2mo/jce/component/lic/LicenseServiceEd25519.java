package io.r2mo.jce.component.lic;

import io.r2mo.jce.constant.LicAsym;

/**
 * @author lang : 2025-09-20
 */
class LicenseServiceEd25519 extends AbstractLicenseService {

    LicenseServiceEd25519() {
        super(LicAsym.AlgLicenseModern.ED25519);
    }
}
