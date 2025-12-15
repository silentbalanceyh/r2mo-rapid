package io.r2mo.xync.weco.wecom;

import io.r2mo.base.util.R2MO;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lang : 2025-12-11
 */
@Data
@Accessors(fluent = true, chain = true)
public class WeComIdentify implements Serializable {
    private String state;
    private String url;
    // 响应信息中的 Token
    private String token;

    public WeComIdentify() {
    }

    public WeComIdentify(final String cached) {
        final JObject cachedJ = JBase.parse(cached);
        this.state = R2MO.valueT(cachedJ, "state");
        this.url = R2MO.valueT(cachedJ, "url");
    }

    public String cached() {
        final JObject content = SPI.J()
            .put("state", this.state)
            .put("url", this.url);
        return content.encode();
    }

    public JObject response() {
        return SPI.J()
            .put("state", this.state);
    }
}
