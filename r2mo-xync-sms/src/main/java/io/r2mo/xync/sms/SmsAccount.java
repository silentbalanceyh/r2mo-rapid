package io.r2mo.xync.sms;

import io.r2mo.base.exchange.BaseAccount;
import io.r2mo.base.exchange.UniCredential;

import java.util.Objects;

/**
 * @author lang : 2025-12-08
 */
public class SmsAccount extends BaseAccount {

    private final SmsCredential credential;

    private String signature;

    public SmsAccount(final SmsCredential credential) {
        super(Objects.requireNonNull(credential).mobile());
        this.credential = credential;
    }

    @SuppressWarnings("all")
    public SmsAccount signature(final String signature) {
        this.signature = signature;
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
