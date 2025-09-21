package io.r2mo.jce.component.lic.io;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.common.HED;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.component.lic.domain.LicensePath;
import io.r2mo.typed.common.Binary;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        this.store.write(licPath, content, false);
        log.info("[ R2MO ] 许可文件：{}", licPath);


        // 2. 签名信息
        final byte[] signature = licenseFile.signature();
        final String sigPath = this.nameSig(licenseFile, configuration);
        this.store.write(sigPath, signature);
        log.info("[ R2MO ] 签名文件：{}", sigPath);


        final Set<String> files = new HashSet<>() {{
            {
                this.add(licPath);
                this.add(sigPath);
            }
        }};
        // 3. 带有此信息的密钥
        final SecretKey key = licenseFile.key();
        if (Objects.nonNull(key)) {
            final byte[] keyBytes = HED.encodeSecretKey(key, configuration.algEnc());
            final String keyPath = this.nameKey(licenseFile, configuration);
            this.store.write(keyPath, keyBytes);
            log.info("[ R2MO ] 密钥文件：{}", keyPath);
            files.add(keyPath);
        }

        // 4. 合并成压缩流
        return this.store.inBinary(files);
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
