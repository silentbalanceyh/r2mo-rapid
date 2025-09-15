package io.r2mo.io.modeling;

import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreRange;
import io.r2mo.io.enums.TransferType;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.r2mo.typed.exception.AbstractException;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 *     继承属性
 *     - id
 *     - appId
 *     - tenantId
 *     - createdAt
 *     - createdBy
 *     - updatedAt
 *     - updatedBy
 * </pre>
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransferRequest extends AbstractNormObject {
    private UUID nodeId;                    // 节点ID
    private UUID userId;                    // 用户ID
    private String clientAgent;             // 客户端代理
    private String clientIp;                // 客户端IP

    private String pathTarget;              // 目标路径（上传用）
    private String pathSource;              // 源路径（下载用）
    private StoreRange range;               // 传输范围
    private StoreChunk content;             // 分片信息（分片上传用）
    private TransferType type;              // 传输类型（上传还是下载）

    private Boolean isMultipart;            // 是否分片文件
    private Boolean isDirectory;            // 是否目录

    private Long fileSize;                  // 文件大小
    private String fileName;                // 文件名称
    private List<String> filePaths;         // 目录中文件路径列表

    private JObject parameters;             // 扩展参数
    private AbstractException error;
}
