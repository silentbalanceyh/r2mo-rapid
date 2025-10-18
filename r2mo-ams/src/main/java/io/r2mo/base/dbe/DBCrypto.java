package io.r2mo.base.dbe;

/**
 * 加密解密模块，针对密码如果存在此 SPI 组件，则需要执行加密解密的操作，若无，则数据库配置使用明文密码
 *
 * @author lang : 2025-10-18
 */
public interface DBCrypto {
    String FOR_DATABASE = "CRYPTO_FOR_DATABASE";

    String encrypt(String plainText);

    String decrypt(String cipherText);
}
