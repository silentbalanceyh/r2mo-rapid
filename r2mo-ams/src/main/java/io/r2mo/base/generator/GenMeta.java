package io.r2mo.base.generator;

import io.r2mo.typed.enums.DatabaseType;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Locale;
import java.util.Set;

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

    @Getter
    private String version;

    @Getter
    private Class<?> baseAct;

    @Getter
    private Set<String> ignoreReq;

    @Getter
    private Set<String> ignoreResp;

    public String baseActName(final boolean isFull) {
        if (isFull) {
            return this.baseAct.getName();
        } else {
            return this.baseAct.getSimpleName();
        }
    }

    public String v() {
        return this.version;
    }

    public String V() {
        return this.version.toUpperCase(Locale.getDefault());
    }
}
