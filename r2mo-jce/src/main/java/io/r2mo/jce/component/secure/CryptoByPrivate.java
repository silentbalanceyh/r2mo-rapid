package io.r2mo.jce.component.secure;

import io.r2mo.base.secure.EDCrypto;
import io.r2mo.base.secure.EDPair;

/**
 * @author lang : 2025-10-20
 */
public abstract class CryptoByPrivate implements EDCrypto {

    protected CryptoByPrivate() {
    }

    @Override
    public String encrypt(final String plainText) {
        // 私钥加密
        final String privateKey = this.data().getPrivateKey();
        return this.executor().encrypt(plainText, privateKey);
    }

    @Override
    public String decrypt(final String cipherText) {
        // 公钥解密
        final String publicKey = this.data().getPublicKey();
        return this.executor().decrypt(cipherText, publicKey);
    }

    protected abstract EDPair data();

    protected abstract ED executor();
}
