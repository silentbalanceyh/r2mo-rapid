package io.r2mo.jce.component.lic;

import io.r2mo.base.io.HStore;
import io.r2mo.base.util.R2MO;
import io.r2mo.jce.common.HED;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.constant.AlgLicense;
import io.r2mo.jce.constant.AlgLicenseSpec;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author lang : 2025-09-20
 */
public abstract class AbstractLicenseService implements LicenseService {

    private final AlgLicenseSpec spec;
    private final AlgLicenseSpec ALG_ENCRYPT = AlgLicense.AES.value();

    protected AbstractLicenseService(final AlgLicenseSpec spec) {
        this.spec = spec;
    }

    @Override
    public boolean generate(final String directory, final HStore store) {
        final LicenseKeyPair kp = LicenseKeyPair.of(store);
        kp.generate(directory, this.spec);
        return true;
    }

    /**
     * 此处包含了 License 文件的ID，此处的 LicenseId 来自 LicenseData 的 核心字段，此字段用于 License 中
     * 的路径处理，最终输出也会是对应的路径信息，所以此处的 ID 一定是路径上合法的值。
     *
     * @param data       License数据
     * @param privateKey 私钥
     *
     * @return License文件对象
     */
    @Override
    public LicenseFile encrypt(final LicenseData data, final PrivateKey privateKey) {
        // 1. 序列化License数据
        final byte[] byteData = R2MO.serialize(data);

        // 2. 使用私钥签名
        final byte[] signature = HED.sign(byteData, privateKey, this.spec);

        // 3. 加密数据（AES）
        final byte[] encrypted = HED.encrypt(byteData, privateKey, this.ALG_ENCRYPT);

        // 4. 构建License文件对象（格式放到上层去处理）
        return LicenseFile.builder()
            .licenseId(data.licenseId())            // License Id
            .data(byteData).encrypted(encrypted)    // 数据 / 加密数据
            .signature(signature)                   // 数字签名
            .build();
    }

    @Override
    public LicenseData decrypt(final LicenseFile file, final PublicKey publicKey) {
        // 1. 取出加密数据
        final byte[] encrypted = file.encrypted();

        // 2. 解密（AES）
        final byte[] decrypted = HED.decrypt(encrypted, publicKey, this.ALG_ENCRYPT);
        if (decrypted == null || decrypted.length == 0) {
            throw new SecurityException("[ R2MO ] License 解密失败，密钥可能不匹配！");
        }

        // 3. 验签（验证原始数据 + 签名是否匹配）
        final boolean verified = HED.verify(decrypted, file.signature(), publicKey, this.spec);
        if (!verified) {
            throw new SecurityException("[ R2MO ] License 签名验证失败，文件可能被篡改！");
        }

        // 4. 反序列化回 LicenseData
        return R2MO.deserialize(decrypted);
    }
}
