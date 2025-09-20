package io.r2mo.jce.program;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.LicenseHelper;
import io.r2mo.jce.constant.AlgLicense;
import io.r2mo.spi.SPI;

/**
 * @author lang : 2025-09-20
 */
public class HEDMock {
    /**
     * 生成一组密钥对，包含 RSA/ECC/ED25519/X25519/SM2
     *
     * @param directory 存储目录
     */
    public static void generate(final String directory) {
        final HStore store = SPI.V_STORE;
        final LicenseHelper kp = LicenseHelper.of(store);
        // 签名密钥
        kp.generate(directory, AlgLicense.RSA);
        kp.generate(directory, AlgLicense.ECC);
        kp.generate(directory, AlgLicense.ED25519);
        kp.generate(directory, AlgLicense.SM2);

        // 加密密钥
        kp.generate(directory, AlgLicense.AES);
        kp.generate(directory, AlgLicense.CHACHA20);
        kp.generate(directory, AlgLicense.SM4);
    }
}
