package io.r2mo.base.exchange;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统一网络代理配置对象
 * <p>
 * 独立对象设计，方便嵌套配置 (e.g. Spring ConfigurationProperties)
 * </p>
 *
 * @author lang : 2025-12-09
 */
@Data
@Accessors(chain = true)
public class NormProxy implements Serializable {

    /**
     * 代理主机 (IP 或域名)
     */
    private String host;

    /**
     * 代理端口
     */
    private Integer port;

    /**
     * 代理认证用户名 (可选)
     */
    private String username;

    /**
     * 代理认证密码 (可选)
     */
    private String password;
}