package io.r2mo.io.modeling;

import io.r2mo.base.io.modeling.StoreDirectory;
import io.r2mo.base.io.modeling.StoreFile;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class TransferResponse extends AbstractNormObject {

    private String token;                           // 传输令牌
    private String url;                             // 传输URL
    private Long size;                              // 总大小

    private Boolean isMultipart;                    // 是否分片文件
    private StoreFile file;                         // 关联存储文件
    private Long countChunk;                        // 分片数量

    private StoreDirectory directory;               // 关联存储目录
    private Long countFile;                         // 文件数量
    /*
     * 注意：响应中没有包含 nodeId，实际是因为执行过程中的流程
     * 1）Request -> StoreNode + Token
     *    Token 中包含了 nodeId 信息
     * 2）StoreNode -> Response
     *    Response 中包含了 token -> nodeId -> 实际文件信息
     */
}
