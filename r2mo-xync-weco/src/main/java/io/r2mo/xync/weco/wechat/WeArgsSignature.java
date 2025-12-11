package io.r2mo.xync.weco.wechat;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author lang : 2025-12-11
 */
@Data
@SuperBuilder
@Accessors(fluent = true, chain = true)
@NoArgsConstructor
public class WeArgsSignature implements Serializable {
    private String signature;
    private String timestamp;
    private String nonce;

    public JObject build() {
        return SPI.J()
            .put("signature", this.signature)
            .put("timestamp", this.timestamp)
            .put("nonce", this.nonce);
    }
}
