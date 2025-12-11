package io.r2mo.xync.weco.wechat;

import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.xync.weco.WeCoAction;
import me.chanjar.weixin.mp.api.WxMpService;

/**
 * @author lang : 2025-12-10
 */
class WeChatActionPre extends WeChatAction implements WeCoAction<String> {

    WeChatActionPre(final WxMpService service) {
        super(service);
    }

    @Override
    public UniResponse execute(final UniMessage<String> request) throws Exception {
        final String signature = request.params("signature");
        final String timestamp = request.params("timestamp");
        final String nonce = request.params("nonce");

        final boolean checked = this.service().checkSignature(timestamp, nonce, signature);
        final JObject checkedJ = SPI.J();
        checkedJ.put("success", checked);
        return UniResponse.success(checkedJ);
    }
}
