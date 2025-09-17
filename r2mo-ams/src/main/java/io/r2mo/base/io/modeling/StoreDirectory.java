package io.r2mo.base.io.modeling;

import io.r2mo.base.io.enums.NodeType;
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
 *     - size
 *     - attributes
 *     - storePath
 *     节点信息
 *     - type
 *     - parentId
 *     - ownerId
 * </pre>
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreDirectory extends StoreNode {
    private String directoryName;           // 目录名称
    private Integer countFile;              // 文件数量
    private Integer countDirectory;         // 子目录数量

    public StoreDirectory() {
        this.setType(NodeType.DIRECTORY);
    }
}
