package io.r2mo.jce.common;

import io.r2mo.jce.component.lic.AlgLicenseSpec;
import io.r2mo.jce.constant.AlgHash;
import io.r2mo.jce.constant.LicAsym;

import java.security.KeyPair;

/**
 * EDExecutor / EDSymmetric
 * 对称和非对称加解密工具类
 *
 * @author lang : 2025-09-19
 */
public final class HED {

    private HED() {
    }

    public static KeyPair generate(final AlgLicenseSpec spec) {
        return JceProvider.ofKeyPair(spec);
    }
    // ==================== 强推荐算法 ====================

    // region generate 的几个典型静态工具接口

    /**
     * 生成 ECC ECDSA（P-256）密钥对
     *
     * <p><b>默认参数：</b>P-256 曲线（256 位）。</p>
     *
     * <p><b>背景：</b>ECDSA (Elliptic Curve Digital Signature Algorithm) 是基于椭圆曲线的数字签名算法，
     * 相比传统的 RSA 能在更小的密钥长度下提供相同级别的安全性。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>高性能：签名与验证速度快，特别适合高并发和资源受限设备。</li>
     *   <li>安全性：256 位 ECC 安全等级大约等同于 3072 位 RSA。</li>
     *   <li>兼容性：被 TLS、JWT、区块链等广泛支持。</li>
     * </ul>
     *
     * <p><b>常见应用：</b>HTTPS/TLS 证书、移动端 API 签名、区块链钱包。</p>
     *
     * @return KeyPair (EC P-256)
     */
    public static KeyPair generateEC() {
        return generate(LicAsym.AlgLicenseEcc.ECDSA_P256);
    }

    /**
     * 生成 RSA 密钥对
     *
     * <p><b>默认参数：</b>3072 位。</p>
     *
     * <p><b>背景：</b>RSA 是最经典的公钥密码算法之一，广泛应用于加密与数字签名。
     * 随着计算能力提升，RSA 2048 位逐渐不够安全，3072 位被认为是未来的主流安全标准。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>兼容性最好：几乎所有语言和平台都原生支持。</li>
     *   <li>成熟稳健：经过数十年的验证，安全性可靠。</li>
     *   <li>适合长期应用：比 2048 更强的安全性，推荐企业使用。</li>
     * </ul>
     *
     * <p><b>局限：</b>性能不如 ECC/EdDSA，密钥和签名长度较大。</p>
     *
     * <p><b>常见应用：</b>SSL/TLS、电子邮件加密 (S/MIME)、文档签名 (PDF, Office)。</p>
     *
     * @return KeyPair (RSA 3072)
     */
    public static KeyPair generateRSA() {
        return generate(LicAsym.AlgLicenseRsa.RSA_3072);
    }

    /**
     * 生成 EdDSA (Ed25519) 密钥对
     *
     * <p><b>默认参数：</b>Ed25519 曲线。</p>
     *
     * <p><b>背景：</b>EdDSA (Edwards-curve Digital Signature Algorithm) 是一种现代数字签名算法，
     * 其中 Ed25519 是目前应用最广泛的实例，具有高性能和强安全性。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>性能极高：签名和验证都比 ECDSA 更快。</li>
     *   <li>安全性强：抗侧信道攻击，避免随机数问题。</li>
     *   <li>简洁易用：密钥和签名长度固定（32B 公钥，64B 签名）。</li>
     * </ul>
     *
     * <p><b>常见应用：</b>SSH、加密通信协议（如 Signal）、区块链（如 Solana, Cardano）。</p>
     *
     * @return KeyPair (Ed25519)
     */
    public static KeyPair generateEd25519() {
        return generate(LicAsym.AlgLicenseModern.ED25519);
    }

    /**
     * 生成 XDH (X25519) 密钥对
     *
     * <p><b>默认参数：</b>X25519 曲线。</p>
     *
     * <p><b>背景：</b>X25519 是基于 Curve25519 的密钥交换算法，
     * 已成为现代 TLS 1.3 的默认选择之一，用于高效的椭圆曲线 Diffie–Hellman (ECDH)。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>高性能：比传统 ECDH 更快。</li>
     *   <li>安全性强：避免弱曲线问题，默认安全参数。</li>
     *   <li>简洁：密钥长度固定（32B），实现简单。</li>
     * </ul>
     *
     * <p><b>常见应用：</b>TLS 1.3、VPN (WireGuard)、安全消息协议。</p>
     *
     * @return KeyPair (X25519)
     */
    public static KeyPair generateX25519() {
        return generate(LicAsym.AlgLicenseModern.X25519);
    }
    // endregion

    // ===== 编解码

    // region encode 系列的基础工具接口

    // Base64
    public static String encodeBase64(final String data) {
        return Coder.of(CoderBase64::new).encode(data);
    }

    public static String decodeBase64(final String data) {
        return Coder.of(CoderBase64::new).decode(data);
    }

    // Hex
    public static String encodeHEX(final String data) {
        return Coder.of(CoderHEX::new).encode(data);
    }

