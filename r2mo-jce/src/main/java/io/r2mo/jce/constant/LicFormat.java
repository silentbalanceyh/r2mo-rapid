package io.r2mo.jce.constant;

import io.r2mo.base.io.common.FileHelper;

/**
 * License 文件存储格式（带默认扩展名）。
 *
 * <p>设计说明：
 * <pre>
 * - 每个格式都关联一个默认扩展名（如 JSON → .json）。
 * - 用于落地文件时生成标准文件名，或通过扩展名识别解析器。
 * - 不包含业务语义（如 Trial/Enterprise），业务区分交由 LicenseData 控制。
 * </pre>
 *
 * <p>示例：
 * <pre>
 * LicFormat.JSON.extension();  // ".json"
 * LicFormat.BIN.extension();   // ".bin"
 * </pre>
 *
 * @author lang
 * @since 2025-09-20
 */
public enum LicFormat {

    /**
     * TEXT 纯文本（Properties / INI / key=value 风格）
     *
     * <p>使用场景：轻量级应用、内嵌式配置文件、人类可读且手动编辑的许可。
     * <p>优点：简单直观，易于手工修改，依赖少。
     * <p>缺点：缺乏结构化，扩展性差，安全性弱。
     * <p>五维评分：安全性 2/5，性能 5/5，兼容性 4/5，使用率 3/5，扩展性 2/5
     * <p>综合评分：16/25 ★★★☆☆
     * <p>推荐度：🟧 一般（仅适合简单场景，不建议正式商用）
     */
    TEXT(".lic"),

    /**
     * JSON 格式
     *
     * <p>使用场景：Web 系统、跨平台传输、需要良好可读性与通用解析的许可。
     * <p>优点：结构化好，兼容性强，生态丰富。
     * <p>缺点：体积偏大，可读性带来安全风险（容易被篡改）。
     * <p>五维评分：安全性 3/5，性能 4/5，兼容性 5/5，使用率 5/5，扩展性 5/5
     * <p>综合评分：22/25 ★★★★☆
     * <p>推荐度：🟦 高推荐（适合大多数现代应用）
     */
    JSON(".json"),

    /**
     * XML 格式
     *
     * <p>使用场景：传统企业系统、需要 Schema 校验和命名空间的复杂许可结构。
     * <p>优点：结构化极强，支持标准验证（XSD/DTD），扩展性好。
     * <p>缺点：冗余大、解析性能低，开发体验一般。
     * <p>五维评分：安全性 3/5，性能 2/5，兼容性 4/5，使用率 3/5，扩展性 5/5
     * <p>综合评分：17/25 ★★★☆☆
     * <p>推荐度：🟨 中立（适合政府/传统系统，但现代应用逐渐被 JSON 替代）
     */
    XML(".xml"),

    /**
     * BIN 二进制压缩/封装格式
     *
     * <p>使用场景：需要高安全性和紧凑存储的场景，如离线授权、嵌入式设备。
     * <p>优点：不可读（安全性好），体积小，解析速度快。
     * <p>缺点：调试困难，跨语言支持不如 JSON 广泛。
     * <p>五维评分：安全性 5/5，性能 5/5，兼容性 3/5，使用率 3/5，扩展性 4/5
     * <p>综合评分：20/25 ★★★★☆
     * <p>推荐度：🟩 强推荐（适合需要防篡改的商用环境）
     */
    BIN(".bin"),

    /**
     * PROTOBUF（或类似的二进制序列化格式）
     *
     * <p>使用场景：跨语言分布式系统、大规模分发、高性能环境。
     * <p>优点：序列化效率高，跨平台强，体积小，支持版本演进。
     * <p>缺点：依赖 Protobuf 库，不如 JSON 直观。
     * <p>五维评分：安全性 4/5，性能 5/5，兼容性 4/5，使用率 3/5，扩展性 5/5
     * <p>综合评分：21/25 ★★★★☆
     * <p>推荐度：🟩 强推荐（高性能系统的理想选择）
     */
    PROTOBUF(".pb");

    /** 默认文件扩展名（带点） */
    private final String extension;

    LicFormat(final String extension) {
        this.extension = extension;
    }

    /**
     * 根据文件名解析对应的 License 格式
     *
     * @param filename 文件名（如 license.json, my.lic）
     *
     * @return 对应的 LicFormat，如果无法识别则返回 null
     */
    public static LicFormat format(final String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }

        String extension = FileHelper.fileExtension(filename);
        if (extension.isEmpty()) {
            return null;
        }

        // 确保扩展名以点开头
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }

        for (final LicFormat format : values()) {
            if (format.extension.equalsIgnoreCase(extension)) {
                return format;
            }
        }

        return null;
    }

    /** 返回默认扩展名（包含点，例如 ".json"） */
    public String extension() {
        return this.extension;
    }

    /** 基于 baseName 拼接标准文件名 */
    public String filename(final String baseName) {
        final String safe = (baseName == null || baseName.isEmpty()) ? "license" : baseName;
        return safe + this.extension;
    }
}
