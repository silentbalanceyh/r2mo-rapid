package io.r2mo.base.generator;

import io.r2mo.typed.enums.DatabaseType;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author lang : 2025-09-04
 */
@Builder
public class GenMeta implements Serializable {

    @Getter
    private SourceStructure structure;

    @Getter
    private DatabaseType database;

    @Getter
    private String schema;

    @Getter
    private String spi;
}
