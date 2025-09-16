package io.r2mo.io.modeling;

import io.r2mo.base.io.locator.StorePath;
import io.r2mo.io.enums.TransferOf;
import io.r2mo.io.enums.TransferType;
import io.r2mo.typed.json.JObject;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lang : 2025-09-16
 */
@Data
@Builder
@Accessors(fluent = true)
public class TransferParameter implements Serializable {
    private StorePath stored;
    private TransferOf transferOf;
    private TransferType transferType;
    private JObject data;
}
