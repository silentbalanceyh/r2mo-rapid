package io.r2mo.typed.domain.extension;

import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractStoreObject extends AbstractNormObject {
    /**
     * 专用存储对象的尺寸信息，其中顶层的 id 就表示 nodeId
     */
    private Long size;
    /**
     * 特殊属性信息，用于描述元数据
     */
    private JObject metadata;
    /**
     * 存储路径信息，当前内容的绝对路径
     */
    private String storePath;
}
