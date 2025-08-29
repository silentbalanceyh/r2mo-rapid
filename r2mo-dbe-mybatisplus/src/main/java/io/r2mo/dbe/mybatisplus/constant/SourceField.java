package io.r2mo.dbe.mybatisplus.constant;

import java.util.List;

/**
 * @author lang : 2025-08-29
 */
public interface SourceField {

    List<String> FIELD_ORDER = List.of(
        "id", "code", "name", "type", "status",
        "enabled", "description",
        "parentId", "appId", "directoryId",
        "createdAt", "createdBy", "updatedAt", "updatedBy",
        "language", "version", "cMetadata"
    );
}
