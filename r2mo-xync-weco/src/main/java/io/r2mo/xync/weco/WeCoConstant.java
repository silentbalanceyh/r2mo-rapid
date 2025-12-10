package io.r2mo.xync.weco;

/**
 * 微信/企微 认证动作常量
 *
 * @author lang : 2025-12-09
 */
public interface WeCoConstant {
    // ==========================================
    // 参数键定义 (Keys)
    // ==========================================

    // --- Header Keys ---
    String HEADER_REDIRECT_URI = "redirectUri";
    String HEADER_STATE = "state";
    /** 扫码会话 ID/参数的 Key (用于 APP_AUTH_QR) */
    String PARAM_UUID = "uuid";

}