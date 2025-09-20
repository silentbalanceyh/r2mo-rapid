package io.r2mo.jce.common;

import io.r2mo.jce.constant.AlgLicenseSpec;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * PublicKey 编解码器
 *
 * <p>背景知识：</p>
 * <pre>
 * 公钥在二进制表示时通常遵循 X.509 标准中的 SubjectPublicKeyInfo (SPKI) 结构。
 *
 * - 常见 PEM 格式：
 *   -----BEGIN PUBLIC KEY-----
 *   (Base64)
 *   -----END PUBLIC KEY-----
 *
 * - 为什么用 X.509 (SPKI)：
 *   1. 标准化：SPKI 是通用的 ASN.1 封装，可描述任意公钥算法（RSA, EC, EdDSA, SM2 等）。
 *   2. 兼容性：Java 的 KeyFactory.generatePublic() 仅支持 X509EncodedKeySpec。
 *   3. 互操作性：TLS 证书、公钥交换协议中，公钥几乎都以 X.509/SPKI 格式分发。
 *
 * - 对比：
 *   * RSA 私钥历史上有 PKCS#1 (只含 modulus+exponent)，但公钥统一用 X.509/SPKI。
 *   * ECC 公钥在 SEC1 格式里只存储点坐标，但也需要包装到 X.509/SPKI 才能被大多数库识别。
 *
 * 所以：在 Java 中，公钥必须使用 X509EncodedKeySpec 进行解码。
 * </pre>
 *
 * @author lang
 * @since 2025-09-19
 */
class ByterPublic extends AbstractByter<PublicKey> {

    /**
     * 构造函数 - 通过 {@link AlgLicenseSpec} 指定算法信息
     *
     * @param spec 算法规范枚举，例如 {@code AlgLicense.AlgLicenseRsa.RSA_2048}
     */
    ByterPublic(final AlgLicenseSpec spec) {
        super(spec);
    }

    /**
     * 编码公钥为标准 X.509 (SPKI) 格式的字节数组
     *
     * @param value 公钥实例
     *
     * @return 编码后的字节数组
     */
    @Override
    public byte[] encode(final PublicKey value) {
        this.validateAlgorithm(value);
        return value.getEncoded();
    }

    /**
     * 从 X.509 (SPKI) 编码字节解码为公钥对象
     *
     * @param bytes 公钥字节数组（X.509 格式）
     *
     * @return 公钥对象
     */
    @Override
    public PublicKey decode(final byte[] bytes) {
        try {
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            final KeyFactory keyFactory = JceProvider.ofKeyFactory(this.spec.alg());
            final PublicKey key = keyFactory.generatePublic(keySpec);
            this.validateAlgorithm(key);
            return key;
        } catch (final Exception e) {
            throw new RuntimeException("[ R2MO ] 公钥解码失败", e);
        }
    }
}
