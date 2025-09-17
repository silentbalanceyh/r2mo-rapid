package io.r2mo.typed.common;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lang : 2025-09-11
 */
@Data
@Builder
@Accessors(fluent = true)
public class Ref implements Serializable {

    public static final String REF_TYPE = "refType";
    public static final String REF_ID = "refId";

    private String refType;
    private UUID refId;

    private Ref(final String type, final UUID id) {
        this.refType = type;
        this.refId = id;
    }

    public static Ref of(final String type, final UUID id) {
        return new Ref(type, id);
    }
}
