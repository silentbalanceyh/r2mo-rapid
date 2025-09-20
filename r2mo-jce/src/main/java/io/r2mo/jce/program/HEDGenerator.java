package io.r2mo.jce.program;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.common.HED;
import io.r2mo.jce.component.lic.AlgLicenseSpec;
import io.r2mo.jce.constant.LicAsym;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;

import java.security.KeyPair;

/**
 * Master 的公私钥生成器
 *
 * @author lang : 2025-09-19
 */
public class HEDGenerator {
    private static final Cc<String, HEDGenerator> CCT_GENERATOR = Cc.openThread();

    private HEDGenerator() {
    }

    public static HEDGenerator of() {
        return CCT_GENERATOR.pick(HEDGenerator::new);
    }

    public void generate(final String directory) {
        this.generate(directory, LicAsym.AlgLicenseRsa.RSA_3072);
        this.generate(directory, LicAsym.AlgLicenseEcc.ECDSA_P256);
        this.generate(directory, LicAsym.AlgLicenseModern.ED25519);
        this.generate(directory, LicAsym.AlgLicenseModern.X25519);
        this.generate(directory, LicAsym.AlgLicenseSm2.SM2_DEFAULT);
    }

    private void generate(final String directory, final AlgLicenseSpec spec) {
        final HStore store = SPI.V_STORE;
        final KeyPair generated = HED.generate(spec);
        store.write(directory + "/" + spec.alg() + "_" + spec.length() + "_private.pem", generated.getPrivate());
        store.write(directory + "/" + spec.alg() + "_" + spec.length() + "_public.pem", generated.getPublic());
    }
}
