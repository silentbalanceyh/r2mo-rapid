package io.r2mo.spring.weco;

import io.r2mo.base.exchange.NormProxy;
import io.r2mo.xync.weco.wechat.WeChatCredential;
import io.r2mo.xync.weco.wecom.WeComCredential;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * 微信体系统一配置映射
 * <pre>
 *   weco:
 *     # ==========================================
 *     # 1. 全局网络代理 (可选)
 *     # ==========================================
 *     proxy:
 *       host:
 *       port:
 *       username:
 *       password:
 *
 *     # ==========================================
 *     # 2. 微信公众号 (WeChat Official Account)
 *     # ==========================================
 *     wechat:
 *       app-id:
 *       secret:
 *       token:      可选
 *       aes-key:    可选
 *       proxy:      # 可选，独立代理配置，优先级高于全局 proxy
 *
 *     # ==========================================
 *     # 3. 企业微信 (WeCom / Work WeChat)
 *     # ==========================================
 *     wecom:
 *       corp-id:
 *       secret:
 *       agent-id:
 *       proxy:      # 可选，独立代理配置，优先级高于全局 proxy
 * </pre>
 *
 * @author lang : 2025-12-09
 */
@Configuration
@ConfigurationProperties(prefix = "weco")
@Data
public class WeCoConfig implements Serializable {

    /**
     * 全局代理配置 (可选)
     * 如果子模块（wechat/wecom）未配置独立代理，则默认使用此配置
     */
    private NormProxy proxy;

    /**
     * 微信公众号配置域
     */
    private WeChat wechat;

    /**
     * 企业微信配置域
     */
    private WeCom wecom;


    // --- 内部静态配置类 ---

    @Data
    public static class WeChat implements Serializable {
        private String appId;
        private String secret;
        private String token;
        private String aesKey;
        private Integer expireSeconds = 300;

        /**
         * 独立代理 (优先级高于全局 proxy)
         */
        private NormProxy proxy;

        /**
         * 快捷转换为底层凭证对象
         */
        public WeChatCredential credential() {
            return new WeChatCredential()
                .appId(this.appId)
                .secret(this.secret)
                .token(this.token)
                .aesKey(this.aesKey);
        }
    }

    @Data
    public static class WeCom implements Serializable {
        private String corpId;
        private String secret;
        private Integer agentId;
        private Integer expireSeconds = 300;

        /**
         * 独立代理 (优先级高于全局 proxy)
         */
        private NormProxy proxy;

        /**
         * 快捷转换为底层凭证对象
         */
        public WeComCredential credential() {
            return new WeComCredential()
                .corpId(this.corpId)
                .secret(this.secret)
                .agentId(this.agentId);
        }
    }
}