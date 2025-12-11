package io.r2mo.xync.weco.wechat;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoAction;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * 动作：使用 Code 换取 OpenID 和用户信息 (WX_LOGIN_BY)
 *
 * @author lang : 2025-12-10
 */
class WeChatActionLogin extends WeChatAction implements WeCoAction<String> {

    WeChatActionLogin(final WxMpService service) {
        super(service);
    }

    /**
     * 执行检查扫码登录会话状态的命令。
     * <pre>
     *      输入格式 ({@link UniMessage})
     *      Header:
     *        (无特殊必需 Header)
     *      Payload:
     *        String. 扫码会话的 UUID。**必需**。
     *      输出格式 ({@link UniResponse})
     *      Payload:
     *        {@link JObject}. 包含以下字段:
     *          - status: String. 当前状态 (WAITING/SUCCESS)。
     *          - isSuccess: Boolean. 是否已登录成功。
     *          - openId: String. 仅在 status 为 SUCCESS 时返回用户的 OpenID。
     * </pre>
     *
     * @param request 封装了会话 UUID 的 UniMessage 请求。
     *
     * @return 包含当前状态和 OpenID (如果成功) 的 UniResponse。
     * @throws Exception 参数缺失。
     */
    @Override
    public UniResponse execute(final UniMessage<String> request) throws Exception {
        final String code = request.payload();

        if (code == null) {
            throw new _400BadRequestException("[ R2MO ] 缺少 Payload 参数: Code");
        }

        // 1. 换取 AccessToken
        final WxOAuth2AccessToken accessToken = this.service().getOAuth2Service().getAccessToken(code);

        // 2. 换取用户信息 (依赖于授权时的 scope)
        final WxOAuth2UserInfo userInfo = this.service().getOAuth2Service().getUserInfo(accessToken, null);

        // 返回包含 OpenID, Nickname 等信息的对象
        return UniResponse.success(userInfo);
    }
}