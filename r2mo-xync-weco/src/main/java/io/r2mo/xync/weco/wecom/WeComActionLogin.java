package io.r2mo.xync.weco.wecom;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoAction;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;

/**
 * 动作：使用 Code 换取 UserID 和用户信息 (WX_LOGIN_BY)
 *
 * @author lang : 2025-12-10
 */
class WeComActionLogin extends WeComAction implements WeCoAction<String> {

    WeComActionLogin(final WxCpService service) {
        super(service);
    }

    /**
     * 执行使用 Code 换取用户信息的命令。
     * <pre>
     *      输入格式 ({@link UniMessage})
     *      Header:
     *        (无特殊必需 Header)
     *      Payload:
     *        String. 授权回调返回的 Code。**必需**。
     *      输出格式 ({@link UniResponse})
     *      Payload:
     *        {@link me.chanjar.weixin.cp.bean.WxCpUser}. 包含用户的详细信息。
     * </pre>
     *
     * @param request 封装了授权 Code 的 UniMessage 请求。
     *
     * @return 包含用户信息的 UniResponse。
     * @throws Exception 参数缺失或微信API调用失败。
     */
    @Override
    public UniResponse execute(final UniMessage<String> request) throws Exception {
        final String code = request.payload();

        if (code == null) {
            throw new _400BadRequestException("[ R2MO ] 缺少 Payload 参数: Code");
        }

        // 1. 企微 Code 换取 UserID (内部员工) 或 OpenID (外部联系人)
        final WxCpOauth2UserInfo oauth2UserInfo = this.service().getOauth2Service().getUserInfo(code);
        final String userId = oauth2UserInfo.getUserId();

        // 2. 如果是企业内部成员 (userId不为空)，获取详细信息
        if (userId != null) {
            final var userDetail = this.service().getUserService().getById(userId);
            final JObject loggedJ = SPI.J();
            loggedJ.put("userId", userDetail.getUserId());
            return UniResponse.success(loggedJ);
        }

        // 3. 如果是外部联系人 (仅 openId)，则只能返回基础 OAuth2 信息 (或者视需求进一步获取外部联系人详情)
        // 目前策略：返回 OAuth2UserInfo，由上层处理
        return UniResponse.success(SPI.J());
    }
}