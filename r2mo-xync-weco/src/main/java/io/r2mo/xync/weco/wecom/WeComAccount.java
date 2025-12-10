package io.r2mo.xync.weco.wecom;

import io.r2mo.base.exchange.BaseAccount;
import io.r2mo.base.exchange.UniCredential;

import java.util.Objects;

/**
 * 企业微信发送方账号 (WeCom Account)
 *
 * @author lang : 2025-12-09
 */
public class WeComAccount extends BaseAccount {

    private final WeComCredential credential;

    private String signature;

    public WeComAccount(final WeComCredential credential) {
        // WeCom 使用 AgentID 作为实际发送者的标识
        super(String.valueOf(Objects.requireNonNull(credential).agentId()));
        this.credential = credential;
    }

    /**
     * 设置企微应用名称
     * e.g. "告警机器人"
     */
    public WeComAccount signature(final String signature) {
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