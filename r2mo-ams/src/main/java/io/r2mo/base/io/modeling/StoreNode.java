package io.r2mo.base.io.modeling;

import io.r2mo.base.io.enums.NodeType;
import io.r2mo.typed.domain.extension.AbstractStoreObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
 *     存储单元
 *     - nodeId         （父节点ID）
 *     - size
 *     - attributes
 *     - storePath
 * </pre>
 *
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StoreNode extends AbstractStoreObject {
    private String name;           // 节点名称
    private NodeType type;         // 节点类型：FILE/DIRECTORY
    private UUID ownerId;          // 所有者ID
}
