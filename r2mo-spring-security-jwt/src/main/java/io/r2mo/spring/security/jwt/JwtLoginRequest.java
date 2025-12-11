package io.r2mo.spring.security.jwt;

import io.r2mo.spring.security.basic.BasicLoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lang : 2025-11-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JwtLoginRequest extends BasicLoginRequest {

    public JwtLoginRequest() {
        super();
    }

    public JwtLoginRequest(final JObject requestJ) {
        super(requestJ);
    }

    /*
     * 只是单纯改变类型而已
     */
    @Override
    public TypeLogin type() {
        return TypeLogin.JWT;
    }
}
