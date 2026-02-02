package io.r2mo.base.util;

import io.r2mo.typed.domain.extension.AbstractNormObject;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

class _UtilProp extends _UtilNum {
    
    public static <T extends AbstractNormObject, ID> void setAudit(
        final T target,
        final Supplier<ID> uByFn,
        final Supplier<LocalDateTime> uAtFn  // updatedAt (LocalDateTime)
    ) {
        setAudit(target, null, null, uByFn, uAtFn);
    }

    public static <T extends AbstractNormObject, ID> void setAudit(
        final T target,
        final Supplier<ID> cByFn,    // createdBy
        final Supplier<LocalDateTime> cAtFn, // createdAt (LocalDateTime)
        final Supplier<ID> uByFn,    // updatedBy
        final Supplier<LocalDateTime> uAtFn  // updatedAt (LocalDateTime)
    ) {
        if (target == null) {
            return;
        }

        // 1. 处理 CreatedBy / UpdatedBy (UUID 转换)
        valueUUID(cByFn).ifPresent(target::setCreatedBy);
        valueUUID(uByFn).ifPresent(target::setUpdatedBy);

        // 2. 处理 CreatedAt / UpdatedAt (时间赋值)
        valueDatetime(cAtFn).ifPresent(target::setUpdatedAt);
        valueDatetime(uAtFn).ifPresent(target::setUpdatedAt);
    }

    public static <T extends AbstractNormObject, ID> void setScope(
        final T target,
        final Supplier<ID> appFn,
        final Supplier<ID> tenantFn
    ) {
        setScope(target, appFn, tenantFn, null);
    }

    public static <T extends AbstractNormObject, ID> void setScope(
        final T target,
        final Supplier<ID> appFn,
        final Supplier<ID> tenantFn,
        final Supplier<ID> idFn
    ) {
        if (target == null) {
            return;
        }

        valueUUID(idFn).ifPresent(target::setId);
        valueUUID(appFn).ifPresent(target::setAppId);
        valueUUID(tenantFn).ifPresent(target::setTenantId);
    }

    private static Optional<LocalDateTime> valueDatetime(final Supplier<LocalDateTime> loader) {
        return Optional.ofNullable(loader)
            .map(Supplier::get);
    }

    // 辅助工具：处理各种类型的 ID 转 UUID
    private static <ID> Optional<UUID> valueUUID(final Supplier<ID> loader) {
        return Optional.ofNullable(loader)
            .map(Supplier::get)
            .map(val -> {
                if (val instanceof UUID) {
                    return (UUID) val;
                }
                return UUID.fromString(val.toString());
            });
    }
}
