package io.r2mo.jce.component.secure;

import io.r2mo.jce.constant.AlgNorm;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <pre>
 *     1. 私钥加密
 *     2. 公钥解密
 * </pre>
 *
 * @author lang : 2025-10-20
 */
class EPrivateDPublic extends EDBase {

    EPrivateDPublic(final String algorithm) {
        super(algorithm);
    }

    protected EPrivateDPublic(final AlgNorm algorithm) {
        super(algorithm);
    }

    @Override
    public String encrypt(final String plainText, final String keyContent) {
        // 获取私钥
        final PrivateKey privateKey = this.pKCS8(keyContent);
        // 使用私钥加密
        return this.runEncrypt(plainText, privateKey);
    }

    @Override
    public String decrypt(final String cipherText, final String keyContent) {
        // 获取公钥
        final PublicKey publicKey = this.x509(keyContent);
        // 使用公钥解密
        return this.runDecrypt(cipherText, publicKey);
    }
}
