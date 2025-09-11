package io.r2mo.dbe.mybatisplus.core.domain;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.spi.SPI;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author lang : 2025-09-11
 */
public class BaseProp {

    private BaseProp() {
    }

    public static <T extends BaseEntity> T newFrom(
        final BaseEntity entity, final Supplier<T> constructor) {
        Objects.requireNonNull(constructor);
        final T created = constructor.get();
        /*
         * 拷贝
         * - language, version, enabled,
         * - appId, tenantId,
         * - <<audit>>
         * - code, cMetadata, id
         */
        copyNorm(created, entity);
        setCode(created);
        created.setCMetadata(SPI.J());
        created.setId(UUID.randomUUID());
        return created;
    }

    public static void setCommon(final BaseEntity baseEntity) {
        baseEntity.setVersion("1.0.0");
        baseEntity.setEnabled(Boolean.TRUE);
        baseEntity.setLanguage("zh-CN");
        baseEntity.setCMetadata(SPI.J());
    }

    public static void setCode(final BaseEntity baseEntity) {
        final String code = baseEntity.getCode();
        if (StrUtil.isEmpty(code)) {
            baseEntity.setCode(RandomUtil.randomString(16));
        }
    }

    public static void setCommon(final BaseEntity baseEntity,
                                 final String language, final String version) {
        if (StrUtil.isNotEmpty(language)) {
            baseEntity.setLanguage(language);
        }
        if (StrUtil.isNotEmpty(version)) {
            baseEntity.setVersion(version);
        }
    }

    public static void copyScope(final BaseEntity target,
                                 final BaseEntity source) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(source);
        target.setAppId(source.getAppId());
        target.setTenantId(source.getTenantId());
    }

    public static void copyAudit(final BaseEntity target,
                                 final BaseEntity source) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(source);
        target.setCreatedBy(source.getCreatedBy());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
    }

    public static void copyNorm(final BaseEntity target,
                                final BaseEntity source) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(source);
        target.setLanguage(source.getLanguage());
        target.setVersion(source.getVersion());
        target.setEnabled(source.isEnabled());

        copyAudit(target, source);

        copyScope(target, source);
    }
}
