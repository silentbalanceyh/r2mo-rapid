package io.r2mo.jaas.auth;

import io.r2mo.typed.enums.TypeID;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 登录 ID 的矩阵类型的标识符
 * <pre>
 *     1. 账号类型
 *        - {@link TypeID}
 *     2. 基础属性
 *        - id
 *        - username
 *        - email
 *        - mobile
 * </pre>
 * 几乎所有的第三方账号都会包含此对象，所以使用这种方式更容易让账号类型得到识别
 *
 * @author lang : 2025-11-13
 */
@Data
@Accessors(chain = true, fluent = true)
public class LoginID implements Serializable {
    public static String ID = "id";
    public static String USERNAME = "username";
    public static String EMAIL = "email";
    public static String MOBILE = "mobile";

    private String id;
    private String username;
    private String email;
    private String mobile;
    private TypeID type;

    public String key() {
        return this.id;
    }
}
