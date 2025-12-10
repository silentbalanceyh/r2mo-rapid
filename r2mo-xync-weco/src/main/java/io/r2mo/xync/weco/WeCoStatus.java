package io.r2mo.xync.weco;

import lombok.Getter;

/**
 * 微信/企微 扫码认证会话的状态枚举
 * <p>用于 WeCoSessionStore 中存储的 UUID 状态值。</p>
 *
 * @author lang : 2025-12-10
 */
public enum WeCoStatus {

    /**
     * 初始状态：二维码已生成，等待用户扫描。
     */
    WAITING("等待扫描", 10),

    /**
     * 已扫码，但用户尚未在手机上点击“确认登录”。
     * <p>注意：微信服务号的 API 模式可能没有该状态，直接跳转到 SUCCESS。</p>
     */
    SCANNED("已扫码，待确认", 20),

    /**
     * 成功状态：用户已在手机上确认登录。
     * <p>此时，WeCoSessionStore 中应该存储实际的 OpenID，而不是这个状态枚举。</p>
     */
    SUCCESS("登录成功", 30),

    /**
     * 失败状态：会话过期或用户拒绝授权。
     */
    EXPIRED("会话过期或失败", 90),

    /**
     * 锁定状态：因安全或风控原因，暂时锁定该会话。
     */
    LOCKED("会话被锁定", 99);

    // ==========================================
    // 枚举属性定义
    // ==========================================
    @Getter
    private final String description;
    @Getter
    private final int code;

    WeCoStatus(final String description, final int code) {
        this.description = description;
        this.code = code;
    }

    // ------------------------------------------
    // 辅助方法
    // ------------------------------------------

    /**
     * 通过 code 获取状态枚举
     */
    public static WeCoStatus fromCode(final int code) {
        for (final WeCoStatus status : WeCoStatus.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid WeCoStatus code: " + code);
    }
}