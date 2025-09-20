package io.r2mo.jce.component.lic.domain;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.io.HUri;
import io.r2mo.jce.constant.AlgLicenseSpec;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * 构造路径信息，这些路径包括
 * <pre>
 *     1. Public / Private 路径
 *     2. 当前 License 的路径
 *        *.lic  -> 发放给客户的 License
 *        *.sig  -> License 对应的签名文件
 *     3. *.pub / *.pem
 *        对应的公钥文件，此文件应该直接发给客户让客户可嵌入到自己的软件中，它的用途
 *        - 验证签名是否正常
 * </pre>
 * 路径说明
 * <pre>
 *     1. 公私钥路径直接依靠配置来指定（不同应用会不同）
 *     2. License 路径依靠 License ID 来指定，在根路径 ioContext 之下提供相关路径
 * </pre>
 *
 * @author lang : 2025-09-20
 */
@Data
@Accessors(fluent = true)
public class LicenseConfiguration implements LicenseOk {
    /**
     * 上下文根路径会在客户端进行计算，计算结果作为 License 的根路径，可执行相关后续流程做路径计算，
     * 计算流程依靠外层处理，而不是本身。
     */
    private String ioContext;           // 上下文路径
    // ------------ 签名相关信息
    private String ioPrivate;           // 私钥路径
    private String ioPublic;            // 公钥路径
    private AlgLicenseSpec algSign;     // 签名算法
    // ------------ 加密相关信息
    private String ioSecret;            // 对称密钥
    private AlgLicenseSpec algEnc;      // 加密算法
    private boolean encrypted;          // 是否加密
    // ------------ License ID
    private String licenseId;

    public String ioLicenseDirectory() {
        return HUri.UT.resolve(this.contextOfLic(), this.licenseId);
    }

    public LicenseConfiguration algEnc(final AlgLicenseSpec algEnc) {
        this.algEnc = algEnc;
        if (Objects.nonNull(this.algEnc)) {
            this.encrypted = true;
        }
        return this;
    }

    public String ioPrivate() {
        if (Objects.nonNull(this.ioPrivate)) {
            return HUri.UT.resolve(this.contextOfCert(), this.ioPrivate);
        }
        return this.ioPem("_private.pem", this.algSign);
    }

    public String ioPublic() {
        if (Objects.nonNull(this.ioPublic)) {
            return HUri.UT.resolve(this.contextOfCert(), this.ioPublic);
        }
        return this.ioPem("_public.pem", this.algSign);
    }

    public String ioSecret() {
        if (Objects.nonNull(this.ioSecret)) {
            return HUri.UT.resolve(this.contextOfCert(), this.ioSecret);
        }
        return this.ioPem("_secret.pem", this.algEnc);
    }

    private String ioPem(final String suffix, final AlgLicenseSpec spec) {
        final String generated = this.ioAlg(spec) + suffix;
        if (StrUtil.isEmpty(this.ioContext)) {
            return generated;
        }
        return HUri.UT.resolve(this.contextOfCert(), generated);
    }

    private String contextOfCert() {
        return HUri.UT.resolve(this.ioContext, "cert");
    }

    private String contextOfLic() {
        return HUri.UT.resolve(this.ioContext, "lic");
    }

    private String ioAlg(final AlgLicenseSpec spec) {
        if (Objects.isNull(spec)) {
            return "";
        }
        return spec.alg() + "_" + spec.length();
    }

    @Override
    public boolean isOk() {
        if (Objects.isNull(this.licenseId)) {
            return true;
        }
        return Objects.isNull(this.algSign);
    }

    @Override
    public String toString() {
        final StringBuilder content = new StringBuilder("[ LicenseConfiguration ]");
        content.append("\n  |- Context    : ").append(this.ioContext);
        content.append("\n  |- AlgSign    : ").append(this.ioAlg(this.algSign));
        content.append("\n  |- PrivateKey : ").append(this.ioPrivate());
        content.append("\n  |- PublicKey  : ").append(this.ioPublic());
        content.append("\n  |- Encrypted  : ").append(this.encrypted);
        if (this.encrypted) {
            content.append("\n  |- AlgEncrypt : ").append(this.ioAlg(this.algEnc));
            content.append("\n  |- SecretKey  : ").append(this.ioSecret());
        }
        if (StrUtil.isNotEmpty(this.licenseId)) {
            content.append("\n  |- LicenseDir : ").append(this.ioLicenseDirectory());
        }
        return content.toString();
    }
}
