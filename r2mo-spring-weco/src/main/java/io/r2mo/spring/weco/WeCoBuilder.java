package io.r2mo.spring.weco;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.NormMessage;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.json.JObject;

import java.util.Map;

/**
 * @author lang : 2025-12-09
 */
class WeCoBuilder {

    static UniMessage<String> message(final JObject params, final Map<String, Object> headers) {
        // 1. 构造消息标识
        String id = R2MO.valueT(params, "id");
        if (StrUtil.isEmpty(id)) {
            id = RandomUtil.randomNumbers(8);
        }
        final NormMessage<String> message = new NormMessage<>(id);

        // 2. 提取载荷
        String payload = R2MO.valueT(params, "code");
        if (StrUtil.isEmpty(payload)) {
            payload = R2MO.valueT(params, "content");
        }
        message.payload(payload);

        // 3. 处理接收目标 (UserID)
        final String toUser = R2MO.valueT(params, "to");
        if (StrUtil.isNotEmpty(toUser)) {
            message.addTo(toUser);
        }

        // 4. 注入头部信息
        if (headers != null) {
            headers.forEach(message::header);
        }

        // 5. 注入原始参数
        if (params != null && !params.isEmpty()) {
            params.toMap().forEach(message::params);
        }

        return message;
    }
}
