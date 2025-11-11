package io.r2mo.jaas.element;

import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用户组核心数据（组分类型，但没有管理组的说法）
 *
 * @author lang : 2025-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MSGroup extends AbstractNormObject implements Serializable {

    @Schema(description = "组名称")
    private String name;

    @Schema(description = "组编码")
    private String code;

    @Schema(description = "组类型")
    private String type;

    @Schema(description = "父组ID")
    private UUID parentId;

    @Schema(description = "组优先级")
    private Integer priority = 0;

    @Schema(description = "角色")
    @Accessors(chain = true, fluent = true)
    private List<MSRole> roles = new ArrayList<>();
}
