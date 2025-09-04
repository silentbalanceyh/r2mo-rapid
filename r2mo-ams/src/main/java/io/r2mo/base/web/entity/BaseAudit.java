package io.r2mo.base.web.entity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author lang : 2025-09-04
 */
public interface BaseAudit {

    UUID getCreatedBy();

    void setCreatedBy(UUID userId);

    UUID getUpdatedBy();

    void setUpdatedBy(UUID userId);

    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();

    void setUpdatedAt(LocalDateTime updatedAt);
}
