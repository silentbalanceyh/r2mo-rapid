package io.r2mo.jce.component.lic.io;

import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HUri;
import io.r2mo.jce.common.HED;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseFile;

import java.security.PublicKey;

/**
 * @author lang : 2025-09-20
 */
abstract class AbstractLicenseIo implements LicenseIo {
    protected final HStore store;

    AbstractLicenseIo(final HStore store) {
        this.store = store;
    }

    // 许可证文件路径
    protected String nameLic(final LicenseFile licenseFile,
                             final LicenseConfiguration configuration) {
        return HUri.UT.resolve(configuration.ioLicenseDirectory(),
            licenseFile.id() + licenseFile.format().extension());
    }

    // 签名文件路径
    protected String nameSig(final LicenseFile licenseFile,
                             final LicenseConfiguration configuration) {
        return HUri.UT.resolve(configuration.ioLicenseDirectory(),
            licenseFile.id() + ".sig");
    }

    // 密钥文件路径（公钥）
    protected String nameKey(final LicenseFile licenseFile,
                             final LicenseConfiguration configuration) {
        return HUri.UT.resolve(configuration.ioLicenseDirectory(),
            licenseFile.id() + ".key");
    }

    protected byte[] bytePublic(final LicenseConfiguration configuration) {
        final String publicPath = configuration.ioPublic();
        final PublicKey publicKey = this.store.inPublic(publicPath);
        return HED.encodePublic(publicKey, configuration.algSign());
    }
}
