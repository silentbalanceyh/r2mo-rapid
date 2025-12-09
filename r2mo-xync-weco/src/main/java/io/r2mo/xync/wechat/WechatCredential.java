package io.r2mo.xync.wechat;

import io.r2mo.base.exchange.UniCredential;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 微信公众号 (Official Account) 凭证
 *
 * @author lang : 2025-12-09
 */
@Data
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode(callSuper = false)
public class WechatCredential implements UniCredential {
    /**
     * 微信后台的 AppID
     */
    private String appId;

    /**
     * 微信后台的 AppSecret
     */
    private String secret;

    /**
     * (可选) 消息加解密密钥，仅在处理回调时需要
     */
    private String aesKey;

    /**
     * (可选) 令牌，仅在处理回调时需要
     */
    private String token;
}