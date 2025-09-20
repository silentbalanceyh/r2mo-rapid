package io.r2mo.jce.common;

import io.r2mo.function.Fn;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author lang : 2025-09-19
 */
class EDExecutorCommon implements EDExecutor {
    private final String algorithm;

    /**
     * 构造函数 - 指定算法名称和提供者
     *
     * @param algorithm 对称加密算法名称
     */
    public EDExecutorCommon(final String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 非对称加密（使用私钥）
     *
     * @param data       待加密数据
     * @param privateKey 私钥
     *
     * @return 加密后数据
     */
    @Override
    public byte[] encrypt(final byte[] data, final PrivateKey privateKey) {
        return Fn.jvmOr(() -> {
            final Cipher cipher = JceProvider.ofCipher(this.algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        });
    }

    /**
     * 非对称解密（使用公钥）
     *
     * @param data      待解密数据
     * @param publicKey 公钥
     *
     * @return 解密后数据
     */
    @Override
    public byte[] decrypt(final byte[] data, final PublicKey publicKey) {
        return Fn.jvmOr(() -> {
            final Cipher cipher = JceProvider.ofCipher(this.algorithm);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        });
    }

    /**
     * 对称加密
     *
     * @param data      待加密数据
     * @param secretKey 对称密钥
     *
     * @return 加密后数据
     */
    @Override
    public byte[] encrypt(final byte[] data, final SecretKey secretKey) {
        return Fn.jvmOr(() -> {
            final Cipher cipher = JceProvider.ofCipher(this.algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        });
    }

    /**
     * 对称解密
     *
     * @param data      待解密数据
     * @param secretKey 对称密钥
     *
     * @return 解密后数据
     */
    @Override
    public byte[] decrypt(final byte[] data, final SecretKey secretKey) {
        return Fn.jvmOr(() -> {
            final Cipher cipher = JceProvider.ofCipher(this.algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        });
    }
}
