package io.r2mo.base.secure;

/**
 * 加密解密模块，针对密码如果存在此 SPI 组件，则需要执行加密解密的操作，若无，则数据库配置使用明文密码
 *
 * @author lang : 2025-10-18
 */
public interface EDCrypto {
    String FOR_DATABASE = "CRYPTO_FOR_DATABASE";

    // -------------------------------- 正常模式 --------------------------------
    // 加密
    String encrypt(String plainText);

    // 解密
    String decrypt(String cipherText);

    static String decryptPassword(final String password) {
        return EDCryptoDoctor.decryptPassword(password);
    }
}
