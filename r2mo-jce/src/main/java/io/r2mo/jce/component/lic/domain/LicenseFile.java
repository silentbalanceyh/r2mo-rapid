package io.r2mo.jce.component.lic.domain;

import io.r2mo.jce.constant.LicFormat;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

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
    private String licenseId;       // 许可证ID
}
