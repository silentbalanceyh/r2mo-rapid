package io.r2mo.spring.security.weco;

import io.r2mo.spring.security.exception._80241Exception400PasswordRequired;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @author lang : 2025-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WeChatReqPreLogin extends WeChatReqAccount {
    /**
     * 临时授权码 (前端传递)
     */
    private String code;
    // --- 构造函数 ---

    public WeChatReqPreLogin() {
    }

    public WeChatReqPreLogin(final JObject request) {
        super(request);
        // 临时授权码
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

    // --- 核心方法 ---
    public void requestValidated() {
        if (Objects.isNull(this.code)) {
            // 复用密码必填异常，提示 code 缺失
            throw new _80241Exception400PasswordRequired("code");
        }
    }
}
