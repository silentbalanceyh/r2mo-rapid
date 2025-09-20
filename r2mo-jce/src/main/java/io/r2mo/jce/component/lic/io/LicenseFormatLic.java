package io.r2mo.jce.component.lic.io;

import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.constant.LicTitle;
import org.bouncycastle.util.encoders.Base64;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * *.lic 文本格式解析器（分为 [HEAD] 与 [BODY] 区块）
 *
 * <pre>
 * 示例文件：
 *
 * [HEAD]
 * License-ID = LIC-1234567890ABCDEF
 * Name       = Enterprise-License
 * Code       = ABCD1234EFGH5678
 *
 * [BODY]
 * data       = Xj8dsakD98a...
 * </pre>
 *
 * - [HEAD]：元信息（id、name、code）
 * - [BODY]：主体数据（data，优先为加密数据）
 * - 不包含签名（签名单独存储为 *.sig）
 *
 * @author lang
 * @since 2025-09-20
 */
class LicenseFormatLic implements LicenseFormat {

    @Override
    public String format(final LicenseFile file) {
        final StringBuilder sb = new StringBuilder();

        // HEAD 区块
        sb.append("[HEAD]\n");
        sb.append(LicTitle.LICENSE_ID).append(" = ").append(file.licenseId()).append("\n");
        sb.append(LicTitle.NAME).append(" = ").append(file.name()).append("\n");
        sb.append(LicTitle.CODE).append(" = ").append(file.code()).append("\n\n");

        // BODY 区块
        sb.append("[BODY]\n");
        final byte[] content = (file.encrypted() != null) ? file.encrypted() : file.data();
        if (content != null) {
            sb.append("data = ").append(Base64.toBase64String(content)).append("\n");
        }

        return sb.toString();
    }

    @Override
    public LicenseFile parse(final String content) {
        final Map<String, String> map = new LinkedHashMap<>();
        final String[] lines = content.split("\\r?\\n");

        String section = null;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                section = line;
                continue;
            }

            final String[] kv = line.split("=", 2);
            if (kv.length == 2) {
                map.put(section + ":" + kv[0].trim(), kv[1].trim());
            }
        }

        return LicenseFile.builder()
            .licenseId(map.get("[HEAD]:" + LicTitle.LICENSE_ID))
            .name(map.get("[HEAD]:" + LicTitle.NAME))
            .code(map.get("[HEAD]:" + LicTitle.CODE))
            .encrypted(map.containsKey("[BODY]:data") ? Base64.decode(map.get("[BODY]:data")) : null)
            .build();
    }
}
