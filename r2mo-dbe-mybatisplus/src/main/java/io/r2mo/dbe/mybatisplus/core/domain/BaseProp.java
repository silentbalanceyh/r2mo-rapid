package io.r2mo.dbe.mybatisplus.core.domain;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.spi.SPI;
import io.r2mo.typed.domain.ContextOr;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-11
 */
@Slf4j
public class BaseProp {

    private BaseProp() {
    }

    /**
     * 设置常规字段
     * <pre>
     *     - language / "zh-CN"
     *     - version  / "1.0.0"
     *     - enabled  / true
     *     - cMetadata / {}
     * </pre>
     *
     * @param baseEntity 目标实体
     */
    public static void setCommon(final BaseEntity baseEntity) {
        if (Objects.isNull(baseEntity)) {
            log.warn("[ R2MO ] - setCommon(BaseEntity): 传入实体为空，无法设置常规字段 \n" +
                " --> version, enabled, language, cMetadata");
            return;
        }
        baseEntity.setVersion("1.0.0");
        baseEntity.setActive(Boolean.TRUE);
        baseEntity.setLanguage("zh-CN");
        baseEntity.setMetadata(SPI.J());
    }

    public static void setScope(final BaseEntity baseEntity,
                                final ContextOr context) {
        if (Objects.isNull(baseEntity) || Objects.isNull(context)) {
            log.warn("[ R2MO ] - setScope(BaseEntity, ContextOr): 传入实体或上下文为空，无法设置范围字段 \n" +
                " --> appId, tenantId");
            return;
        }
        baseEntity.setAppId(context.idApp(true));
        baseEntity.setTenantId(context.idTenant(true));
    }

    /**
     * 设置 Audit 相关字段
     * <pre>
     *     - createdBy, createdAt
     *     - updatedBy, updatedAt
     * </pre>
     *
     * @param baseEntity 目标实体
     * @param userId     操作者
     * @param created    是否为创建操作
     */
    public static void setAudit(final BaseEntity baseEntity,
                                final UUID userId,
                                final boolean created) {
        if (Objects.isNull(baseEntity)) {
            log.warn("[ R2MO ] - setAudit(BaseEntity, UUID, boolean): 传入实体为空，无法设置审计字段 \n" +
                " --> createdBy, createdAt, updatedBy, updatedAt");
            return;
        }
        final LocalDateTime executeAt = LocalDateTime.now();
        if (Objects.nonNull(userId)) {
            baseEntity.setUpdatedBy(userId);
        }
        baseEntity.setUpdatedAt(executeAt);
        if (created) {
            if (Objects.nonNull(userId)) {
                baseEntity.setCreatedBy(userId);
            }
            baseEntity.setCreatedAt(executeAt);
        }
    }

    public static void setAudit(final BaseEntity baseEntity,
                                final String userId,
                                final boolean created) {
        if (Objects.isNull(userId)) {
            setAudit(baseEntity, (UUID) null, created);
        } else {
            setAudit(baseEntity, UUID.fromString(userId), created);
        }
    }

    /**
     * （空就设置）设置 Code 字段
     * <pre>
     *     - code
     * </pre>
     *
     * @param baseEntity 目标实体
     */
    public static void setCode(final BaseEntity baseEntity) {
        if (Objects.isNull(baseEntity)) {
            log.warn("[ R2MO ] - setCode(BaseEntity): 传入实体为空，无法设置 Code 字段 \n" +
                " --> code");
            return;
        }
        final String code = baseEntity.getCode();
        if (StrUtil.isEmpty(code)) {
            baseEntity.setCode(RandomUtil.randomString(16));
        }
    }

    // ------------------- 拷贝专用方法 -----------------

    /**
     * 拷贝 Scope 相关字段
     * <pre>
     *     - appId
     *     - tenantId
     * </pre>
     *
     * @param target 目标实体
     * @param source 源实体
     */
    public static void copyScope(final BaseEntity target,
                                 final BaseEntity source) {
        if (Objects.isNull(target) || Objects.isNull(source)) {
            log.warn(" [ R2MO ] - copyScope(BaseEntity, BaseEntity): 传入实体为空，无法拷贝范围字段 \n" +
                " --> appId, tenantId");
            return;
        }
        target.setAppId(source.getAppId());
        target.setTenantId(source.getTenantId());
    }

    public static void copyScope(final AbstractNormObject target,
                                 final BaseEntity source) {
        if (Objects.isNull(target) || Objects.isNull(source)) {
            log.warn(" [ R2MO ] - copyScope(AbstractNormObject, BaseEntity): 传入实体为空，无法拷贝范围字段 \n" +
                " --> appId, tenantId");
            return;
        }
        target.setAppId(source.getAppId());
        target.setTenantId(source.getTenantId());
    }

    /**
     * 拷贝全部的 Audit 相关字段
     * <pre>
     *     - createdBy
     *     - createdAt
     *     - updatedBy
     *     - updatedAt
     * </pre>
     *
     * @param target 目标实体
     * @param source 源实体
     */
    public static void copyAudit(final BaseEntity target,
                                 final BaseEntity source) {
        if (Objects.isNull(target) || Objects.isNull(source)) {
            log.warn(" [ R2MO ] - copyAudit(BaseEntity, BaseEntity): 传入实体为空，无法拷贝审计字段 \n" +
                " --> createdBy, createdAt, updatedBy, updatedAt");
            return;
        }
        target.setCreatedBy(source.getCreatedBy());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
    }

    public static void copyAudit(final AbstractNormObject target,
                                 final BaseEntity source) {
        if (Objects.isNull(target) || Objects.isNull(source)) {
            log.warn(" [ R2MO ] - copyAudit(AbstractNormObject, BaseEntity): 传入实体为空，无法拷贝审计字段 \n" +
                " --> createdBy, createdAt, updatedBy, updatedAt");
            return;
        }
        target.setCreatedBy(source.getCreatedBy());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setUpdatedAt(source.getUpdatedAt());
    }

    /**
     * 拷贝常规字段
     * <pre>
     *     - language
     *     - version
     *     - enabled
     *     - <<audit>> / createdBy, createdAt, updatedBy, updatedAt
     *     - <<scope>> / appId, tenantId
     * </pre>
     *
     * @param target 目标实体
     * @param source 源实体
     */
    public static void copyFull(final BaseEntity target,
                                final BaseEntity source) {
        if (Objects.isNull(target) || Objects.isNull(source)) {
            log.warn(" [ R2MO ] - copyFull(BaseEntity, BaseEntity): 传入实体为空，无法拷贝全字段！");
            return;
        }
        target.setLanguage(source.getLanguage());
        target.setVersion(source.getVersion());
        target.setActive(source.isActive());

        copyAudit(target, source);

        copyScope(target, source);
    }
}
