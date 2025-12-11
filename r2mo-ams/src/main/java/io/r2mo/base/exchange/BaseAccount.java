package io.r2mo.base.exchange;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * @author lang : 2025-12-05
 */
@Data
public abstract class BaseAccount implements UniAccount {

    /**
     * 业务ID，通常业务 id 通过 {@link UniCredential} 获得
     */
    @Setter(AccessLevel.NONE)
    private String id;
    /**
     * 发送者昵称 (Display Name)
     * e.g. "R2MO 官方客服"
     * <p>对应接口: getName() - 由 Lombok 自动生成</p>
     */
    private String name;
    /**
     * 发送者头像
     * <p>对应接口: getAvatar() - 由 Lombok 自动生成</p>
     */
    private String avatar;

    protected BaseAccount(final String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return this.id;
    }
}
