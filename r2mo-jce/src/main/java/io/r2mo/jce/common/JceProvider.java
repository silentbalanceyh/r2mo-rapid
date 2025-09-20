package io.r2mo.jce.common;

import io.r2mo.function.Fn;
import io.r2mo.jce.constant.AlgLicenseSpec;
import io.r2mo.typed.constant.DefaultConstantValue;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.security.*;
import java.util.Objects;

/**
 * JceProvider - 精简版（仅包含支持指定 Provider 的 JCE 构造）
 *
 * <p>设计目标与原则：</p>
 * <pre>
 * 1. 统一 Provider：全应用使用同一个 Provider 实例，避免不同 JVM/环境下算法实现不一致。
 * 2. 单例 Provider：内部维护一个单例 Provider（PROVIDER），在类加载时优先查找配置值
 *    {@link DefaultConstantValue#DEFAULT_SEC_PROVIDER}，不存在时注册并使用 BouncyCastle。
 * 3. 责任单一：仅封装那些具有 getInstance(algorithm, Provider) 重载的方法。
 *    没有 Provider 重载的构造（如 SecureRandom、KeyStore、SSLContext 等）不在本类中提供。
 * 4. 错误处理统一：方法内部通过 {@link Fn#jvmOr} 包装，简化调用方异常处理。
 * 5. 命名规范：方法统一以 {@code ofXxx} 命名，其中 Xxx 为返回类型名。
 * </pre>
 *
 * <p>线程与实例注意：</p>
 * <pre>
 * - 大多数 JCE 实例（Cipher、Signature、MessageDigest 等）并非线程安全，应为每个线程/操作获取独立实例。
 * - KeyGenerator / KeyPairGenerator 可复用，但通常建议每次操作新建实例，避免状态干扰。
 * </pre>
 *
 * @author lang
 * @since 2025-09-19
 */
public final class JceProvider {

    private static final Provider PROVIDER;

    static {
        Provider p = Security.getProvider(DefaultConstantValue.DEFAULT_SEC_PROVIDER);
        if (Objects.isNull(p)) {
            p = new BouncyCastleProvider();
            Security.addProvider(p);
        }
        PROVIDER = p;
    }

    private JceProvider() {
    }

    /**
     * 返回内部使用的 Provider。
     *
     * @return 单例 Provider
     */
    public static Provider provider() {
        return PROVIDER;
    }

    // ========================= 对称加密 =========================

    /**
     * 获取 Cipher 实例。
     *
     * @param algorithm 算法，如 "AES/GCM/NoPadding"
     *
     * @return Cipher 实例
     */
    public static Cipher ofCipher(final String algorithm) {
        return Fn.jvmOr(() -> Cipher.getInstance(algorithm, PROVIDER));
    }

    /**
     * 生成对称密钥。
     *
     * @param spec 算法规范（如 AES-256）
     *
     * @return SecretKey 实例
     */
    public static SecretKey ofSecretKey(final AlgLicenseSpec spec) {
        return Fn.jvmOr(() -> {
            final KeyGenerator kg = KeyGenerator.getInstance(spec.alg(), PROVIDER);
            kg.init(spec.length());
            return kg.generateKey();
        });
    }

    /**
     * 获取 SecretKeyFactory。
     *
     * @param algorithm 算法，如 "PBKDF2WithHmacSHA256"
     *
     * @return SecretKeyFactory 实例
     */
    public static SecretKeyFactory ofSecretKeyFactory(final String algorithm) {
        return Fn.jvmOr(() -> SecretKeyFactory.getInstance(algorithm, PROVIDER));
    }

    /**
     * 获取 Mac 实例。
     *
     * @param algorithm 算法，如 "HmacSHA256"
     *
     * @return Mac 实例
     */
    public static Mac ofMac(final String algorithm) {
        return Fn.jvmOr(() -> Mac.getInstance(algorithm, PROVIDER));
    }

    // ========================= 非对称加密 =========================

    /**
     * 获取 KeyFactory。
     *
     * @param type 算法，如 "RSA", "EC"
     *
     * @return KeyFactory 实例
     */
    public static KeyFactory ofKeyFactory(final String type) {
        return Fn.jvmOr(() -> KeyFactory.getInstance(type, PROVIDER));
    }

    /**
     * 生成 KeyPair（公私钥对）。
     *
     * @param spec 算法规范（如 RSA-2048, ECDSA-P256）
     *
     * @return KeyPair 实例
     */
    public static KeyPair ofKeyPair(final AlgLicenseSpec spec) {
        return Fn.jvmOr(() -> {
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance(spec.alg(), PROVIDER);
            kpg.initialize(spec.length());
            return kpg.generateKeyPair();
        });
    }

    /**
     * 获取 Signature。
     *
     * @param algorithm 算法，如 "SHA256withRSA"
     *
     * @return Signature 实例
     */
    public static Signature ofSignature(final String algorithm) {
        return Fn.jvmOr(() -> Signature.getInstance(algorithm, PROVIDER));
    }

    /**
     * 获取 KeyAgreement。
     *
     * @param algorithm 算法，如 "ECDH", "X25519"
     *
     * @return KeyAgreement 实例
     */
    public static KeyAgreement ofKeyAgreement(final String algorithm) {
        return Fn.jvmOr(() -> KeyAgreement.getInstance(algorithm, PROVIDER));
    }

    // ========================= 摘要 / 参数 =========================

    /**
     * 获取 MessageDigest。
     *
     * @param algorithm 算法，如 "SHA-256"
     *
     * @return MessageDigest 实例
     */
    public static MessageDigest ofMessageDigest(final String algorithm) {
        return Fn.jvmOr(() -> MessageDigest.getInstance(algorithm, PROVIDER));
    }

    /**
     * 获取 AlgorithmParameters。
     *
     * @param algorithm 算法，如 "PSS", "EC"
     *
     * @return AlgorithmParameters 实例
     */
    public static AlgorithmParameters ofAlgorithmParameters(final String algorithm) {
        return Fn.jvmOr(() -> AlgorithmParameters.getInstance(algorithm, PROVIDER));
    }

    /**
     * 获取 AlgorithmParameterGenerator。
     *
     * @param algorithm 算法，如 "DH"
     *
     * @return AlgorithmParameterGenerator 实例
     */
    public static AlgorithmParameterGenerator ofAlgorithmParameterGenerator(final String algorithm) {
        return Fn.jvmOr(() -> AlgorithmParameterGenerator.getInstance(algorithm, PROVIDER));
    }
}
