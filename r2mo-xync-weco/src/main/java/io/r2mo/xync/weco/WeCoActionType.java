package io.r2mo.xync.weco;

import lombok.Getter;

/**
 * 微信/企微 认证动作的枚举定义
 * <pre>
 *     用于 WeCoAction 的分发和识别，前缀区分发起方向：
 *     - WX_: 依赖微信内部环境（H5页面跳转）
 *     - APP_: 依赖应用外部环境（PC端扫码轮询，API模式）
 * </pre>
 */
public enum WeCoActionType {

    // ==========================================
    // WX_ 前缀：微信环境内部打开 (H5 页面跳转流程)
    // ==========================================

    /**
     * 动作：获取 OAuth2 授权 URL
     * <br>场景：前端重定向到微信域，适用于 H5 页面内或 PC 端跳转扫码。
     */
    WX_AUTH_URL("获取微信授权跳转 URL"),

    /**
     * 动作：使用 Code 换取 OpenID 和用户信息
     * <br>场景：微信回调后，前端提交 Code 给后端。
     */
    WX_LOGIN_BY("使用Code执行微信登录"),

    // ==========================================
    // APP_ 前缀：应用外部环境 (PC 端轮询/API 模式)
    // ==========================================

    /**
     * 动作：获取带参二维码 (PC 端展示用)
     * <br>实现：生成 UUID，调微信 API 换图片 URL，初始化缓存状态。
     */
    APP_AUTH_QR("获取扫码登录二维码"),

    /**
     * 动作：检查扫码状态 (PC 端轮询用)
     * <br>实现：检查缓存中 UUID 是否已关联 OpenID。
     */
    APP_STATUS("检查扫码状态");

    // ==========================================
    // 枚举属性定义
    // ==========================================

    @Getter
    private final String description;

    WeCoActionType(final String description) {
        this.description = description;
    }
}