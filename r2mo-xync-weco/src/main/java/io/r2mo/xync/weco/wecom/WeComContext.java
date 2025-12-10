package io.r2mo.xync.weco.wecom;

import io.r2mo.base.exchange.BaseContext;
import io.r2mo.base.exchange.NormProxy;

/**
 * 企业微信 (WeCom) 环境上下文
 *
 * @author lang : 2025-12-09
 */
@SuppressWarnings("all")
public class WeComContext extends BaseContext {

    // --- 1. Key Constants ---
    public static final String KEY_MAX_RETRY = "max_retry";
    public static final String KEY_PROXY = "proxy";
    public static final String KEY_HOST = "host";
    public static final String KEY_PROTOCOL = "protocol";
    public static final String KEY_SSL = "ssl";
    public static final String KEY_TIMEOUT = "timeout";

    // --- 2. Value Constants ---
    public static final String DEF_HOST = "qyapi.weixin.qq.com";
    public static final String DEF_PROTOCOL = "https";
    public static final boolean DEF_SSL = true;
    public static final int DEF_TIMEOUT = 5000;
    public static final int DEF_MAX_RETRY = 3;

    public WeComContext() {
        this.set(KEY_HOST, DEF_HOST);
        this.set(KEY_PROTOCOL, DEF_PROTOCOL);
        this.set(KEY_SSL, DEF_SSL);
        this.set(KEY_TIMEOUT, DEF_TIMEOUT);
        this.set(KEY_MAX_RETRY, DEF_MAX_RETRY);
    }

    // --- Fluent API ---

    public WeComContext setHost(final String host) {
        this.set(KEY_HOST, host);
        return this;
    }

    public String getHost() {
        return this.get(KEY_HOST);
    }

    public WeComContext setTimeout(final int timeout) {
        this.set(KEY_TIMEOUT, timeout);
        return this;
    }

    public int getTimeout() {
        return this.getOrDefault(KEY_TIMEOUT, DEF_TIMEOUT);
    }

    public WeComContext setMaxRetry(final int maxRetry) {
        this.set(KEY_MAX_RETRY, maxRetry);
        return this;
    }

    public int getMaxRetry() {
        return this.getOrDefault(KEY_MAX_RETRY, DEF_MAX_RETRY);
    }

    // --- 代理对象操作 ---

    public WeComContext setProxy(final NormProxy proxy) {
        this.set(KEY_PROXY, proxy);
        return this;
    }

    public NormProxy getProxy() {
        return this.get(KEY_PROXY);
    }
}