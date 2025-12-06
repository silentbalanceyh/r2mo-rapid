package io.r2mo.base.exchange;

import io.r2mo.typed.json.JObject;

/**
 * @author lang : 2025-12-05
 */
public interface OmniQr {

    JObject waitFor(JObject params);
}
