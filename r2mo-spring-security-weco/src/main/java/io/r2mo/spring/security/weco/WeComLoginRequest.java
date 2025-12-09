package io.r2mo.spring.security.weco;

import io.r2mo.jaas.auth.LoginRequest;
import io.r2mo.spring.security.exception._80241Exception400PasswordRequired;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @author lang : 2025-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WeComLoginRequest extends LoginRequest {
    /**
     * 临时授权码 (前端传递)
     */
    private String code;

    /**
     * 企业成员 UserID (后端换取后填充)
     */
    private String userId;

    // --- 构造函数 ---

    public WeComLoginRequest() {
    }

    public WeComLoginRequest(final JObject request) {
        this.setCode(request.getString("code"));
        // 构造完成后立即验证
        this.requestValidated();
    }

    // --- Setter 联动逻辑 ---

    public void setCode(final String code) {
        this.code = code;
        // Code 即临时凭证
        this.setCredential(code);
    }

    public void setUserId(final String userId) {
        this.userId = userId;
        // UserID 即用户身份标识
        this.setId(userId);
    }

    // --- 核心方法 ---

    @Override
    public TypeLogin type() {
        return TypeLogin.ID_WECOM;
    }

    public void requestValidated() {
        if (Objects.isNull(this.code)) {
            // 复用密码必填异常，提示 code 缺失
            throw new _80241Exception400PasswordRequired("code");
        }
    }
}