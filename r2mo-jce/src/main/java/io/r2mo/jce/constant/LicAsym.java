package io.r2mo.jce.constant;

import io.r2mo.jce.component.lic.AlgLicenseSpec;

/**
 * @author lang : 2025-09-20
 */
public final class LicAsym {
    private LicAsym() {
    }

    /** ECC 系列 */
    public enum AlgLicenseEcc implements AlgLicenseSpec {

        /**
         * ECC P-256 (ECDSA)
         *
         * <p>优势：高性能，安全性足够，国际广泛支持。
         * <p>五维评分：安全性 4/5，性能 5/5，兼容性 4/5，使用率 4/5，扩展性 4/5
         * <p>综合评分：21/25 ★★★★☆
         * <p>推荐度：🟩 强推荐（性能最佳，适合移动端/高并发）
         */
        ECDSA_P256("EC", 256, "ECIES", "SHA256withECDSA"),

        /**
         * ECC P-384 (ECDSA)
         *
         * <p>优势：安全性更高，适合长期存档与政府系统。
         * <p>五维评分：安全性 5/5，性能 4/5，兼容性 4/5，使用率 3/5，扩展性 4/5
         * <p>综合评分：20/25 ★★★★☆
         * <p>推荐度：🟦 高（安全优先场景的推荐选择）
         */
        ECDSA_P384("EC", 384, "ECIES", "SHA384withECDSA"),

        /**
         * ECC P-521 (ECDSA)
         *
         * <p>优势：安全性最高，但兼容性稍弱。
         * <p>五维评分：安全性 5/5，性能 3/5，兼容性 3/5，使用率 2/5，扩展性 4/5
         * <p>综合评分：17/25 ★★★☆☆
         * <p>推荐度：🟨 中立（仅在需要极高安全性的专业场景使用）
         */
        ECDSA_P521("EC", 521, "ECIES", "SHA512withECDSA");

        private final String alg;
        private final int length;
        private final String algCipher;
        private final String algSign;

        AlgLicenseEcc(final String alg, final int length, final String algCipher, final String algSign) {
            this.alg = alg;
            this.length = length;
            this.algCipher = algCipher;
            this.algSign = algSign;
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

        @Override
        public String algSign() {
            return this.algSign;
        }
    }

    /** 现代曲线 EdDSA / XDH 系列 */
    public enum AlgLicenseModern implements AlgLicenseSpec {

        /**
         * Ed25519
         *
         * <p>优势：现代签名算法，简洁高效，抗侧信道攻击能力强。
         * <p>五维评分：安全性 5/5，性能 5/5，兼容性 3/5，使用率 3/5，扩展性 5/5
         * <p>综合评分：21/25 ★★★★☆
         * <p>推荐度：🟩 强推荐（新一代标准，未来趋势）
         */
        ED25519("EdDSA", 255, null, "Ed25519"),

        /**
         * Ed448
         *
         * <p>优势：更高的安全边界，但兼容性不足。
         * <p>五维评分：安全性 5/5，性能 4/5，兼容性 2/5，使用率 2/5，扩展性 4/5
         * <p>综合评分：17/25 ★★★☆☆
         * <p>推荐度：🟨 中立（适合学术与高安全实验场景）
         */
        ED448("EdDSA", 448, null, "Ed448"),

        /**
         * X25519
         *
         * <p>优势：高效的密钥交换，已广泛应用于 TLS 1.3。
         * <p>五维评分：安全性 5/5，性能 5/5，兼容性 4/5，使用率 4/5，扩展性 5/5
         * <p>综合评分：23/25 ★★★★★
         * <p>推荐度：🟩 强推荐（现代 TLS 的首选曲线）
         */
        X25519("XDH", 255, "X25519", null),

        /**
         * X448
         *
         * <p>优势：安全性更高，适合长期密钥保护。
         * <p>五维评分：安全性 5/5，性能 4/5，兼容性 2/5，使用率 2/5，扩展性 4/5
         * <p>综合评分：17/25 ★★★☆☆
         * <p>推荐度：🟨 中立（偏专业化应用）
         */
        X448("XDH", 448, "X448", null);

        private final String alg;
        private final int length;
        private final String algCipher;
        private final String algSign;

        AlgLicenseModern(final String alg, final int length, final String algCipher, final String algSign) {
            this.alg = alg;
            this.length = length;
            this.algCipher = algCipher;
            this.algSign = algSign;
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

        @Override
        public String algSign() {
            return this.algSign;
        }
    }

    /** RSA 系列 */
    public enum AlgLicenseRsa implements AlgLicenseSpec {

        /**
         * RSA 2048
         *
         * <p>优势：广泛支持，性能和安全性兼顾，适合绝大多数企业系统。
         * <p>五维评分：安全性 4/5，性能 3/5，兼容性 5/5，使用率 5/5，扩展性 3/5
         * <p>综合评分：20/25 ★★★★☆
         * <p>推荐度：🟦 高（兼容性最好，当前最常用的 RSA 密钥长度）
         */
        RSA_2048("RSA", 2048, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "SHA256withRSA"),

        /**
         * RSA 3072
         *
         * <p>优势：比 2048 更安全，逐渐被推荐作为长期替代方案。
         * <p>五维评分：安全性 5/5，性能 3/5，兼容性 5/5，使用率 4/5，扩展性 4/5
         * <p>综合评分：21/25 ★★★★☆
         * <p>推荐度：🟩 强推荐（适合对安全要求较高的企业应用）
         */
        RSA_3072("RSA", 3072, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "SHA256withRSA"),

        /**
         * RSA 4096
         *
         * <p>优势：安全性最高，但计算性能消耗大。
         * <p>五维评分：安全性 5/5，性能 2/5，兼容性 5/5，使用率 3/5，扩展性 4/5
         * <p>综合评分：19/25 ★★★★☆
         * <p>推荐度：🟨 中立（仅适合安全极端敏感但不追求性能的场景）
         */
        RSA_4096("RSA", 4096, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "SHA512withRSA");

        private final String alg;
        private final int length;
        private final String algCipher;
        private final String algSign;

        AlgLicenseRsa(final String alg, final int length, final String algCipher, final String algSign) {
            this.alg = alg;
            this.length = length;
            this.algCipher = algCipher;
            this.algSign = algSign;
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

        @Override
        public String algSign() {
            return this.algSign;
        }
    }

    /** 国密 SM2 系列 */
    public enum AlgLicenseSm2 implements AlgLicenseSpec {

        /**
         * SM2
         *
         * <p>优势：符合中国国家密码标准（GM/T 0003），强制要求合规环境使用。
         * <p>五维评分：安全性 4/5，性能 4/5，兼容性 2/5，使用率 3/5，扩展性 3/5
         * <p>综合评分：16/25 ★★★☆☆
         * <p>推荐度：🟨 中立（适合国密合规场景，国际兼容性较差）
         */
        SM2_DEFAULT("SM2", 256, "SM2", "SM3withSM2");

        private final String alg;
        private final int length;
        private final String algCipher;
        private final String algSign;

        AlgLicenseSm2(final String alg, final int length, final String algCipher, final String algSign) {
            this.alg = alg;
            this.length = length;
            this.algCipher = algCipher;
            this.algSign = algSign;
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

        @Override
        public String algSign() {
            return this.algSign;
        }
    }
}
