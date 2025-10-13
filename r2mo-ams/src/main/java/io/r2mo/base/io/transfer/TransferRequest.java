package io.r2mo.base.io.transfer;

import io.r2mo.base.io.modeling.FileRange;
import io.r2mo.base.io.modeling.StoreChunk;
import io.r2mo.typed.domain.extension.AbstractNormObject;
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
    // ============== 基础属性
    private UUID userId;                    // 用户ID
    private String clientAgent;             // 客户端代理
    private String clientIp;                // 客户端IP

    // ============== 传输属性
    private TransferType type;              // 传输类型（上传还是下载）
    private Boolean isDirectory = false;            // 是否目录
    private Boolean isMultipart;            // 是否分片文件
    private Boolean isResume;               // 是否断点续传
    private FileRange range;                // 下载范围

    // ============== 资源定位
    private UUID nodeId;                    // 节点ID
    private String pathTarget;              // 目标路径（上传用）
    private String pathSource;              // 源路径（下载用）

    // ============== 数据信息
    // isMultipart = true 时，使用 content
    private StoreChunk chunkData;           // 分片数据
    private Long chunkCount;                // 分片总数
    private Long chunkSize;                 // 分片大小
    private Long totalSize;                 //文件总大小
    // isDirectory = true 时，使用 filePaths
    private List<String> filePaths;         // 目录中文件路径列表
    private Long fileSize;                  // 尺寸信息
    private String fileName;                // 文件名称

    // ============== 扩展信息
    private JObject parameters;             // 扩展参数
    private String token;

}
