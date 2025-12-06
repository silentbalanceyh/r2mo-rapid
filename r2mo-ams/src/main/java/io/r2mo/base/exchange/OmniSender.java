package io.r2mo.base.exchange;

import io.r2mo.typed.json.JObject;

/**
 * 发送专用接口
 *
 * @author lang : 2025-12-05
 */
public interface OmniSender {

    String send(String filename, JObject params);
}
