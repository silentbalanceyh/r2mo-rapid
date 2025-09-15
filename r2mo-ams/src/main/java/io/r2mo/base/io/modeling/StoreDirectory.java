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
public class StoreDirectory extends AbstractStoreObject {
    private Integer countFile;     // 文件数量
    private Integer countDir;      // 子目录数量
}
