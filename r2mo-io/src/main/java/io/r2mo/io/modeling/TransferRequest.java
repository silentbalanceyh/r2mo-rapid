package io.r2mo.io.modeling;

import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.base.io.modeling.StoreRange;
import io.r2mo.base.io.transfer.TransferType;
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
    // 共享属性
    private UUID userId;                    // 用户ID
    private String clientAgent;             // 客户端代理
    private String clientIp;                // 客户端IP
    private TransferType type;              // 传输类型（上传还是下载）

    // 上传专用
    private String pathTarget;              // 目标路径（上传用）
    private StoreChunk content;             // 分片信息（分片上传用）

    // 下载专用
    private String pathSource;              // 源路径（下载用）
    private StoreRange range;               // 传输范围

    // 目录专用
    private Boolean isDirectory;            // 是否目录
    private UUID nodeId;                    // 节点ID
    private List<String> filePaths;         // 目录中文件路径列表

    // 文件专用
    private Boolean isMultipart;            // 是否分片文件
    private Long fileSize;                  // 文件大小
    private String fileName;                // 文件名称

    private JObject parameters;             // 扩展参数
    private AbstractException error;
}
