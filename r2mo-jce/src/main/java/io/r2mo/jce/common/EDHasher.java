package io.r2mo.jce.common;

import io.r2mo.jce.constant.AlgHash;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.generators.OpenBSDBCrypt;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;

/**
 * EDHasher 提供统一的字符串哈希/密码加密入口
 *
 * <p>仅开放一个公开方法：
 * <pre>
 *   static String encrypt(final String data, final AlgHash algorithm)
 * </pre>
 *
 * <p>内部根据算法类型：
 * <ul>
 *   <li>标准算法（SHA-2 / SHA-3 / SM3 / BLAKE2B） → 使用 JDK / BouncyCastle MessageDigest</li>
 *   <li>密码哈希算法（PBKDF2 / bcrypt / scrypt / Argon2） → 使用 BouncyCastle 实现，返回包含盐和参数的编码串</li>
 * </ul>
 *
 * <p>注意：密码哈希算法的返回值为 <b>带盐的不可逆编码字符串</b>，适合存储并用于后续验证。
 *
 * @author lang
 * @since 2025-09-19
 */
class EDHasher {

    private static final SecureRandom RANDOM = new SecureRandom();

    // ===== PBKDF2 默认参数 =====
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final int PBKDF2_SALT_LEN = 16;
    private static final int PBKDF2_DK_LEN = 32;

    // ===== bcrypt 默认参数 =====
    private static final int BCRYPT_SALT_LEN = 16;
    private static final int BCRYPT_COST = 12;

    // ===== scrypt 默认参数 =====
    private static final int SCRYPT_N = 16384;
    private static final int SCRYPT_R = 8;
    private static final int SCRYPT_P = 1;
    private static final int SCRYPT_SALT_LEN = 16;
    private static final int SCRYPT_DK_LEN = 32;

    // ===== Argon2 默认参数 =====
    private static final int ARGON2_MEMORY_KB = 65536;
    private static final int ARGON2_ITERATIONS = 3;
    private static final int ARGON2_PARALLELISM = 1;
    private static final int ARGON2_SALT_LEN = 16;
    private static final int ARGON2_OUT_LEN = 32;

    private EDHasher() {
    }

    /**
     * 统一加密入口
     *
     * @param data      输入字符串
     * @param algorithm 算法枚举
     *
     * @return 哈希结果（标准算法为 Hex；密码哈希算法为编码串）
     */
    static String encrypt(final String data, final AlgHash algorithm) {
        Objects.requireNonNull(algorithm, "[ R2MO ] algorithm 算法参数不能为空");
        if (algorithm.fromJvm()) {
            final MessageDigest digest = JceProvider.ofMessageDigest(algorithm.value());
            final byte[] out = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Hex.toHexString(out);
        } else {
            return switch (algorithm) {
                case PBKDF2 -> pbkdf2(data);
                case BCRYPT -> bcrypt(data);
                case SCRYPT -> scrypt(data);
                case ARGON2 -> argon2(data);
                default -> throw new UnsupportedOperationException("Unsupported algorithm: " + algorithm);
            };
        }
    }

    // ================= 内部实现 =================

    private static String pbkdf2(final String password) {
        try {
            final byte[] salt = new byte[PBKDF2_SALT_LEN];
            RANDOM.nextBytes(salt);
            final javax.crypto.SecretKeyFactory skf =
                javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(), salt, PBKDF2_ITERATIONS, PBKDF2_DK_LEN * 8);
            final byte[] dk = skf.generateSecret(spec).getEncoded();
            return String.format(Locale.ROOT, "pbkdf2$%d$%s$%s",
                PBKDF2_ITERATIONS, Hex.toHexString(salt), Hex.toHexString(dk));
        } catch (final Exception e) {
            throw new RuntimeException("PBKDF2 hashing failed", e);
        }
    }

    private static String bcrypt(final String password) {
        final byte[] salt = new byte[BCRYPT_SALT_LEN];
        RANDOM.nextBytes(salt);
        return OpenBSDBCrypt.generate(password.toCharArray(), salt, BCRYPT_COST);
    }

    private static String scrypt(final String password) {
        try {
            final byte[] salt = new byte[SCRYPT_SALT_LEN];
            RANDOM.nextBytes(salt);
            final byte[] dk = SCrypt.generate(
                password.getBytes(StandardCharsets.UTF_8),
                salt,
                SCRYPT_N, SCRYPT_R, SCRYPT_P,
                SCRYPT_DK_LEN
            );
            return String.format(Locale.ROOT, "scrypt$%d$%d$%d$%s$%s",
                SCRYPT_N, SCRYPT_R, SCRYPT_P, Hex.toHexString(salt), Hex.toHexString(dk));
        } catch (final Exception e) {
            throw new RuntimeException("scrypt hashing failed", e);
        }
    }

    private static String argon2(final String password) {
        try {
            final byte[] salt = new byte[ARGON2_SALT_LEN];
            RANDOM.nextBytes(salt);

            final Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withMemoryAsKB(ARGON2_MEMORY_KB)
                .withIterations(ARGON2_ITERATIONS)
                .withParallelism(ARGON2_PARALLELISM);

            final Argon2BytesGenerator gen = new Argon2BytesGenerator();
            gen.init(builder.build());

            final byte[] out = new byte[ARGON2_OUT_LEN];
            gen.generateBytes(password.getBytes(StandardCharsets.UTF_8), out, 0, ARGON2_OUT_LEN);

            return String.format(Locale.ROOT, "argon2id$m=%d,t=%d,p=%d$%s$%s",
                ARGON2_MEMORY_KB, ARGON2_ITERATIONS, ARGON2_PARALLELISM,
                Hex.toHexString(salt), Hex.toHexString(out));
        } catch (final Exception e) {
            throw new RuntimeException("Argon2 hashing failed", e);
        }
    }
}
