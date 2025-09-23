package io.r2mo.base.io.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lang : 2025-09-23
 */
@Data
@Accessors(fluent = true)
public class FileMem implements Serializable {
    private String name;
    private byte[] content;
}
