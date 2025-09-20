package io.r2mo.jce.component.lic.domain;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.io.HUri;
import io.r2mo.jce.constant.AlgLicenseSpec;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
public class LicenseLocation implements Serializable {
    /**
     * 上下文根路径会在客户端进行计算，计算结果作为 License 的根路径，可执行相关后续流程做路径计算，
     * 计算流程依靠外层处理，而不是本身。
     */
    private String ioContext;           // 上下文路径

    private String ioPrivate;           // 私钥路径
    private String ioPublic;            // 公钥路径
    private AlgLicenseSpec algorithm;   // License算法
    // License ID
    private String licenseId;

    public String ioLicenseDirectory() {
        return HUri.UT.resolve(this.ioContext, this.licenseId);
    }

    public String ioPrivate() {
        return this.ioKeyPair("_private.pem");
    }

    public String ioPublic() {
        return this.ioKeyPair("_public.pem");
    }

    private String ioKeyPair(final String suffix) {
        final String privateFile = this.ioAlgorithm() + suffix;
        if (StrUtil.isEmpty(this.ioContext)) {
            return privateFile;
        }
        if (Objects.isNull(this.ioPrivate)) {
            return HUri.UT.resolve(this.ioContext, privateFile);
        } else {
            final String privatePath = HUri.UT.resolve(this.ioPrivate, privateFile);
            return HUri.UT.resolve(this.ioContext, privatePath);
        }
    }

    public String ioAlgorithm() {
        if (Objects.isNull(this.algorithm)) {
            return "";
        }
        return this.algorithm.alg() + "_" + this.algorithm.length();
    }
}
