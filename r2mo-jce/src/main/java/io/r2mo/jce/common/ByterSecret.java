package io.r2mo.jce.common;

import io.r2mo.jce.component.lic.AlgLicenseSpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * SecretKey 编解码器
 *
 * @author lang : 2025-09-19
 */
class ByterSecret extends AbstractByter<SecretKey> {

    /**
     * 构造函数 - 必须指定算法和长度
     */
    ByterSecret(final AlgLicenseSpec spec) {
        super(spec);
    }

    @Override
    public byte[] encode(final SecretKey value) {
        this.validateAlgorithm(value);
        final byte[] encoded = value.getEncoded();
        this.validateKeyLength(encoded);
        return encoded;
    }

    @Override
    public SecretKey decode(final byte[] bytes) {
        this.validateKeyLength(bytes);
        final SecretKey key = new SecretKeySpec(bytes, this.spec.alg());
        this.validateAlgorithm(key);
        return key;
    }
}
