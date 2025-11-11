package io.r2mo.typed.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author lang : 2025-11-11
 */
@Data
@Accessors(chain = true, fluent = true)
public class Duration implements Serializable {

    private long value;

    private TimeUnit unit;
}
