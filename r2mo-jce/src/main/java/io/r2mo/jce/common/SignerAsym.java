package io.r2mo.jce.common;

import io.r2mo.function.Fn;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * @author lang : 2025-09-19
 */
class SignerAsym implements Signer<PublicKey, PrivateKey> {
    private final String algorithm;

    /**
     * 构造函数 - 指定算法名称
     *
     * @param algorithm 签名算法名称
     */
    SignerAsym(final String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 使用私钥对数据进行签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     *
     * @return 签名结果
     */
    @Override
    public byte[] sign(final byte[] data, final PrivateKey privateKey) {
        return Fn.jvmOr(() -> {
            final Signature signature = JceProvider.ofSignature(this.algorithm);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        });
    }

    /**
     * 使用公钥验证签名
     *
     * @param data      原始数据
     * @param publicKey 公钥
     * @param sign      签名数据
     *
     * @return 验证结果
     */
    @Override
    public boolean verify(final byte[] data, final PublicKey publicKey, final byte[] sign) {
        return Fn.jvmOr(() -> {
            final Signature signature = JceProvider.ofSignature(this.algorithm);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        });
    }
}
