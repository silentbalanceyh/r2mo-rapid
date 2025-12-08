package io.r2mo.xync.email;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.typed.domain.BaseConfig;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 配置信息，通常只负责配置
 * <pre>
 *     host:
 *     port:
 *     ssl:
 *     # 账号信息
 *     username:
 *     password:
 * </pre>
 *
 * @author lang : 2025-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EmailDomain extends BaseConfig implements Serializable {
    private String host;
    private int port;
    private boolean ssl;

    @JsonIgnore
    @Accessors(chain = true, fluent = true)
    private final EmailCredential credential = new EmailCredential();
    /**
     * 协议类型
     */
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private final EmailProtocol protocol;

    public EmailDomain(final EmailProtocol protocol) {
        this.protocol = protocol;
    }
    // --- 核心修改：增加绑定逻辑 ---

    /**
     * 将 Map 属性绑定到当前对象
     * 1. 识别标准字段 (host, port...)
     * 2. 不识别的字段扔进 Extension (BaseConfig)
     */
    public void bind(final Map<String, Object> props) {
        if (MapUtil.isEmpty(props)) {
            return;
        }

        props.forEach((key, value) -> {
            switch (key) {
                case "host":
                    this.setHost(Convert.toStr(value));
                    break;
                // 默认端口逻辑交给外部检查，这里默认为0
                case "port":
                    this.setPort(Convert.toInt(value, 0));
                    break;
                case "ssl":
                    this.setSsl(Convert.toBool(value, false));
                    break;
                case "username":
                    this.setUsername(Convert.toStr(value));
                    break;
                case "password":
                    this.setPassword(Convert.toStr(value));
                    break;
                // 关键：扁平化处理，不认识的 Key 全部作为扩展属性
                default:
                    this.putExtension(key, value);
                    break;
            }
        });
    }
    // --- 关键：手动桥接 YAML 的扁平属性到 credential 对象 ---

    public void setUsername(final String username) {
        this.credential.username(username);
    }

    public void setPassword(final String password) {
        this.credential.password(password);
    }

    public String getUsername() {
        return this.credential.username();
    }

    // 禁止序列化出去（防止日志打印或 API 返回泄露密码）
    // 或者仅仅为了内部使用，不加注解也行，但要小心 ObjectMapper
    @JsonIgnore
    public String getPassword() {
        return this.credential.password();
    }
}
