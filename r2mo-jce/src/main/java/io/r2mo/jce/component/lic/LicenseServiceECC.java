package io.r2mo.jce.component.lic;

import io.r2mo.jce.constant.LicAsym;

/**
 * @author lang : 2025-09-20
 */
class LicenseServiceECC extends AbstractLicenseService {

    LicenseServiceECC() {
        super(LicAsym.AlgLicenseEcc.ECDSA_P256);
    }
}
