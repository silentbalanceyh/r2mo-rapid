package io.r2mo.jce.component.lic.domain;

import io.r2mo.jce.constant.LicFormat;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author lang : 2025-09-19
 */
@Data
@Accessors(fluent = true)
@Builder
public class LicenseFile {
    private byte[] data;            // 许可证数据
    private byte[] signature;       // 许可证签名
    private byte[] encrypted;       // 加密后的许可证数据
    private LicFormat format;       // 许可证格式

    // ====================== 下边部分是格式化所需
    private String licenseId;       // 许可证ID
    private String name;            // 许可证名称
    private String code;            // 许可证编号

    public boolean isOk() {
        if (Objects.isNull(this.signature) || 0 == this.signature.length) {
            return false;
        }
        if (Objects.isNull(this.format)) {
            return false;
        }
        return Objects.nonNull(this.licenseId);
    }
}
