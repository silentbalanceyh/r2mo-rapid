package io.r2mo.jce.component.lic.io;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.component.lic.domain.LicensePath;
import io.r2mo.typed.common.Binary;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * @author lang : 2025-09-20
 */
@Slf4j
class LicenseIoLic extends AbstractLicenseIo implements LicenseIo {
    private final LicenseFormat formatter = new LicenseFormatLic();

    LicenseIoLic(final HStore store) {
        super(store);
    }

    @Override
    public Binary writeTo(final LicenseFile licenseFile, final LicenseConfiguration configuration) {
        // 1. 格式化
        final String content = this.formatter.format(licenseFile);
        final String licPath = this.nameLic(licenseFile, configuration);
        final boolean writeLic = this.store.write(licPath, content, false);
        log.info("[ R2MO ] 许可文件：{}", licPath);


        // 2. 签名信息
        final byte[] signature = licenseFile.signature();
        final String sigPath = this.nameSig(licenseFile, configuration);
        final boolean writeSig = this.store.write(sigPath, new ByteArrayInputStream(signature));
        log.info("[ R2MO ] 签名文件：{}", sigPath);


        // 3. 公钥信息
        final byte[] key = this.bytePublic(configuration);
        final String keyPath = this.nameKey(licenseFile, configuration);
        final boolean writeKey = this.store.write(keyPath, new ByteArrayInputStream(key));
        log.info("[ R2MO ] 公钥文件：{}", keyPath);
        System.out.println(content);
        return null;
    }

    @Override
    public LicenseFile readIn(final LicensePath path, final LicenseConfiguration configuration) {
        return null;
    }

    @Override
    public LicenseData verify(final LicenseFile licenseFile, final LicenseConfiguration configuration) {
        return null;
    }
}
