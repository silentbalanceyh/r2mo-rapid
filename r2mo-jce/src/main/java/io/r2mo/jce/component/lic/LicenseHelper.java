package io.r2mo.jce.component.lic;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.common.HED;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.constant.AlgLicense;
import io.r2mo.typed.cc.Cc;

import java.security.KeyPair;
import java.util.Objects;

/**
 * Master 的公私钥生成器
 *
 * @author lang : 2025-09-19
 */
public class LicenseHelper {
    private static final Cc<String, LicenseHelper> CCT_GENERATOR = Cc.openThread();
    private final HStore store;

    private LicenseHelper(final HStore store) {
        this.store = store;
    }

    public static LicenseHelper of(final HStore store) {
        return CCT_GENERATOR.pick(() -> new LicenseHelper(store), String.valueOf(store));
    }

    /**
     * 生成公私钥对，并存储到指定目录
     *
     * @param directory 存储目录
     * @param license   算法
     */
    public void generate(final String directory, final AlgLicense license) {
        Objects.requireNonNull(this.store, "[ R2MO ] 此方法要求 HStore 不能为空");
        final KeyPair generated = HED.generate(license);
        final LicenseConfiguration configuration = new LicenseConfiguration();
        configuration.ioContext(directory).algSign(license.value());
        this.store.write(configuration.ioPrivate(), generated.getPrivate());
        this.store.write(configuration.ioPublic(), generated.getPublic());
    }
}
