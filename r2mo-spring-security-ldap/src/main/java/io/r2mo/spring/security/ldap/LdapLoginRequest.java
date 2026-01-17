package io.r2mo.spring.security.ldap;

import io.r2mo.spring.security.basic.BasicLoginRequest;
import io.r2mo.typed.enums.TypeLogin;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 复用 BasicLoginRequest 作为 LDAP 登录请求，只需要设置 username 和 password 即可，同时也可以支持图片验证码的
 * 功能，其中对应关系如
 * <pre>
 *     输入：
 *     username：email地址
 *     password：密码
 *     字段详情
 *     uid: 用户id标识
 *     email：用户的邮箱 mail
 *     username: uid
 * </pre>
 *
 * @author lang : 2025-12-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LdapLoginRequest extends BasicLoginRequest {
    private String uid;

    public LdapLoginRequest(final JObject params) {
        super(params);
    }

    @Override
    public TypeLogin type() {
        return TypeLogin.LDAP;
    }
}
