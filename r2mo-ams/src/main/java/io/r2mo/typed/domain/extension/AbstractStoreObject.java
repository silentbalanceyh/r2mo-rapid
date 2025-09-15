package io.r2mo.typed.domain.extension;

import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * @author lang : 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractStoreObject extends AbstractNormObject {
    private UUID nodeId;
    private Long size;
    private JObject attributes;
    private String storePath;
}
