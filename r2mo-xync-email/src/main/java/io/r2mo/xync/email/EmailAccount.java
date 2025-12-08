package io.r2mo.xync.email;

import io.r2mo.base.exchange.BaseAccount;
import io.r2mo.base.exchange.UniCredential;

import java.util.Objects;

/**
 * @author lang : 2025-12-05
 */
public class EmailAccount extends BaseAccount {

    private final EmailCredential credential;

    /**
     * 发送地址 (From Address)
     * e.g. "support@r2mo.io"
     * <p>对应接口: signature() - 手动实现以提供默认逻辑</p>
     */
    private String signature;

    /**
     * 直接使用 {@link UniCredential} 构造
     *
     * @param credential 凭证
     */
    public EmailAccount(final EmailCredential credential) {
        super(Objects.requireNonNull(credential).username());
        this.credential = credential;
    }

    public EmailAccount signature(final String signature) {
        this.signature = signature;
        return this;
    }

    /**
     * 获取发送签名 (在邮件场景下即 From Address)
     * <p>
     * 智能回退逻辑：如果未手动设置 signature，则默认使用 credential 中的 username。
     * 这样在 username 和 from address 一致时（大多数情况），无需重复设置。
     * </p>
     */
    @Override
    public String signature() {
        if (Objects.isNull(this.signature) && Objects.nonNull(this.credential())) {
            return this.credential.username();
        }
        return this.signature;
    }

    @Override
    public UniCredential credential() {
        return this.credential;
    }
}