    public static String decodeHEX(final String data) {
        return Coder.of(CoderHEX::new).decode(data);
    }

    // URL
    public static String encodeURL(final String data) {
        return Coder.of(CoderURL::new).encode(data);
    }

    public static String decodeURL(final String data) {
        return Coder.of(CoderURL::new).decode(data);
    }

    // endregion

    // region encode 关于选择算法之后的公私钥专用接口

    // endregion

    // ===== 哈希算法（密码加密用）

    // region encrypt 的几个典型的加密接口

    /**
     * 使用 SHA-256 对字符串进行哈希计算。
     *
     * <p><b>算法定义：</b>SHA-256 属于 SHA-2 系列，是国际标准哈希算法，输出 256 位（32 字节）。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>安全性与性能兼顾，抗碰撞性强。</li>
     *   <li>广泛支持，几乎所有平台/语言均有内置实现。</li>
     *   <li>常用于 TLS 握手、JWT 签名、区块链交易哈希。</li>
     * </ul>
     *
     * <p><b>推荐度：</b>🟩 强推荐（当前主流的安全哈希算法）。</p>
     *
     * @param data 输入字符串
     *
     * @return 64 位十六进制字符串表示的 SHA-256 哈希
     */
    public static String encryptSHA256(final String data) {
        return EDHasher.encrypt(data, AlgHash.SHA256);
    }

    /**
     * 使用 BLAKE2b 对字符串进行哈希计算。
     *
     * <p><b>算法定义：</b>BLAKE2b 是 BLAKE 家族成员，基于 SHA-3 竞赛算法优化，
     * 输出默认 512 位（可配置），在性能和安全性上均优于 SHA-2。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>速度快于 SHA-2，同时具有相同的安全强度。</li>
     *   <li>支持可变输出长度，灵活性强。</li>
     *   <li>已被广泛应用于密码学库、文件完整性校验。</li>
     * </ul>
     *
     * <p><b>推荐度：</b>🟩 强推荐（高性能安全应用场景）。</p>
     *
     * @param data 输入字符串
     *
     * @return 十六进制字符串表示的 BLAKE2b 哈希
     */
    public static String encryptBLAKE2B(final String data) {
        return EDHasher.encrypt(data, AlgHash.BLAKE2B);
    }

    /**
     * 使用 PBKDF2 对字符串进行密码哈希。
     *
     * <p><b>算法定义：</b>PBKDF2 (Password-Based Key Derivation Function 2)，
     * 由 NIST 标准化，使用 HMAC（默认 HMAC-SHA-256）并结合随机盐与迭代次数，
     * 生成安全的哈希值，常用于密码存储。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>被绝大多数语言和框架支持，兼容性极佳。</li>
     *   <li>通过迭代次数提高破解成本，有效防御暴力破解。</li>
     *   <li>已成为企业级通用密码存储方案。</li>
     * </ul>
     *
     * <p><b>推荐度：</b>🟩 强推荐（企业级通用方案）。</p>
     *
     * @param data 输入密码
     *
     * @return PBKDF2 编码串（包含盐与迭代参数）
     */
    public static String encryptPBKDF2(final String data) {
        return EDHasher.encrypt(data, AlgHash.PBKDF2);
    }

    /**
     * 使用 bcrypt 对字符串进行密码哈希。
     *
     * <p><b>算法定义：</b>bcrypt 专为密码存储设计，自动生成随机盐，
     * 并使用工作因子（cost）控制计算复杂度，天然抗 GPU 暴力破解。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>设计简洁安全，内置盐，不需要外部存储。</li>
     *   <li>支持工作因子调整，便于适应硬件性能提升。</li>
     *   <li>在 Web 系统和身份认证领域被广泛使用。</li>
     * </ul>
     *
     * <p><b>推荐度：</b>🟩 强推荐（Web 系统首选密码存储算法）。</p>
     *
     * @param data 输入密码
     *
     * @return bcrypt 编码串（包含盐和 cost 参数）
     */
    public static String encryptBCRYPT(final String data) {
        return EDHasher.encrypt(data, AlgHash.BCRYPT);
    }

    /**
     * 使用 Argon2 对字符串进行密码哈希。
     *
     * <p><b>算法定义：</b>Argon2 是密码哈希大赛（PHC）获胜算法，
     * 支持内存消耗、迭代次数和并行度调节，被认为是现代最安全的密码哈希算法之一。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>内存硬计算，极大提升 GPU/ASIC 破解难度。</li>
     *   <li>灵活参数配置，兼顾安全性和性能。</li>
     *   <li>正在逐渐成为密码学标准（RFC 9106）。</li>
     * </ul>
     *
     * <p><b>推荐度：</b>🟩 强推荐（新系统的首选默认方案）。</p>
     *
     * @param data 输入密码
     *
     * @return Argon2 编码串（包含盐与参数信息）
     */
    public static String encryptARGON2(final String data) {
        return EDHasher.encrypt(data, AlgHash.ARGON2);
    }

    // endregion

    public static String encrypt(final String data, final AlgHash algorithm) {
        return EDHasher.encrypt(data, algorithm);
    }
}