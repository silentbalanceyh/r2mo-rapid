package io.r2mo.jce.program;

import io.r2mo.jce.component.lic.HEDKeyPair;
import io.r2mo.jce.constant.LicAsym;

/**
 * @author lang : 2025-09-20
 */
public class HEDManager {

    public static void generate(final String directory) {
        final HEDKeyPair kp = HEDKeyPair.of();
        kp.generate(directory, LicAsym.AlgLicenseRsa.RSA_3072);
        kp.generate(directory, LicAsym.AlgLicenseEcc.ECDSA_P256);
        kp.generate(directory, LicAsym.AlgLicenseModern.ED25519);
        kp.generate(directory, LicAsym.AlgLicenseModern.X25519);
        kp.generate(directory, LicAsym.AlgLicenseSm2.SM2);
    }
}
