package io.r2mo.jaas.element;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.jaas.enums.TypeID;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MSUser extends AbstractNormObject implements Serializable {

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final ConcurrentMap<TypeID, String> idMap = new ConcurrentHashMap<>();

    @Schema(description = "账号名")
    private String username;

    @Schema(description = "密码")
    private String password;

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

    public String id(final TypeID typeID) {
        // 获取
        return this.idMap.getOrDefault(typeID, null);
    }

    public Set<String> idKeys() {
        final Set<String> keys = Arrays.stream(TypeID.values())
            .map(this::id)
            .collect(Collectors.toSet());
        keys.add(this.getMobile());
        keys.add(this.getEmail());
        keys.add(this.getUsername());
        return keys.stream().filter(StrUtil::isNotEmpty).collect(Collectors.toSet());
    }

    /**
     * 令牌常用数据
     *
     * @return 令牌常用数据
     */
    public Map<String, Object> token() {
        final Map<String, Object> tokenData = new TreeMap<>();
        tokenData.put("id", this.getId());
        tokenData.put("username", this.username);
        if (StrUtil.isEmpty(this.email)) {
            tokenData.put("email", this.email);
        }
        if (StrUtil.isEmpty(this.mobile)) {
            tokenData.put("mobile", this.mobile);
        }
        Arrays.stream(TypeID.values()).forEach(typeId -> {
            final Object value = this.id(typeId);
            if (Objects.nonNull(value)) {
                tokenData.put(typeId.name(), value);
            }
        });
        return tokenData;
    }

    public MSUser id(final TypeID typeID, final String id) {
        if (StrUtil.isEmpty(id)) {
            // 移除
            this.idMap.remove(typeID);
        } else {
            // 添加
            this.idMap.put(typeID, id);
        }
        return this;
    }
}
