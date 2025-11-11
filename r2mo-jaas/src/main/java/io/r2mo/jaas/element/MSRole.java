package io.r2mo.jaas.element;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * 角色核心数据（角色不分类型）
 *
 * @author lang : 2025-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MSRole extends AbstractNormObject implements Serializable {

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "父角色ID")
    private UUID parentId;

    @Schema(description = "管理员角色")
    private boolean isAdmin = false;

    @Schema(description = "优先级")
    private Integer priority = 0;

    @JsonIgnore
    @Accessors(chain = true, fluent = true)
    private List<String> authorities = new ArrayList<>();

    private MSRole add(final String authority) {
        authorities.add(authority);
        return this;
    }

    private MSRole remove(final String authority) {
        authorities.remove(authority);
        return this;
    }
}
