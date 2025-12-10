package io.r2mo.spring.security.weco;

import io.r2mo.typed.json.JObject;

/**
 * 企业微信 (WeCom) 业务服务
 * <p>
 * 职责：
 * 1. 管理企业微信的 OAuth2 授权流程
 * 2. 处理企业成员身份校验
 * </p>
 *
 * @author lang : 2025-12-09
 */
public interface WeComService {

    /**
     * 获取企业微信扫码登录 URL
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
     * @return 填充了 UserID 的完整请求对象
     */
    WeComLoginRequest validate(WeComLoginRequest request);
}
