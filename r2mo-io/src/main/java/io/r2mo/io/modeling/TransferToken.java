package io.r2mo.io.modeling;

import io.r2mo.io.enums.TransferType;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

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
    private String token;                   // 唯一令牌
    private String userId;                  // 用户ID
    private TransferType type;              // 传输类型（上传还是下载）
    private String clientAgent;             // 客户端代理
    private String clientIp;                // 客户端IP
    private LocalDateTime expiredAt;        // 过期时间

    private Boolean isMultipart;            // 是否分片
    private Boolean isDirectory;            // 是否目录

    private String serviceProvider;         // 服务名称
    private String serviceConsumer;         // 调用方名称
    private JObject configuration;          // 扩展配置
    // 为了可传输目录，此处使用 Ref 结构
    private Ref ref;                        // 关联对象
}
