package io.r2mo.jce.constant;

import io.r2mo.jce.component.lic.AlgLicenseSpec;
import lombok.experimental.Accessors;

/**
 * 企业许可的常用非对称加密与签名算法（分组 + 子枚举版，带评分与推荐度，优化版）
 */
@Accessors(fluent = true)
public enum AlgLicense {

    // ===== RSA 系列 =====
    RSA(true, LicAsym.AlgLicenseRsa.class, LicAsym.AlgLicenseRsa.RSA_3072),

    // ===== ECC 系列 =====
    ECC(true, LicAsym.AlgLicenseEcc.class, LicAsym.AlgLicenseEcc.ECDSA_P256),

    // ===== 现代曲线 EdDSA / XDH 系列 =====
    MODERN(true, LicAsym.AlgLicenseModern.class, LicAsym.AlgLicenseModern.ED25519),

    // ===== 国密 SM 系列 =====
    SM2(true, LicAsym.AlgLicenseSm2.class, LicAsym.AlgLicenseSm2.SM2_DEFAULT);

    /** 是否非对称 */
    private final boolean asymmetric;

    /** 子枚举类型 */
    private final Class<? extends AlgLicenseSpec> subEnum;

    /** 默认算法 */
    private final AlgLicenseSpec defaultAlg;

    AlgLicense(final boolean asymmetric,
               final Class<? extends AlgLicenseSpec> subEnum,
               final AlgLicenseSpec defaultAlg) {
        this.asymmetric = asymmetric;
        this.subEnum = subEnum;
        this.defaultAlg = defaultAlg;
    }

    /** 是否为非对称算法 */
    public boolean isAsymmetric() {
        return this.asymmetric;
    }

    public Class<? extends AlgLicenseSpec> type() {
        return this.subEnum;
    }

    public AlgLicenseSpec value() {
        return this.defaultAlg;
    }

    // ================= 公共接口 =================

    // ================= 子枚举定义 =================

}
