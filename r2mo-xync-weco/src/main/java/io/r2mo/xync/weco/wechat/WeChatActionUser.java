package io.r2mo.xync.weco.wechat;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.base.util.R2MO;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoAction;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

/**
 * @author lang : 2025-12-11
 */
class WeChatActionUser extends WeChatAction implements WeCoAction<JObject> {
    WeChatActionUser(final WxMpService service) {
        super(service);
    }


    @Override
    public UniResponse execute(final UniMessage<JObject> request) throws Exception {
        final JObject payload = request.payload();
        final String openid = R2MO.valueT(payload, "openid");
        if (StrUtil.isEmpty(openid)) {
            throw new _400BadRequestException("[ R2MO ] 缺少 Payload 参数: openid");
        }

        String language = request.header("language");
        if (StrUtil.isEmpty(language)) {
            language = "zh_CN";     // 默认
        }
        final WxMpUser user = this.service().getUserService().userInfo(openid, language);
        // 3. 构建返回数据
        final JObject result = SPI.J();
        result.put("openId", user.getOpenId());
        result.put("unionId", user.getUnionId()); // 只有绑定开放平台才有，否则为 null

        // 补充信息 (可选)
        result.put("subscribed", user.getSubscribe()); // 是否关注
        return UniResponse.success(result);
    }
}
