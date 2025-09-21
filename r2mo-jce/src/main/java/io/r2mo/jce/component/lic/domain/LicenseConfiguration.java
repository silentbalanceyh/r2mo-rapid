package io.r2mo.jce.component.lic.domain;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.io.HUri;
import io.r2mo.jce.constant.AlgLicenseSpec;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * LicenseConfiguration
 * 用于描述 License 的配置，包括路径计算规则、签名/加密算法、以及 License ID 的定位。
 * 核心职责：
 * <pre>
 * 1. 提供公钥、私钥文件的路径计算规则（证书目录）
 * 2. 提供 License 文件路径计算规则（license 目录）
 * 3. 记录签名与加密算法的配置，便于后续校验与解密
 * 4. 提供简化的 toString() 输出，方便调试与日志打印
 * </pre>
 *
 * 目录规范：
 * <pre>
 * - {@link LicenseConfiguration#ioContext}/cert : 存放公私钥文件（*.pem）
 * - {@link LicenseConfiguration#ioContext}/lic  : 存放 License 文件（*.lic, *.sig, *.key）
 * </pre>
 *
 * @author lang
 * @since 2025-09-20
 */
@Data
@Accessors(fluent = true)
public class LicenseConfiguration implements LicenseOk {
    /**
     * 上下文根路径 🔴
     * - 必须：所有路径的计算基准目录
     * - 设计：由外层环境注入（客户端/服务端不同）
     * - 通用性：适用于任何需要组织证书与许可文件的场景
     */
    private String ioContext;

    // ------------ 签名相关信息 ------------

    /**
     * 私钥路径 🔵
     * - 可选：如未指定，则自动根据签名算法生成默认路径
     * - 设计：指向 cert 目录下的 *_private.pem
     * - 通用性：仅服务端需要，客户端通常不会持有
     */
    private String ioPrivate;

    /**
     * 公钥路径 🔵
     * - 可选：如未指定，则自动根据签名算法生成默认路径
     * - 设计：指向 cert 目录下的 *_public.pem
     * - 通用性：客户端使用，用于验签
     */
    private String ioPublic;

    /**
     * 签名算法 🔴
     * - 必须：决定私钥/公钥生成与验证的方式
     * - 通用性：RSA / ECDSA 等签名机制，所有许可系统必备
     */
    private AlgLicenseSpec algSign;

    // ------------ 加密相关信息 ------------

    /**
     * 加密算法 🔵
     * - 可选：用于决定加密 License 内容时采用的对称算法
     * - 设计：如 AES-256，配合密钥文件一起下发
     * - 通用性：适用于需要保护 License 明文的系统
     */
    private AlgLicenseSpec algEnc;

    /**
     * 是否加密 🔵
     * - 可选：标识 License 是否经过加密
     * - 设计：当 algEnc != null 时自动置为 true
     * - 通用性：防止 License 内容被直接篡改或伪造
     */
    private boolean encrypted;

    // ------------ 工具方法 ------------

    /**
     * License 文件目录
     *
     * @return ioContext/lic/{licenseId}
     */
    public String contextLicense() {
        return HUri.UT.resolve(this.ioContext, "lic");
    }

    public LicenseConfiguration algEnc(final AlgLicenseSpec algEnc) {
        this.algEnc = algEnc;
        if (Objects.nonNull(this.algEnc)) {
            this.encrypted = true;
        }
        return this;
    }

    /**
     * 计算私钥文件路径
     *
     * @return ioContext/cert/{ioPrivate 或者 默认生成名}
     */
    public String ioPrivate() {
        if (Objects.nonNull(this.ioPrivate)) {
            return HUri.UT.resolve(this.contextCert(), this.ioPrivate);
        }
        return this.ioPem("_private.pem", this.algSign);
    }

    /**
     * 计算公钥文件路径
     *
     * @return ioContext/cert/{ioPublic 或者 默认生成名}
     */
    public String ioPublic() {
        if (Objects.nonNull(this.ioPublic)) {
            return HUri.UT.resolve(this.contextCert(), this.ioPublic);
        }
        return this.ioPem("_public.pem", this.algSign);
    }

    private String ioPem(final String suffix, final AlgLicenseSpec spec) {
        final String generated = this.ioAlg(spec) + suffix;
        if (StrUtil.isEmpty(this.ioContext)) {
            return generated;
        }
        return HUri.UT.resolve(this.contextCert(), generated);
    }

    private String contextCert() {
        return HUri.UT.resolve(this.ioContext, "cert");
    }

    private String ioAlg(final AlgLicenseSpec spec) {
        if (Objects.isNull(spec)) {
            return "";
        }
        return spec.alg() + "_" + spec.length();
    }

    @Override
    public boolean isOk() {
        return Objects.isNull(this.algSign);
    }

    @Override
    public String toString() {
        return "[ LicenseConfiguration ]" + "\n  |- Context    : " + this.ioContext +
            "\n  |- AlgSign    : " + this.ioAlg(this.algSign) +
            "\n  |- PrivateKey : " + this.ioPrivate() +
            "\n  |- PublicKey  : " + this.ioPublic() +
            "\n  |- Encrypted  : " + this.encrypted +
            "\n  |- AlgEncrypt : " + this.ioAlg(this.algEnc);
    }
}
