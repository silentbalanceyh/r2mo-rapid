package io.r2mo.jaas.element;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.jaas.auth.LoginID;
import io.r2mo.jaas.session.UserClaim;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.r2mo.typed.enums.TypeID;
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

    private static final Cc<String, UserClaim> CCT_TOKEN = Cc.openThread();
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private final ConcurrentMap<TypeID, LoginID> idMap = new ConcurrentHashMap<>();
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
        final LoginID loginId = this.idMap.getOrDefault(typeID, null);
        return Objects.nonNull(loginId) ? loginId.key() : null;
    }

    public Set<String> ids() {
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
     * <pre>
     *     - id: 用户唯一标识
     *     - username: 账号名
     *     可选：
     *     - email: 邮箱（如果有）
     *     - mobile: 手机号（如果有）
     *     - 其他 ID 标识
     *     如果扩展，则直接提取
     * </pre>
     *
     * @return 令牌常用数据
     */
    public Map<String, Object> token() {
        final Map<String, Object> tokenData = new TreeMap<>();
        tokenData.put(LoginID.ID, this.getId());
        tokenData.put(LoginID.USERNAME, this.username);

        final UserClaim claim = CCT_TOKEN.pick(() -> SPI.findOneOf(UserClaim.class));
        if (Objects.isNull(claim)) {
            // 上述是必须的
            if (StrUtil.isEmpty(this.email)) {
                tokenData.put(LoginID.EMAIL, this.email);
            }
            if (StrUtil.isEmpty(this.mobile)) {
                tokenData.put(LoginID.MOBILE, this.mobile);
            }
            Arrays.stream(TypeID.values()).forEach(typeId -> {
                final Object value = this.id(typeId);
                if (Objects.nonNull(value)) {
                    tokenData.put(typeId.name(), value);
                }
            });
        } else {
            final Map<String, Object> claimData = claim.token(this);
            if (Objects.nonNull(claimData)) {
                tokenData.putAll(claimData);
            }
        }
        return tokenData;
    }
}
