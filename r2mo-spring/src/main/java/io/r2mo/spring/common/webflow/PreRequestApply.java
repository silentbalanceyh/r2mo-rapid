package io.r2mo.spring.common.webflow;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.domain.BaseAudit;
import io.r2mo.typed.domain.BaseScope;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-09
 */
class PreRequestApply {
    private static final Cc<String, PreRequestApply> CCT_APPLY = Cc.openThread();

    private PreRequestApply() {
    }

    static PreRequestApply of() {
        return CCT_APPLY.pick(PreRequestApply::new);
    }

    void writeAudit(final PreRequestContext context, final Object entityObj, final boolean created) {
        Objects.requireNonNull(entityObj, "[ R2MO ] -> 传入实体不可为 null");
        if (entityObj instanceof final BaseAudit entity) {
            final UUID userId = context.idUser(true);
            final LocalDateTime processedAt = LocalDateTime.now();
            entity.setUpdatedAt(processedAt);
            entity.setUpdatedBy(userId);
            if (created) {
                entity.setCreatedAt(processedAt);
                entity.setCreatedBy(userId);
            }
        }
    }

    void writeScope(final PreRequestContext context, final Object entityObj) {
        Objects.requireNonNull(entityObj, "[ R2MO ] -> 传入实体不可为 null");
        if (context.webOk() && entityObj instanceof final BaseScope entity) {
            // idApp
            entity.app(context.idApp(false));
            // idTenant
            entity.tenant(context.idTenant(false));
        }
    }
}
