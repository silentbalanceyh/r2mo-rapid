package io.r2mo.jce.common;

import io.r2mo.function.Fn;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.MessageDigest;

/**
 * SignerMac 提供基于对称密钥的消息认证（HMAC/CMAC）
 *
 * <p>区别于 {@link SignerMac}（非对称签名）：
 * <pre>
 * - SignerCommon: 使用私钥生成签名，公钥验证签名，保证不可抵赖性。
 * - SignerMac: 使用同一个对称密钥生成和验证 MAC，只能保证消息完整性和认证，不能防抵赖。
 * </pre>
 *
 * <p>常见算法：
 * <ul>
 *   <li>HmacSHA256</li>
 *   <li>HmacSHA512</li>
 *   <li>HmacSHA3-256</li>
 *   <li>HmacSM3</li>
 * </ul>
 *
 * @author lang
 * @since 2025-09-20
 */
class SignerMac implements Signer<SecretKey, SecretKey> {

    private final String algorithm;

    /**
     * 构造函数 - 指定 MAC 算法
     *
     * @param algorithm 算法名称，例如 "HmacSHA256"
     */
    SignerMac(final String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 使用对称密钥生成 MAC 值
     *
     * @param data      输入数据
     * @param secretKey 对称密钥
     *
     * @return MAC 值
     */
    @Override
    public byte[] sign(final byte[] data, final SecretKey secretKey) {
        return Fn.jvmOr(() -> {
            final Mac mac = Mac.getInstance(this.algorithm);
            mac.init(secretKey);
            mac.update(data);
            return mac.doFinal();
        });
    }

    /**
     * 使用对称密钥验证 MAC 值
     *
     * @param data      输入数据
     * @param secretKey 对称密钥
     * @param sign      需要验证的 MAC 值
     *
     * @return 是否验证通过
     */
    @Override
    public boolean verify(final byte[] data, final SecretKey secretKey, final byte[] sign) {
        final byte[] computed = this.sign(data, secretKey);
        return MessageDigest.isEqual(computed, sign);
    }
}
