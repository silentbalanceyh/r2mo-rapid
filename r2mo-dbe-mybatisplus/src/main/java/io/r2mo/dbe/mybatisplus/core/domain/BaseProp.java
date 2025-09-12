package io.r2mo.dbe.mybatisplus.core.domain;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.spi.SPI;
import io.r2mo.typed.domain.ContextOr;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author lang : 2025-09-11
 */
public class BaseProp {

    private BaseProp() {
    }

    /**
     * 根据已有的 {@link BaseEntity} 创建一个新的实体，其中属性包括
     * <pre>
     *     - language, version, enabled
     *     - appId, tenantId
     *     - <<audit>> / createdBy, createdAt, updatedBy, updatedAt
     *     - code, cMetadata, id
     * </pre>
     *
     * @param entity      已有的实体
     * @param constructor 新实体的构造器
     * @param <T>         新实体类型
     *
     * @return 新实体
     */
    public static <T extends BaseEntity> T newFull(
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
        copyFull(created, entity);
        setCode(created);
        created.setCMetadata(SPI.J());
        created.setId(UUID.randomUUID());
        return created;
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
        baseEntity.setVersion("1.0.0");
        baseEntity.setEnabled(Boolean.TRUE);
        baseEntity.setLanguage("zh-CN");
        baseEntity.setCMetadata(SPI.J());
    }

    public static void setScope(final BaseEntity baseEntity,
                                final ContextOr context) {
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
        final String code = baseEntity.getCode();
        if (StrUtil.isEmpty(code)) {
            baseEntity.setCode(RandomUtil.randomString(16));
        }
    }

    /**
     * （输入合法就设置）设置常规字段
     * <pre>
     *     - language
     *     - version
     * </pre>
     *
     * @param baseEntity 目标实体
     * @param language   语言
     * @param version    版本
     */
    public static void setCommon(final BaseEntity baseEntity,
                                 final String language, final String version) {
        if (StrUtil.isNotEmpty(language)) {
            baseEntity.setLanguage(language);
        }
        if (StrUtil.isNotEmpty(version)) {
            baseEntity.setVersion(version);
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
        Objects.requireNonNull(target);
        Objects.requireNonNull(source);
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
        Objects.requireNonNull(target);
        Objects.requireNonNull(source);
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
        Objects.requireNonNull(target);
        Objects.requireNonNull(source);
        target.setLanguage(source.getLanguage());
        target.setVersion(source.getVersion());
        target.setEnabled(source.isEnabled());

        copyAudit(target, source);

        copyScope(target, source);
    }
}
