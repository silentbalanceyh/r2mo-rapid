package io.r2mo.xync.weco.wechat;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoConstant;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * 动作：获取 OAuth2 授权跳转 URL (WX_AUTH_URL)
 *
 * @author lang : 2025-12-10
 */
class WeChatActionAuthUrl extends WeChatAction implements WeCoAction<Void> {

    WeChatActionAuthUrl(final WxMpService service) {
        super(service);
    }

    /**
     * 执行获取微信 OAuth2 网页授权跳转 URL 的命令。
     * <pre>
     *     输入格式 ({@link UniMessage})
     *       Header:
     *         - redirectUri: String. 授权成功后，微信回调的完整地址。**必需**。
     *         - state: String. 业务自定义状态参数，原样返回。**必需**。
     *       Payload:
     *         null (Void)
     *     输出格式 ({@link UniResponse}
     *       Payload:
     *         String. 构造完成的微信授权跳转 URL (<a href="https://open.weixin.qq.com/connect/oauth2/authorize?...">WeChat</a>)
     * </pre>
     *
     * @param request 封装了回调地址和状态参数的 UniMessage 请求。
     *
     * @return 包含授权跳转 URL 字符串的 UniResponse。
     * @throws Exception 微信API调用失败或参数缺失。
     */
    @Override
    public UniResponse execute(final UniMessage<Void> request) throws Exception {
        final String redirectUri = request.header(WeCoConstant.HEADER_REDIRECT_URI);
        final String state = request.header(WeCoConstant.HEADER_STATE);

        if (StrUtil.isBlank(redirectUri) || StrUtil.isBlank(state)) {
            throw new _400BadRequestException("[ R2MO ] 缺少必要 Header: redirectUri 或 state");
        }

        // 构造微信网页授权 URL (snsapi_userinfo 模式获取详细用户信息)
        final String url = this.service().getOAuth2Service().buildAuthorizationUrl(
            redirectUri,
            // 注意：这里使用 snsapi_userinfo，如果仅需 OpenID 可用 snsapi_base
            "snsapi_userinfo",
            state
        );

        return UniResponse.success(url);
    }
}
