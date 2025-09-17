package io.r2mo.base.io.modeling;

import io.r2mo.base.io.enums.NodeType;
import io.r2mo.typed.domain.extension.AbstractStoreObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

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
    @Setter(AccessLevel.PROTECTED)
    private NodeType type;         // 节点类型：FILE/DIRECTORY
    private UUID parentId;         // 父节点ID（这样才可以构造一个树形结构）
    private UUID ownerId;          // 所有者ID
}
