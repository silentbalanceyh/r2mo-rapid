package io.r2mo.spring.weco;

import io.r2mo.typed.json.JObject;

/**
 * 微信公众号 (WeChat) 业务客户端
 * <p>
 * 定义标准化的微信 OAuth2 交互行为。
 * </p>
 *
 * @author lang : 2025-12-09
 */
public interface WeChatClient {

    /**
     * 获取 PC 端扫码登录的 URL (Website QR Connect)
     *
     * @param redirectUri 回调地址 (需与微信后台配置一致)
     * @param state       防伪随机串
     *
     * @return 包含 url 的响应对象
     */
    JObject authUrl(String redirectUri, String state);

    /**
     * 执行登录：使用 Code 换取用户信息
     *
     * @param code 微信回调的临时授权码
     *
     * @return 包含 OpenID、Nickname 等用户信息的响应对象
     */
    JObject login(String code);
}