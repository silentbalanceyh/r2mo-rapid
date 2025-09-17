package io.r2mo.base.io.transfer.token;

import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
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
 * 上传下载通用令牌
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransferToken extends AbstractNormObject {
    private UUID userId;                    // 用户ID
    private String token;                   // 唯一令牌
    private TransferType type;              // 传输类型（上传还是下载）
    private LocalDateTime expiredAt;        // 过期时间
    // ---- 代理信息
    private String clientAgent;             // 客户端代理
    private String clientIp;                // 客户端IP
    // ---- 相关判断
    private Boolean isMultipart;            // 是否分片
    private Boolean isDirectory;            // 是否目录
    // ---- 服务信息
    private String serviceProvider;         // 服务名称
    private String serviceConsumer;         // 调用方名称

    private JObject configuration;          // 扩展配置
    /**
     * 关联对象
     * <pre>
     *     - 目录：refType = DIRECTORY, refId = StoreNode.id
     *     - 文件：refType = FILE, refId = StoreNode.id
     *     - 分片：refType = CHUNK, refId = StoreChunk.id
     * </pre>
     */
    private Ref ref;                        // 关联对象
}
