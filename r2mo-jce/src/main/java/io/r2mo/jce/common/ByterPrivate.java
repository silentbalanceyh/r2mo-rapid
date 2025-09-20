package io.r2mo.jce.common;

import io.r2mo.jce.component.lic.AlgLicenseSpec;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * PrivateKey 编解码器
 *
 * <p>主要职责：
 * <pre>
 * 1. 负责私钥的字节编解码；
 * 2. 使用 {@link PKCS8EncodedKeySpec} 来处理私钥的标准化表示；
 * 3. 保证算法和长度校验与 {@link AlgLicenseSpec} 对齐。
 * </pre>
 *
 * @author lang
 * @since 2025-09-20
 */
class ByterPrivate extends AbstractByter<PrivateKey> {

    /**
     * 构造函数 - 必须指定算法枚举
     *
     * @param spec 算法规范（含算法名和密钥长度）
     */
    ByterPrivate(final AlgLicenseSpec spec) {
        super(spec);
    }

    @Override
    public byte[] encode(final PrivateKey value) {
        this.validateAlgorithm(value);
        return value.getEncoded();
    }

    /**
     * 将字节数组解码为 {@link PrivateKey}
     *
     * <p>这里必须使用 {@link PKCS8EncodedKeySpec}，原因如下：
     * <ul>
     *   <li><b>PKCS#8 是私钥的国际标准格式</b>：它由 RSA Laboratories 在 Public-Key Cryptography Standards (PKCS) 定义，
     *       用于存储和传输私钥；相比厂商自定义格式更具可移植性。</li>
     *   <li><b>支持多种算法</b>：PKCS#8 使用 ASN.1 结构，可以封装 RSA、EC、DSA、EdDSA 等多种私钥，
     *       因此能作为统一的解码入口。</li>
     *   <li><b>JCE/JDK 的兼容性</b>：在 Java JCE 中，{@link KeyFactory#generatePrivate} 接口
     *       默认支持基于 PKCS#8 的解码，而不直接支持 PKCS#1（仅限 RSA）。</li>
     *   <li><b>安全性和扩展性</b>：PKCS#8 还允许私钥加密（EncryptedPrivateKeyInfo），
     *       为未来的密钥保护提供了扩展点。</li>
     * </ul>
     *
     * @param bytes 私钥的 DER/PEM 解码字节数组
     *
     * @return 私钥对象
     */
    @Override
    public PrivateKey decode(final byte[] bytes) {
        try {
            // Java 语言中，PrivateKey 必须使用 PKCS8EncodedKeySpec 来解码
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            final KeyFactory keyFactory = JceProvider.ofKeyFactory(this.spec.alg());
            final PrivateKey key = keyFactory.generatePrivate(keySpec);
            this.validateAlgorithm(key);
            return key;
        } catch (final Exception e) {
            throw new RuntimeException("[ R2MO ] 私钥解码失败", e);
        }
    }
}
