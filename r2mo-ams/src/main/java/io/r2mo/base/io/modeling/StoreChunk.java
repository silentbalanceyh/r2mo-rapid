package io.r2mo.base.io.modeling;

import io.r2mo.typed.domain.extension.AbstractStoreObject;
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
 *     存储单元
 *     - nodeId         （关联节点ID）
 *     - size
 *     - attributes
 *     - storePath
 * </pre>
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreChunk extends AbstractStoreObject {
    private Integer index;         // 分片索引
    private Long byteFrom;         // 起始字节
    private Long byteTo;           // 结束字节
    private String checksum;       // 校验码
    private Boolean done;          // 是否完成
}
