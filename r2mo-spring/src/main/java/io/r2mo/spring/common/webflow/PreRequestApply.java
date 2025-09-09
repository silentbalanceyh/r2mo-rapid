package io.r2mo.spring.common.webflow;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.r2mo.base.web.entity.BaseAudit;
import io.r2mo.base.web.entity.BaseScope;
import io.r2mo.typed.cc.Cc;

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
            final UUID userId = context.userId();
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
        if (context.isOk() && entityObj instanceof final BaseScope entity) {
            // appId
            entity.app(context.appId(false));
            // tenantId
            entity.tenant(context.tenantId(false));
        }
    }

    void writeBean(final Object source, final Object target) {
        final CopyOptions copyOptions = new CopyOptions()
            .ignoreNullValue()
            .ignoreError();
        BeanUtil.copyProperties(source, target, copyOptions);
    }
}
