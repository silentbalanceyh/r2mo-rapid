package io.r2mo.jce.component.lic.io;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.constant.LicFormat;

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
        final LicFormat format = licenseFile.format();
        return this.nameLicense(licenseFile, configuration) + format.extension();
    }

    // 签名文件路径
    protected String nameSig(final LicenseFile licenseFile,
                             final LicenseConfiguration configuration) {
        return this.nameLicense(licenseFile, configuration) + ".sig";
    }

    // 密钥文件路径（公钥）
    protected String nameKey(final LicenseFile licenseFile,
                             final LicenseConfiguration configuration) {
        return this.nameLicense(licenseFile, configuration) + ".key";
    }

    private String nameLicense(final LicenseFile licenseFile,
                               final LicenseConfiguration configuration) {
        return configuration.contextLicense() +
            "/" + licenseFile.licenseId() +
            "/" + licenseFile.id();
    }
}
