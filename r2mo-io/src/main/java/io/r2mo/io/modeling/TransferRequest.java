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
    /*
     * 如果不请求中不包含传输令牌则先创建一个新的令牌，然后再执行后续的下载，如果包含了传输令牌则可以直接验证令牌来下载相关信息以
     * 确定请求的合法性，如此执行之后就不用考虑是直接传输还是间接传输
     * - 直接模式
     *   这种模式通常令牌信息已经包含在请求中了，所以可以直接调用服务来执行传输
     * - 间接模式
     *   这种模式的请求和令牌是分离的，所以需要先将令牌植入到请求中，然后再执行传输
     * 它们的切换：
     * 如果请求中忘记设置令牌则创建一个新的令牌，然后再执行传输
     */
    private String token;                  // 传输令牌
}
