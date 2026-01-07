package io.r2mo.jce.common;

import io.r2mo.jce.component.secure.ED;
import io.r2mo.jce.constant.AlgHash;
import io.r2mo.jce.constant.AlgLicense;
import io.r2mo.jce.constant.AlgNorm;

import java.security.KeyPair;

/**
 * EDExecutor / EDSymmetric
 * 对称和非对称加解密工具类
 *
 * @author lang : 2025-09-19
 */
public final class HED extends HEDBase {

    private HED() {
    }

    public static void initialize() {
        JceProvider.configure();
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
        return generate(AlgLicense.ECC);
    }

    /**
     * 生成 SM2 密钥对
     *
     * <p><b>默认参数：</b>SM2 曲线（256 位），符合中国国家密码标准 GM/T 0003。</p>
     *
     * <p><b>背景：</b>SM2 是基于椭圆曲线密码学（ECC）的公钥密码算法，广泛应用于数字签名、密钥交换和加密。</p>
     *
     * <p><b>优势：</b></p>
     * <ul>
     *   <li>合规性：在中国的政府、金融和电信等领域被强制要求使用。</li>
     *   <li>安全性：基于椭圆曲线，提供与 ECC 类似的高安全性。</li>
     *   <li>生态支持：和 SM3（哈希算法）、SM4（对称加密算法）常常结合使用。</li>
     * </ul>
     *
     * <p><b>劣势：</b></p>
     * <ul>
     *   <li>国际兼容性较差，主要在国内合规场景使用。</li>
     *   <li>与 RSA/ECC 相比，跨国系统集成时可能存在兼容性障碍。</li>
     * </ul>
     *
     * <p><b>常见应用：</b>电子政务系统、金融支付平台、国产操作系统、国密 TLS 协议。</p>
     *
     * @return KeyPair (SM2)
     */
    public static KeyPair generateSM2() {
        return generate(AlgLicense.SM2);
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
        return generate(AlgLicense.RSA);
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
        return generate(AlgLicense.ED25519);
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
     * @return Argon2 编码串（包含盐与参数信息）
     */
    public static String encryptARGON2(final String data) {
        return EDHasher.encrypt(data, AlgHash.ARGON2);
    }

    // endregion

    /**
     * 获取当前机器指纹（可读友好的冒号分隔字符串）
     *
     * <p><b>概述</b>：该方法会收集本机若干环境/硬件信息（如主机名、首个可用网卡 MAC、操作系统与 JVM 信息），
     * 将这些信息拼接成一个原始输入串，再交由底层 {@code EDHasher.encrypt(..., AlgHash.SHA256)}
     * 计算 SHA-256 摘要，最后把得到的十六进制摘要规范化为常见的「冒号分隔」格式返回。</p>
     *
     * <pre>
     * 生成流程（高亮步骤）：
     *  1️⃣  收集信息：
     *       - 🖥️ OS 信息：os.name / os.arch / os.version
     *       - 🌐 主机名：InetAddress.getLocalHost().getHostName()
     *       - 🪪 第一个可用网卡的 MAC（排除 loopback）
     *       - ☕ JVM 信息：java.vendor / java.version
     *
     *  2️⃣  将上述字段拼接为原始字符串（按约定的键值/分隔符），例如：
     *       "HOST=dev-01;MAC=a1b2c3d4e5f6;OS=Linux_x86_64_5.4;JVM=AdoptOpenJDK_11.0.16"
     *
     *  3️⃣  使用 EDHasher.encrypt(rawString, AlgHash.SHA256) 计算 SHA-256（返回 hex 字符串）
     *
     *  4️⃣  将 hex 字符串规范化为冒号分组形式（两字符一组、小写），例如：
     *       "a1:b2:c3:d4:e5:f6:7a:8b:..." （共 32 组，31 个 ':'，长度 = 95）
     * </pre>
     *
     * <h3>🔎 输出格式</h3>
     * <ul>
     *   <li>返回值示例（冒号分隔，全部小写）：<code>"3f:7a:91:22:cd:88:11:09:56:3a:..."</code></li>
     *   <li>来源于 SHA-256（32 字节）按字节两字符一组并以 ':' 连接。</li>
     * </ul>
     *
     * <h3>✅ 使用建议</h3>
     * <ul>
     *   <li>对外显示 / 调试 ：优先使用 {@code fingerString()}，因为冒号分隔对人工检查更友好。</li>
     *   <li>比较指纹时请使用规范化比较（见下面的比较示例），避免直接使用 String.equals() 导致大小写或分隔符问题。</li>
     * </ul>
     *
     * <h3>⚠️ 稳定性 & 风险</h3>
     * <ul>
     *   <li>该指纹 <b>通常</b> 在同一台物理机上保持稳定；但下列操作可能导致变化：更换/禁用网卡、重装或升级操作系统、修改主机名、升级 JVM、运行于某些云/容器环境（MAC 可能是动态的）。</li>
     *   <li>在虚拟化或容器化环境中不建议仅依赖单一指纹做强绑定，建议结合服务器端策略或容错匹配。</li>
     * </ul>
     *
     * <h3>🔐 安全建议</h3>
     * <ul>
     *   <li>不要把指纹当作秘密密钥直接下发或作为加密凭证。若用于验证/绑定，建议在服务端用 HMAC（带服务器密钥）或对指纹进行加密存储。</li>
     *   <li>比较指纹建议使用 {@link java.security.MessageDigest#isEqual(byte[], byte[])} 对比解码后的字节数组以减少定时攻击风险（如需）。</li>
     * </ul>
     *
     * <h3>📌 示例：如何把冒号格式与 raw hex 做安全比较</h3>
     * <pre>
     * // 假设 s1/s2 分别为两端获取到的冒号格式指纹（fingerString()）
     * String hex1 = s1.replace(\":\", \"\");   // 去除冒号
     * String hex2 = s2.replace(\":\", \"\");
     * byte[] b1 = hexToBytes(hex1);
     * byte[] b2 = hexToBytes(hex2);
     * boolean same = java.security.MessageDigest.isEqual(b1, b2);
     * </pre>
     *
     * <h3>🧾 异常/返回</h3>
     * <ul>
     *   <li>方法为便捷包装，内部会调用 {@code HEDFinger.fingerString()}。遇到底层异常（如网络接口读取失败、哈希失败）会抛出 {@link RuntimeException}。</li>
     *   <li>该方法为无状态、线程安全的静态工具方法，可并发调用。</li>
     * </ul>
     *
     * @return 冒号分隔的机器指纹字符串（小写 hex，形如 {@code aa:bb:cc:...}）
     * @since 2025-09-21
     */
    public static String fingerString() {
        return HEDFinger.fingerString();
    }

    /**
     * 获取当前机器指纹的原始十六进制表示（连续 hex 字符串）
     *
     * <p><b>概述</b>：与 {@link #fingerString()} 同源（收集相同字段并用 {@code EDHasher.encrypt(..., AlgHash.SHA256)} 计算 SHA-256 摘要），
     * 但直接返回不带分隔符的十六进制字符串，适合机器存储、索引或作为数据库字段。</p>
     *
     * <pre>
     * 输出说明：
     *  - 格式：连续十六进制字符（小写），长度通常为 64（代表 32 字节的 SHA-256 摘要）
     *  - 示例： "a1f9e58c7d29311f39b57c7a8d0f4e21d6b84a0b2c3d45f2f11e0f5c9d7a2e33"
     * </pre>
     *
     * <h3>📌 何时使用 fingerHex()</h3>
     * <ul>
     *   <li>作为数据库字段（VARCHAR(64)）保存或索引时优先使用此方法。</li>
     *   <li>在网络协议或文件中作为规范化标识传输时优先使用 raw hex（减少分隔字符带来的协议处理）。</li>
     * </ul>
     *
     * <h3>比较/校验示例</h3>
     * <pre>
     * String h1 = HEDFinger.fingerHex();
     * String h2 = otherSourceHex;
     * // 推荐：先把 hex 转成字节数组再用 MessageDigest.isEqual 比较
     * byte[] b1 = hexToBytes(h1);
     * byte[] b2 = hexToBytes(h2);
     * boolean same = java.security.MessageDigest.isEqual(b1, b2);
     * </pre>
     *
     * <h3>⚠️ 注意事项（与 fingerString 相同）</h3>
     * <ul>
     *   <li>指纹并非 100% 永久不变：主机名、网卡、OS/JVM 变化都会影响结果。</li>
     *   <li>在云/容器化环境使用需谨慎，建议结合其他绑定/校验机制。</li>
     * </ul>
     *
     * @return 十六进制字符串（不含分隔符，通常长度 = 64）
     * @since 2025-09-21
     */
    public static String fingerHex() {
        return HEDFinger.fingerHex();
    }

    // -------------- 编程过程中的快速模式 --------------

    public static String encryptByPublic(final String input, final String keyContent, final AlgNorm algNorm) {
        return ED.encryptOfPublic(algNorm).encrypt(input, keyContent);
    }

    public static String decryptByPrivate(final String input, final String keyContent, final AlgNorm algNorm) {
        return ED.encryptOfPublic(algNorm).decrypt(input, keyContent);
    }

    public static String encryptByPrivate(final String input, final String keyContent, final AlgNorm algNorm) {
        return ED.encryptOfPrivate(algNorm).encrypt(input, keyContent);
    }

    public static String decryptByPublic(final String input, final String keyContent, final AlgNorm algNorm) {
        return ED.encryptOfPrivate(algNorm).decrypt(input, keyContent);
    }

    public static String encryptRSAPublic(final String input, final String keyContent) {
        return encryptByPublic(input, keyContent, AlgNorm.RSA);
    }

    public static String decryptRSAPrivate(final String input, final String keyContent) {
        return decryptByPrivate(input, keyContent, AlgNorm.RSA);
    }

    public static String encryptRSAPrivate(final String input, final String keyContent) {
        return encryptByPrivate(input, keyContent, AlgNorm.RSA);
    }

    public static String decryptRSAPublic(final String input, final String keyContent) {
        return decryptByPrivate(input, keyContent, AlgNorm.RSA);
    }
}