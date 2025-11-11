package io.r2mo.jaas.element;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.jaas.enums.UserIDType;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MSUser extends AbstractNormObject implements Serializable {

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final ConcurrentMap<UserIDType, String> id = new ConcurrentHashMap<>();

    @Schema(description = "账号名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avator;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "角色")
    @Accessors(chain = true, fluent = true)
    private List<MSRole> roles = new ArrayList<>();

    @Schema(description = "用户组")
    @Accessors(chain = true, fluent = true)
    private List<MSGroup> groups = new ArrayList<>();

    @JsonIgnore
    @Accessors(chain = true, fluent = true)
    private ConcurrentMap<String, Object> extension = new ConcurrentHashMap<>();

    public MSUser extension(final String name, final Object value) {
        this.extension.put(name, value);
        return this;
    }

    public Object extension(final String name) {
        return this.extension.get(name);
    }

    // 子类类型相关信息
    public Class<?> getType() {
        return this.getClass();
    }

    public String id(final UserIDType idType) {
        // 获取
        return this.id.getOrDefault(idType, null);
    }

    public MSUser id(final UserIDType idType, final String id) {
        if (StrUtil.isEmpty(id)) {
            // 移除
            this.id.remove(idType);
        } else {
            // 添加
            this.id.put(idType, id);
        }
        return this;
    }
}
