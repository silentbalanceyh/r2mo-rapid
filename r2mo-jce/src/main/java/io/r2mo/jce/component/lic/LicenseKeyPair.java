package io.r2mo.jce.component.lic;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.common.HED;
import io.r2mo.jce.component.lic.domain.LicenseLocation;
import io.r2mo.jce.constant.AlgLicenseSpec;
import io.r2mo.typed.cc.Cc;

import java.security.KeyPair;
import java.util.Objects;

/**
 * Master 的公私钥生成器
 *
 * @author lang : 2025-09-19
 */
public class LicenseKeyPair {
    private static final Cc<String, LicenseKeyPair> CCT_GENERATOR = Cc.openThread();
    private final HStore store;

    private LicenseKeyPair(final HStore store) {
        this.store = store;
    }

    public static LicenseKeyPair of(final HStore store) {
        Objects.requireNonNull(store, "[ R2MO ] HStore 不能为空");
        return CCT_GENERATOR.pick(() -> new LicenseKeyPair(store), String.valueOf(store));
    }

    public void generate(final String directory, final AlgLicenseSpec spec) {
        final KeyPair generated = HED.generate(spec);
        final LicenseLocation location = new LicenseLocation();
        location.ioContext(directory).algorithm(spec);

        this.store.write(location.ioPrivate(), generated.getPrivate());
        this.store.write(location.ioPublic(), generated.getPublic());
    }
}
