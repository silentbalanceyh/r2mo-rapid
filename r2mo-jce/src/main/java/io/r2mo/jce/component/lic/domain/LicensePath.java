package io.r2mo.jce.component.lic.domain;

import cn.hutool.core.util.StrUtil;
import io.r2mo.jce.constant.LicFormat;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-20
 */
@Data
@Accessors(fluent = true)
@Builder
public class LicensePath implements LicenseID.Valid, LicenseID {
    private String fileLicense;
    private String fileSign;
    private String fileKey;
    private LicFormat format;       // 许可证格式
    private String licenseId;
    private UUID id;

    @Override
    public boolean isOk() {
        return Objects.isNull(this.format)
            || Objects.isNull(this.id)
            || !StrUtil.isNotEmpty(this.fileSign);
    }
}
