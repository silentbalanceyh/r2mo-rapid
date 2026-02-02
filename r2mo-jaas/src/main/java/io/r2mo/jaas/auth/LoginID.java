package io.r2mo.jaas.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.r2mo.typed.enums.TypeID;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
@JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonIgnore
    @Accessors(chain = true, fluent = true)
    private ConcurrentMap<String, Object> attribute = new ConcurrentHashMap<>();

    /**
     * 🔥【关键修复】添加这个静态工厂方法
     * 作用：当 Jackson 遇到字符串类型的 Value（比如脏数据 "cn.hutool.json.JSONObject"）时，
     * 调用此方法。我们直接返回 null，让 Map 中存储 {KEY : null}，从而避免报错。
     */
    @JsonCreator
    public static LoginID fromString(final String value) {
        // 这里可以做个判断，如果是脏数据，直接返回 null
        return null;
    }

    public LoginID attribute(final String name, final Object value) {
        this.attribute.put(name, value);
        return this;
    }

    public Object attribute(final String name) {
        return this.attribute.get(name);
    }

    public String key() {
        return this.id;
    }
}
