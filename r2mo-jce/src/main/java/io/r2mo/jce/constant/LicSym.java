package io.r2mo.jce.constant;

/**
 * 企业许可的常用对称加密算法（分组 + 子枚举版，带评分与推荐度）
 *
 * <p>特点：
 * <pre>
 * 1. 聚焦对称加密（AES、ChaCha20、SM4 等），用于数据机密性保护。
 * 2. 每个枚举包含算法名称、密钥长度、推荐的 Cipher 算法模式。
 * 3. 提供详细注释（优势、评分、推荐度），便于企业做技术选型。
 * </pre>
 *
 * @author lang
 */
public final class LicSym {
    private LicSym() {
    }

    /** AES 系列 */
    public enum AlgLicenseAes implements AlgLicenseSpec {

        /**
         * AES-128-GCM
         *
         * <p>优势：国际标准，性能优异，广泛支持，适合大多数场景。
         * <p>五维评分：安全性 4/5，性能 5/5，兼容性 5/5，使用率 5/5，扩展性 4/5
         * <p>综合评分：23/25 ★★★★★
         * <p>推荐度：🟩 强推荐（企业默认对称加密首选）
         */
        AES_128("AES", 128, "AES/GCM/NoPadding"),

        /**
         * AES-256-GCM
         *
         * <p>优势：安全性更强，符合合规要求，适合金融、政府、长期存档。
         * <p>五维评分：安全性 5/5，性能 4/5，兼容性 5/5，使用率 4/5，扩展性 5/5
         * <p>综合评分：23/25 ★★★★★
         * <p>推荐度：🟩 强推荐（高安全场景必选）
         */
        AES_256("AES", 256, "AES/GCM/NoPadding");

        private final String alg;
        private final int length;
        private final String algCipher;

        AlgLicenseAes(final String alg, final int length, final String algCipher) {
            this.alg = alg;
            this.length = length;
            this.algCipher = algCipher;
        }

        @Override
        public String alg() {
            return this.alg;
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public String algCipher() {
            return this.algCipher;
        }
    }

    /** ChaCha20 系列 */
    public enum AlgLicenseChaCha implements AlgLicenseSpec {

        /**
         * ChaCha20-Poly1305
         *
         * <p>优势：由 Google 推广，TLS 1.3 支持，速度快，适合移动端/低硬件设备。
         * <p>五维评分：安全性 5/5，性能 5/5，兼容性 4/5，使用率 3/5，扩展性 4/5
         * <p>综合评分：21/25 ★★★★☆
         * <p>推荐度：🟩 强推荐（移动端和 VPN 常用）
         */
        CHACHA20("ChaCha20", 256, "ChaCha20-Poly1305");

        private final String alg;
        private final int length;
        private final String algCipher;

        AlgLicenseChaCha(final String alg, final int length, final String algCipher) {
            this.alg = alg;
            this.length = length;
            this.algCipher = algCipher;
        }

        @Override
        public String alg() {
            return this.alg;
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public String algCipher() {
            return this.algCipher;
        }
    }

    /** 国密 SM4 系列 */
    public enum AlgLicenseSm4 implements AlgLicenseSpec {

        /**
         * SM4
         *
         * <p>优势：中国国家标准（GM/T 0002），等效 AES-128。
         * <p>五维评分：安全性 4/5，性能 4/5，兼容性 2/5，使用率 3/5，扩展性 3/5
         * <p>综合评分：16/25 ★★★☆☆
         * <p>推荐度：🟨 中立（仅在国密合规环境下强制使用）
         */
        SM4_DEFAULT("SM4", 128, "SM4/ECB/PKCS5Padding");

        private final String alg;
        private final int length;
        private final String algCipher;

        AlgLicenseSm4(final String alg, final int length, final String algCipher) {
            this.alg = alg;
            this.length = length;
            this.algCipher = algCipher;
        }

        @Override
        public String alg() {
            return this.alg;
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public String algCipher() {
            return this.algCipher;
        }
    }
}
