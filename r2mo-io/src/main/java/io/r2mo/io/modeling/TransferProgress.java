package io.r2mo.io.modeling;

import io.r2mo.base.io.transfer.TransferType;
import io.r2mo.io.enums.TransferStatus;
import io.r2mo.typed.domain.extension.AbstractNormObject;
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
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TransferProgress extends AbstractNormObject {
    private String token;                   // 传输令牌
    private UUID nodeId;                    // 节点ID
    private Long bytesTransferred;          // 已传输字节数
    private Long bytesTotal;                // 总字节数
    private Double progress;                // 进度百分比

    private Boolean isMultipart;            // 是否分片传输
    private TransferStatus status;          // 传输状态
    private TransferType type;              // 传输类型
    private LocalDateTime startedAt;        // 开始时间
    private LocalDateTime finishedAt;       // 完成时间

    private String errorMessage;            // 错误信息
    private String errorReason;             // 错误原因
}
