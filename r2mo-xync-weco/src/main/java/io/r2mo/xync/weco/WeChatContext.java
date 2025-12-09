package io.r2mo.xync.weco;

import io.r2mo.base.exchange.BaseContext;
import io.r2mo.base.exchange.NormProxy;

/**
 * 微信公众号 (WeChat MP) 环境上下文
 *
 * @author lang : 2025-12-09
 */
@SuppressWarnings("all")
public class WeChatContext extends BaseContext {

    // --- 1. Key Constants (配置键) ---
    public static final String KEY_LANG = "lang";
    public static final String KEY_MAX_RETRY = "max_retry";
    public static final String KEY_PROXY = "proxy"; // 存储 UniProxy 对象

    // --- 2. Value Constants (默认值) ---
    public static final String DEF_HOST = "api.weixin.qq.com";
    public static final String DEF_PROTOCOL = "https";
    public static final boolean DEF_SSL = true;
    public static final int DEF_TIMEOUT = 5000;
    public static final int DEF_MAX_RETRY = 3;
    public static final String DEF_LANG = "zh_CN";

    public WeChatContext() {
        // 使用常量初始化默认状态
        this.set(KEY_HOST, DEF_HOST);
        this.set(KEY_PROTOCOL, DEF_PROTOCOL);
        this.set(KEY_SSL, DEF_SSL);
        this.set(KEY_TIMEOUT, DEF_TIMEOUT);
        this.set(KEY_MAX_RETRY, DEF_MAX_RETRY);
        this.set(KEY_LANG, DEF_LANG);
    }

    // --- Fluent API ---

    public WeChatContext setHost(final String host) {
        this.set(KEY_HOST, host);
        return this;
    }

    public WeChatContext setTimeout(final int timeout) {
        this.set(KEY_TIMEOUT, timeout);
        return this;
    }

    public WeChatContext setLang(final String lang) {
        this.set(KEY_LANG, lang);
        return this;
    }

    public String getLang() {
        return this.get(KEY_LANG);
    }

    public WeChatContext setMaxRetry(final int maxRetry) {
        this.set(KEY_MAX_RETRY, maxRetry);
        return this;
    }

    public int getMaxRetry() {
        return this.getOrDefault(KEY_MAX_RETRY, DEF_MAX_RETRY);
    }

    // --- 代理对象操作 ---

    /**
     * 设置代理对象
     *
     * @param proxy 独立的代理配置实体
     */
    public WeChatContext setProxy(final NormProxy proxy) {
        this.set(KEY_PROXY, proxy);
        return this;
    }

    /**
     * 获取代理对象
     *
     * @return 可能为 null
     */
    public NormProxy getProxy() {
        return this.get(KEY_PROXY);
    }
}