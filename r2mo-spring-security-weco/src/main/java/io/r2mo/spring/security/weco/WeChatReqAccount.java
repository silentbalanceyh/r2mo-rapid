package io.r2mo.spring.security.weco;

import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @author lang : 2025-12-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatReqAccount extends LoginRequest {

    /**
     * 用户唯一标识 (后端换取后填充)
     */
    private String openId;

    /**
     * 开放平台统一标识 (后端换取后填充，可选)
     */
    private String unionId;

    public WeChatReqAccount() {
    }

    public WeChatReqAccount(final JObject request) {
        this.unionId = R2MO.valueT(request, "unionId");
        this.openId = R2MO.valueT(request, "openId");
        if (Objects.isNull(this.unionId)) {
            // Union Id 优先
            this.setId(this.openId);
        } else {
            // Open Id 次之
            this.setId(this.unionId);
        }
    }

    public void setUnionId(final String unionId) {
        this.unionId = unionId;
        // OpenID 即用户身份标识
        this.setId(unionId);
    }

    @Override
    public TypeLogin type() {
        return TypeLogin.ID_WECHAT;
    }
}
