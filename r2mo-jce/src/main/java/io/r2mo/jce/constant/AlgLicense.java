package io.r2mo.jce.constant;

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
    ED25519(true, LicAsym.AlgLicenseModern.class, LicAsym.AlgLicenseModern.ED25519),

    // ===== 国密 SM 系列 =====
    SM2(true, LicAsym.AlgLicenseSm2.class, LicAsym.AlgLicenseSm2.SM2),

    // （对称加密系列）
    // ===== AES 系列（对称）=====
    AES(false, LicSym.AlgLicenseAes.class, LicSym.AlgLicenseAes.AES_256),

    // ===== ChaCha20 系列（对称）=====
    CHACHA20(false, LicSym.AlgLicenseChaCha.class, LicSym.AlgLicenseChaCha.CHACHA20),

    // ===== 国密 SM4 系列（对称）=====
    SM4(false, LicSym.AlgLicenseSm4.class, LicSym.AlgLicenseSm4.SM4_DEFAULT);

    /** 是否非对称 */
    private final boolean asymmetric;

    /** 子枚举类型 */
    private final Class<? extends AlgLicenseSpec> enumCls;

    /** 默认算法 */
    private final AlgLicenseSpec spec;

    AlgLicense(final boolean asymmetric,
               final Class<? extends AlgLicenseSpec> enumCls,
               final AlgLicenseSpec spec) {
        this.asymmetric = asymmetric;
        this.enumCls = enumCls;
        this.spec = spec;
    }

    /** 是否为非对称算法 */
    public boolean isAsymmetric() {
        return this.asymmetric;
    }

    public Class<? extends AlgLicenseSpec> type() {
        return this.enumCls;
    }

    public AlgLicenseSpec value() {
        return this.spec;
    }

    public String identifier() {
        return (this.asymmetric ? "ASYM@" : "SYM@") + this.name() + "/" + this.spec.alg();
    }

    // ================= 公共接口 =================

    // ================= 子枚举定义 =================

}
