package io.r2mo.xync.sms;

import io.r2mo.base.exchange.UniCredential;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author lang : 2025-12-08
 */
@Data
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode(callSuper = false)
public class SmsCredential implements UniCredential {
    private String accessId;
    private String accessSecret;
}
