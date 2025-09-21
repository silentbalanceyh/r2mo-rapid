package io.r2mo.jce.component.lic.domain;

import io.r2mo.jce.constant.LicFormat;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.crypto.SecretKey;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-19
 */
@Data
@Accessors(fluent = true)
@Builder
@ToString
public class LicenseFile implements LicenseID.Valid, LicenseID {
    @ToString.Exclude
    private byte[] data;            // 许可证数据
    @ToString.Exclude
    private byte[] signature;       // 许可证签名
    // ====================== 加密部分的核心数据
    @ToString.Exclude
    private byte[] encrypted;       // 加密后的许可证数据
    private LicFormat format;       // 许可证格式
    private SecretKey key;          // 对称加密的密钥（临时生成的）

    // ====================== 下边部分是格式化所需
    private String licenseId;       // 许可证ID
    private String name;            // 许可证名称
    private String code;            // 许可证编号
    private UUID id;                // 许可证系统ID -> uuid.lic / uuid.sig / uuid.key

    @Override
    public boolean isOk() {
        if (Objects.isNull(this.signature) || 0 == this.signature.length) {
            return true;
        }
        if (Objects.isNull(this.format)) {
            return true;
        }
        return Objects.isNull(this.licenseId);
    }

    public void loadMetadata(final LicenseFile securityFile) {
        this.id = securityFile.id;
        this.key = securityFile.key;
        this.signature = securityFile.signature;
    }
}
