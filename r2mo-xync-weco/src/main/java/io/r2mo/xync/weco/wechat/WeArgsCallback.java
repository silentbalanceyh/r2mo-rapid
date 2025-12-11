package io.r2mo.xync.weco.wechat;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author lang : 2025-12-11
 */
@Data
@Accessors(fluent = true, chain = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class WeArgsCallback extends WeArgsSignature implements Serializable {

    private String openid;

    private String encType;

    private String msgSignature;

    private WeChatType type;

    @Override
    public JObject build() {
        return super.build()
            .put("openid", this.openid)
            .put("encType", this.encType)
            .put("msgSignature", this.msgSignature);
    }

    public JObject message() {
        return SPI.J()
            .put("content", this.build());
    }
}
