package io.r2mo.xync.sms;

import io.r2mo.base.exchange.BaseContext;

/**
 * @author lang : 2025-12-08
 */
@SuppressWarnings("all")
public class SmsContext extends BaseContext {

    public static final String KEY_ACCESS_ID = "access_id";
    public static final String KEY_ACCESS_SECRET = "access_secret";
    public static final String KEY_SIGN_NAME = "sign_name";

    public static final String KEY_TIMEOUT_CONNECT = "timeout_connect";
    public static final String KEY_TIMEOUT_READ = "timeout_read";

    private static final String KEY_PRODUCT = "product";
    public static final String KEY_REGION = "region";

    public SmsContext() {
        this.set(KEY_PRODUCT, "Dysmsapi");
        this.set(KEY_REGION, "cn-hangzhou");
        this.set(KEY_HOST, "dysmsapi.aliyuncs.com");
    }

    public SmsContext setTimeoutConnect(final int timeoutConnect) {
        this.set(KEY_TIMEOUT_CONNECT, timeoutConnect);
        return this;
    }

    public int getTimeoutRead() {
        return this.get(KEY_TIMEOUT_READ);
    }

    public SmsContext setTimeoutRead(final int timeoutRead) {
        this.set(KEY_TIMEOUT_READ, timeoutRead);
        return this;
    }

    public int getTimeoutConnect() {
        return this.get(KEY_TIMEOUT_CONNECT);
    }

    public SmsContext setRegion(final String region) {
        this.set(KEY_REGION, region);
        return this;
    }

    public String getRegion() {
        return this.get(KEY_REGION);
    }

    public String getProduct() {
        return this.get(KEY_PRODUCT);
    }

    public SmsContext setHost(final String host) {
        this.set(KEY_HOST, host);
        return this;
    }
}
