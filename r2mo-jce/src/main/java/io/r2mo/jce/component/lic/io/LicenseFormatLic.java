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
 * LicenseID  = LIC-1234567890ABCDEF
 * Name       = Enterprise-License
 * Code       = ABCD1234EFGH5678
 *
 * [BODY]
 * Xj8dsakD98a...
 * AbcdEfghIjkl...
 * </pre>
 *
 * - [HEAD]：元信息（id、name、code）
 * - [BODY]：主体数据（优先为加密数据）
 * - 不包含签名（签名单独存储为 *.sig）
 *
 * @author lang
 * @since 2025-09-20
 */
class LicenseFormatLic implements LicenseFormat {

    private static final String HEAD_SECTION = "[HEAD]";
    private static final String BODY_SECTION = "[BODY]";
    private static final Integer MAX_LINE_LENGTH = 80;

    @Override
    public String format(final LicenseFile file) {
        final StringBuilder sb = new StringBuilder();

        // HEAD 区块
        sb.append(HEAD_SECTION).append("\n");

        // 定义 key-value 映射
        final Map<String, String> headMap = new LinkedHashMap<>();
        headMap.put(LicTitle.LICENSE_ID, file.licenseId());
        headMap.put(LicTitle.NAME, file.name());
        headMap.put(LicTitle.CODE, file.code());

        // 计算最大 key 长度
        final int maxKeyLen = headMap.keySet().stream()
            .mapToInt(String::length)
            .max()
            .orElse(0);

        // 按对齐规则写入
        headMap.forEach((k, v) -> {
            sb.append(String.format("%-" + maxKeyLen + "s = %s%n", k, v));
        });

        sb.append("\n");

        // BODY 区块
        sb.append(BODY_SECTION).append("\n");
        final byte[] content = (file.encrypted() != null) ? file.encrypted() : file.data();
        if (content != null) {
            final String base64 = Base64.toBase64String(content);
            sb.append(this.wrapBase64(base64)).append("\n");
        }

        return sb.toString();
    }


    @Override
    public LicenseFile parse(final String content, final boolean encrypted) {
        final ParsedContent parsed = this.parseContent(content);

        final LicenseFile.LicenseFileBuilder builder = LicenseFile.builder()
            .licenseId(parsed.head.get(HEAD_SECTION + ":" + LicTitle.LICENSE_ID))
            .name(parsed.head.get(HEAD_SECTION + ":" + LicTitle.NAME))
            .code(parsed.head.get(HEAD_SECTION + ":" + LicTitle.CODE));

        if (encrypted) {
            builder.encrypted(parsed.body);
        } else {
            builder.data(parsed.body);
        }
        return builder.build();
    }

    /**
     * Base64 长文本换行
     */
    private String wrapBase64(final String base64) {
        final StringBuilder wrapped = new StringBuilder();
        for (int i = 0; i < base64.length(); i += MAX_LINE_LENGTH) {
            final int end = Math.min(i + MAX_LINE_LENGTH, base64.length());
            wrapped.append(base64, i, end).append("\n");
        }
        return wrapped.toString().trim();
    }

    /**
     * 公共解析逻辑
     */
    private ParsedContent parseContent(final String content) {
        final Map<String, String> map = new LinkedHashMap<>();
        final String[] lines = content.split("\\r?\\n");

        String section = null;
        final StringBuilder dataBuf = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                section = line;
                continue;
            }

            if (HEAD_SECTION.equals(section)) {
                final String[] kv = line.split("=", 2);
                if (kv.length == 2) {
                    map.put(HEAD_SECTION + ":" + kv[0].trim(), kv[1].trim());
                }
            } else if (BODY_SECTION.equals(section)) {
                if (line.matches("^[A-Za-z0-9+/=]+$")) {
                    dataBuf.append(line);
                }
            }
        }

        final byte[] body = !dataBuf.isEmpty() ? Base64.decode(dataBuf.toString()) : new byte[0];
        return new ParsedContent(map, body);
    }

    /**
     * 内部解析结果：包含 HEAD 属性 和 BODY 数据
     */
    private static class ParsedContent {
        final Map<String, String> head;
        final byte[] body;

        ParsedContent(final Map<String, String> head, final byte[] body) {
            this.head = head;
            this.body = body;
        }
    }
}
