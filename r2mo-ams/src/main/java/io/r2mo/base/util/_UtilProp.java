package io.r2mo.base.util;

import io.r2mo.typed.domain.extension.AbstractNormObject;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

class _UtilProp extends _UtilNum {

    public static <T extends AbstractNormObject> void vModel(
        final T target,
        final Supplier<String> mIdFn,
        final Supplier<String> mKeyFn
    ) {
        Optional.ofNullable(mIdFn).map(Supplier::get)
            .ifPresent(modelId -> target.extension("modelId", modelId));
        Optional.ofNullable(mKeyFn).map(Supplier::get)
            .ifPresent(modelKey -> target.extension("modelKey", modelKey));
    }

    public static <T extends AbstractNormObject> void vActive(
        final T target,
        final Supplier<Boolean> activeFn
    ) {
        vActive(target, activeFn, null, null);
    }

    public static <T extends AbstractNormObject> void vActive(
        final T target,
        final Supplier<Boolean> activeFn,
        final Supplier<String> versionFn,
        final Supplier<String> languageFn
    ) {
        Optional.ofNullable(activeFn).map(Supplier::get)
            .ifPresent(active -> target.extension("active", active));
        Optional.ofNullable(versionFn).map(Supplier::get)
            .ifPresent(version -> target.extension("version", version));
        Optional.ofNullable(languageFn).map(Supplier::get)
            .ifPresent(language -> target.extension("language", language));
    }

    public static <T extends AbstractNormObject, ID> void vAudit(
        final T target,
        final Supplier<ID> uByFn,
        final Supplier<LocalDateTime> uAtFn  // updatedAt (LocalDateTime)
    ) {
        vAudit(target, null, null, uByFn, uAtFn);
    }

    public static <T extends AbstractNormObject, ID> void vAudit(
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

    public static <T extends AbstractNormObject, ID> void vScope(
        final T target,
        final Supplier<ID> appFn,
        final Supplier<ID> tenantFn
    ) {
        vScope(target, appFn, tenantFn, null);
    }

    public static <T extends AbstractNormObject, ID> void vScope(
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
        return Optional.ofNullable(loader).map(Supplier::get);
    }

    // 辅助工具：处理各种类型的 ID 转 UUID
    private static <ID> Optional<UUID> valueUUID(final Supplier<ID> loader) {
        return Optional.ofNullable(loader).map(Supplier::get).map(val -> {
            if (val instanceof UUID) {
                return (UUID) val;
            }
            return UUID.fromString(val.toString());
        });
    }
}
