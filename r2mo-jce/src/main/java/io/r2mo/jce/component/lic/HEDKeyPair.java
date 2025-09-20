package io.r2mo.jce.component.lic;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.common.HED;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;

import java.security.KeyPair;
import java.util.Objects;

/**
 * Master 的公私钥生成器
 *
 * @author lang : 2025-09-19
 */
public class HEDKeyPair {
    private static final Cc<String, HEDKeyPair> CCT_GENERATOR = Cc.openThread();
    private static LicenseMgr MANAGER;

    private HEDKeyPair() {
    }

    public static HEDKeyPair of() {
        return CCT_GENERATOR.pick(HEDKeyPair::new);
    }

    private LicenseMgr manager() {
        if (Objects.isNull(MANAGER)) {
            MANAGER = SPI.findOne(LicenseMgr.class);
        }
        Objects.requireNonNull(MANAGER, "[ R2MO ] LicenseMgr 未找到，请检查相关配置");
        return MANAGER;
    }


    public void generate(final String directory, final AlgLicenseSpec spec) {
        final HStore store = this.manager().refStore();
        final KeyPair generated = HED.generate(spec);
        store.write(this.formatPrivate(directory, spec), generated.getPrivate());
        store.write(this.formatPublic(directory, spec), generated.getPublic());
    }

    private String formatDirectory(final String directory, final AlgLicenseSpec spec) {
        return directory + "/" + spec.alg() + "_" + spec.length();
    }

    public String formatPrivate(final String directory, final AlgLicenseSpec spec) {
        return this.formatDirectory(directory, spec) + "_private.pem";
    }

    public String formatPublic(final String directory, final AlgLicenseSpec spec) {
        return this.formatDirectory(directory, spec) + "_public.pem";
    }
}
