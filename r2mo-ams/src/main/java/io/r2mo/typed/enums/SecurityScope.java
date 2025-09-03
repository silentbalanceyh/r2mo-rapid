package io.r2mo.typed.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author lang : 2025-09-03
 */
@Schema(description = "安全范围")
public enum SecurityScope {

    @Schema(description = "用户")
    USER,

    @Schema(description = "角色")
    ROLE,

    @Schema(description = "用户组")
    GROUP,

    @Schema(description = "组织")
    ORGANIZATION,

    @Schema(description = "其他")
    OTHER,

    @Schema(description = "所有")
    ALL
}
