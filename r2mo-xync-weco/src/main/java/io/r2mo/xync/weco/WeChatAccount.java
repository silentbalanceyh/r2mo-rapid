package io.r2mo.xync.weco;

import io.r2mo.base.exchange.BaseAccount;
import io.r2mo.base.exchange.UniCredential;

import java.util.Objects;

/**
 * 微信公众号发送方账号 (WeChat Account)
 *
 * @author lang : 2025-12-09
 */
public class WeChatAccount extends BaseAccount {

    private final WeChatCredential credential;

    private String signature;

    public WeChatAccount(final WeChatCredential credential) {
        // AppID 是 WeChat 体系的唯一标识
        super(Objects.requireNonNull(credential).appId());
        this.credential = credential;
    }

    /**
     * 设置公众号名称
     * e.g. "R2MO服务号"
     */
    public WeChatAccount signature(final String signature) {
        this.signature = signature;
        this.setName(signature);
        return this;
    }

    @Override
    public String signature() {
        return this.signature;
    }

    @Override
    public UniCredential credential() {
        return this.credential;
    }
}