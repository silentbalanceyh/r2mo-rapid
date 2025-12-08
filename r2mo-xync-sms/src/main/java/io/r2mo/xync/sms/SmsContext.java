package io.r2mo.xync.sms;

import io.r2mo.base.exchange.BaseContext;

/**
 * @author lang : 2025-12-08
 */
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

    public SmsContext setAccessId(final String accessId) {
        this.set(KEY_ACCESS_ID, accessId);
        return this;
    }

    public SmsContext setAccessSecret(final String accessSecret) {
        this.set(KEY_ACCESS_SECRET, accessSecret);
        return this;
    }

    public SmsContext setSignName(final String signName) {
        this.set(KEY_SIGN_NAME, signName);
        return this;
    }

    public SmsContext setTimeoutConnect(final int timeoutConnect) {
        this.set(KEY_TIMEOUT_CONNECT, timeoutConnect);
        return this;
    }

    public SmsContext setTimeoutRead(final int timeoutRead) {
        this.set(KEY_TIMEOUT_READ, timeoutRead);
        return this;
    }

    public SmsContext setRegion(final String region) {
        this.set(KEY_REGION, region);
        return this;
    }

    public SmsContext setHost(final String host) {
        this.set(KEY_HOST, host);
        return this;
    }
}
