package io.r2mo.jce.common;

import io.r2mo.function.Fn;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 * <pre>
 * 🔐 对称加密核心逻辑 (Symmetric Crypto Core)
 * =============================================================================
 * 此模块实现了基于 JCE 的通用加密/解密封装，并通过策略分发模式（Strategy Dispatch）
 * 自动适配不同的算法工作模式（Mode of Operation）。
 *
 * 💡 为什么需要这样设计？(Design Rationale)
 * -----------------------------------------------------------------------------
 * 在 Java 原生 Cipher API 中，不同的加密模式对初始化参数的要求完全不同：
 *
 * 1. ECB 模式：
 * 无状态，仅需要 SecretKey 即可初始化，但安全性较差（不推荐）。
 *
 * 2. CBC 模式：
 * 依赖初始化向量 (IV) 来保证语义安全，必须通过 IvParameterSpec 传入。
 *
 * 3. GCM 模式 (推荐)：
 * 属于 AEAD (认证加密)，不仅需要 IV，还需要处理 Tag。必须通过 GCMParameterSpec 初始化。
 *
 * 👉 旧代码的问题：
 * 之前统一使用 cipher.init(MODE, key) 会导致 GCM/CBC 模式抛出
 * "InvalidKeyException: no IV set" 异常。
 *
 * 👉 新代码的方案：
 * 本实现作为底层黑盒，自动处理了 IV 的 [生成] -> [存储] -> [提取]。
 * 上层业务（如 Token 生成器）无需关心 IV 存在哪，只需传入原始数据和密钥即可。
 *
 * ⚙️ 核心原理与字节结构 (Mechanism & Protocol)
 * -----------------------------------------------------------------------------
 * 为了保证密文的自包含性（Self-Contained），我们将随机生成的 IV 直接拼接在密文头部。
 * 解密时，根据算法规范自动切分头部数据还原 IV。
 *
 * 🛡️ GCM 模式 (AES/GCM/NoPadding) - [Bank Grade / Token Preferred]
 * - 加密原理：生成 12 字节标准随机 Nonce (IV)，进行流式加密并计算 Ghash (Tag)。
 * - 字节结构：[ 12字节 IV ] + [ 密文 CipherText ... ] + [ 128位 AuthTag ]
 * - 解密原理：读取前 12 字节初始化 GCMParameterSpec，剩余部分解密。
 *
 * 📦 CBC 模式 (AES/CBC/PKCS5Padding) - [Legacy Compatible]
 * - 加密原理：生成与 Block Size 等长 (通常 16 字节) 的随机 IV。
 * - 字节结构：[ 16字节 IV ] + [ 密文 CipherText ... ]
 * - 解密原理：读取前 16 字节初始化 IvParameterSpec，剩余部分解密。
 *
 * 🚀 性能与优化 (Performance)
 * -----------------------------------------------------------------------------
 * ✅ 零拷贝解密 (Zero-Copy):
 * 在解密逻辑中，利用 GCMParameterSpec(tLen, src, offset, len) 和
 * cipher.doFinal(input, offset, len) API，直接在原始字节数组上进行操作，
 * 避免了手动 Arrays.copyOfRange 带来的内存复制开销。
 *
 * 🔍 使用场景建议 (Scenarios)
 * -----------------------------------------------------------------------------
 * * GCM (推荐)     : API Token、敏感隐私数据存储。同时保证机密性和完整性。
 * * CBC            : 与旧系统对接，或者处理大文件加密。
 * * Default (ECB)  : 仅用于简单混淆或测试。
 * </pre>
 *
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
     * 对称加密 (分发入口)
     */
    @Override
    public byte[] encrypt(final byte[] data, final SecretKey secretKey) {
        return Fn.jvmOr(() -> {
            final Cipher cipher = JceProvider.ofCipher(this.algorithm);
            final String mode = this.algorithm.toUpperCase();

            // 模式分发
            if (mode.contains("GCM")) {
                return this.encryptGcm(cipher, data, secretKey);
            }
            if (mode.contains("CBC")) {
                return this.encryptCbc(cipher, data, secretKey);
            }
            return this.encryptDefault(cipher, data, secretKey);
        });
    }

    /**
     * 对称解密 (分发入口)
     */
    @Override
    public byte[] decrypt(final byte[] data, final SecretKey secretKey) {
        return Fn.jvmOr(() -> {
            final Cipher cipher = JceProvider.ofCipher(this.algorithm);
            final String mode = this.algorithm.toUpperCase();

            // 模式分发
            if (mode.contains("GCM")) {
                return this.decryptGcm(cipher, data, secretKey);
            }
            if (mode.contains("CBC")) {
                return this.decryptCbc(cipher, data, secretKey);
            }
            return this.decryptDefault(cipher, data, secretKey);
        });
    }

    // =========================================================================
    // 🔒 私有加密实现
    // =========================================================================

    /**
     * GCM 模式加密
     * 结构: [IV (12)] + [CipherText + Tag]
     */
    private byte[] encryptGcm(final Cipher cipher, final byte[] data, final SecretKey key) throws Exception {
        // 1. 生成 12 字节标准 IV
        final byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        // 2. 初始化
        final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        // 3. 执行加密
        final byte[] cipherText = cipher.doFinal(data);

        // 4. 拼接返回
        return ByteBuffer.allocate(iv.length + cipherText.length)
            .put(iv)
            .put(cipherText)
            .array();
    }

    /**
     * CBC 模式加密
     * 结构: [IV (BlockSize)] + [CipherText]
     */
    private byte[] encryptCbc(final Cipher cipher, final byte[] data, final SecretKey key) throws Exception {
        // 1. 生成块大小的 IV (通常 16 字节)
        final byte[] iv = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(iv);

        // 2. 初始化
        final IvParameterSpec spec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        // 3. 执行加密
        final byte[] cipherText = cipher.doFinal(data);

        // 4. 拼接返回
        return ByteBuffer.allocate(iv.length + cipherText.length)
            .put(iv)
            .put(cipherText)
            .array();
    }

    /**
     * 默认模式加密 (如 ECB)
     */
    private byte[] encryptDefault(final Cipher cipher, final byte[] data, final SecretKey key) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    // =========================================================================
    // 🔓 私有解密实现
    // =========================================================================

    /**
     * GCM 模式解密
     * 预期: [IV (12)] + [CipherText]
     */
    private byte[] decryptGcm(final Cipher cipher, final byte[] data, final SecretKey key) throws Exception {
        final int ivLen = 12;
        if (data.length < ivLen) {
            throw new IllegalArgumentException("[ R2MO ] GCM 解密失败：数据长度不足，无法提取 IV (至少 12 字节)");
        }

        // 直接读取 IV
        final GCMParameterSpec spec = new GCMParameterSpec(128, data, 0, ivLen);

        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        // 解密剩余部分
        return cipher.doFinal(data, ivLen, data.length - ivLen);
    }

    /**
     * CBC 模式解密
     * 预期: [IV (BlockSize)] + [CipherText]
     */
    private byte[] decryptCbc(final Cipher cipher, final byte[] data, final SecretKey key) throws Exception {
        final int ivLen = cipher.getBlockSize();
        if (data.length < ivLen) {
            throw new IllegalArgumentException("[ R2MO ] CBC 解密失败：数据长度不足，无法提取 IV (至少 " + ivLen + " 字节)");
        }

        // 直接读取 IV
        final IvParameterSpec spec = new IvParameterSpec(data, 0, ivLen);

        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        return cipher.doFinal(data, ivLen, data.length - ivLen);
    }

    /**
     * 默认模式解密
     */
    private byte[] decryptDefault(final Cipher cipher, final byte[] data, final SecretKey key) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }
}
