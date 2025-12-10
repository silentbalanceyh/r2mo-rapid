package io.r2mo.xync.weco.wecom;

import io.r2mo.base.exchange.UniCredential;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 企业微信 (WeCom / Work WeChat) 凭证
 *
 * @author lang : 2025-12-09
 */
@Data
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode(callSuper = false)
public class WeComCredential implements UniCredential {
    /**
     * 企业 ID (CorpID)
     * 例如: ww1234567890abcdef
     */
    private String corpId;

    /**
     * 应用密钥 (Secret)
     * 注意：企微的 Secret 是跟应用绑定的
     */
    private String secret;

    /**
     * 应用 ID (AgentId)
     * 发送消息时必须指定是哪个应用发的
     */
    private Integer agentId;
}