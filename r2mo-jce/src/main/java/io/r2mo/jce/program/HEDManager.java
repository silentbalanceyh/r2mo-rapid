package io.r2mo.jce.program;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.LicenseKeyPair;
import io.r2mo.jce.constant.LicAsym;
import io.r2mo.spi.SPI;

/**
 * @author lang : 2025-09-20
 */
public class HEDManager {
    /**
     * 生成一组密钥对，包含 RSA/ECC/ED25519/X25519/SM2
     *
     * @param directory 存储目录
     */
    public static void generate(final String directory) {
        final HStore store = SPI.V_STORE;
        final LicenseKeyPair kp = LicenseKeyPair.of(store);
        kp.generate(directory, LicAsym.AlgLicenseRsa.RSA_3072);
        kp.generate(directory, LicAsym.AlgLicenseEcc.ECDSA_P256);
        kp.generate(directory, LicAsym.AlgLicenseModern.ED25519);
        kp.generate(directory, LicAsym.AlgLicenseModern.X25519);
        kp.generate(directory, LicAsym.AlgLicenseSm2.SM2);
    }
}
