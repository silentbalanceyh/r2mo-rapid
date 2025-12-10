package io.r2mo.spring.security.weco;

import io.r2mo.typed.json.JObject;

/**
 * 微信公众号 (WeChat Official Account) 业务服务
 * <p>
 * 职责：
 * 1. 管理公众号的 OAuth2 授权流程
 * 2. 校验回调 Code 并提取用户身份
 * </p>
 *
 * @author lang : 2025-12-09
 */
public interface WeChatService {

    // ==========================================
    // 模式一：OAuth2 网页授权 (手机微信内使用)
    // ==========================================

    /**
     * 获取微信扫码登录 URL
     *
     * @param redirectUri 回调地址
     * @param state       状态参数
     *
     * @return 包含 URL 的结果对象
     */
    JObject getAuthUrl(String redirectUri, String state);

    /**
     * 校验登录请求
     *
     * @param request 包含 Code 的请求对象
     *
     * @return 填充了 OpenID 的完整请求对象
     */
    WeChatLoginRequest validate(WeChatLoginRequest request);

    // ==========================================
    // 模式二：扫码登录 (PC端/非微信环境使用)
    // ==========================================

    /**
     * 获取登录二维码
     *
     * @return 登录二维码获取
     */
    JObject getQrCode();
}
