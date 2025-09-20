package io.r2mo.jce.component.lic;

import io.r2mo.jce.constant.LicAsym;

/**
 * @author lang : 2025-09-20
 */
class LicenseServiceRSA extends AbstractLicenseService {

    LicenseServiceRSA() {
        super(LicAsym.AlgLicenseRsa.RSA_3072);
    }
}
