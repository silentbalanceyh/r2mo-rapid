package io.r2mo.xync.weco;

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
public class WeCoBuilder {
    /**
     * 格式说明
     * <pre>
     *     1. 消息ID
     *     2. content / code 二选一，code 拥有更高优先级
     *     3. to (接收用户ID) -> 发送时才会使用
     *     4. 通用 header
     *     5. 通用 params
     * </pre>
     *
     * @param params  参数
     * @param headers 头部信息
     * @return 统一消息对象
     */
    @SuppressWarnings("unchecked")
    public static <T> UniMessage<T> message(final JObject params, final Map<String, Object> headers) {
        // 1. 构造消息标识
        String id = R2MO.valueT(params, "id");
        if (StrUtil.isEmpty(id)) {
            id = RandomUtil.randomNumbers(8);
        }
        final NormMessage<T> message = new NormMessage<>(id);

        // 2. 提取载荷
        final String payload = R2MO.valueT(params, "code");
        if (StrUtil.isEmpty(payload)) {
            final Object content = R2MO.valueT(params, "content");
            // Content 可支持多种类型
            message.payload((T) content);
        } else {
            message.payload((T) payload);
        }

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
