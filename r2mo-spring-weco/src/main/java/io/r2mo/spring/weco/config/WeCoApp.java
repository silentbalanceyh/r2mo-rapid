package io.r2mo.spring.weco.config;

import io.r2mo.base.exchange.NormProxy;
import lombok.Data;

import java.io.Serializable;

/**
 * 基础应用配置基类（提取公共字段）
 *
 * @author lang : 2025-12-11
 */
@Data
public abstract class WeCoApp implements Serializable {
    protected String appId;
    private String secret;
    /** 独立代理（优先级高于全局代理） **/
    private NormProxy proxy;
}
