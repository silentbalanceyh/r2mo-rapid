package io.r2mo.spring.mybatisplus.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.r2mo.dbe.mybatisplus.core.domain.BaseEntity;
import io.r2mo.typed.constant.DefaultConstantValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * MP注入处理器
 *
 */
@Slf4j
public class InjectionMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入填充方法，用于在插入数据时自动填充实体对象中的创建时间、更新时间、创建人、更新人等信息
     *
     * @param metaObject 元对象，用于获取原始对象并进行填充
     */
    @Override
    public void insertFill(final MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof final BaseEntity baseEntity) {
                // createdAt / createdBy
                this.setCreated(baseEntity);
                // updatedAt / updatedBy
                this.setUpdated(baseEntity);
                // id
                this.setId(baseEntity);
                // code
                this.setCode(baseEntity);

                // language, version, enabled 构造时就带有默认值，无需处理
            } else {
                final Date date = new Date();
                this.strictInsertFill(metaObject, "createdAt", Date.class, date);
                this.strictInsertFill(metaObject, "updatedAt", Date.class, date);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setCode(final BaseEntity baseEntity) {
        final String code = baseEntity.getCode();
        if (StrUtil.isBlank(code)) {
            final String generated = RandomUtil.randomString(16).toUpperCase(Locale.getDefault());
            baseEntity.setCode(generated);
        }
    }

    private void setId(final BaseEntity baseEntity) {
        final UUID id = baseEntity.getId();
        if (Objects.isNull(id)) {
            baseEntity.setId(UUID.randomUUID());
        }
    }

    /*
     * - createdAt / createdBy
     */
    private void setCreated(final BaseEntity baseEntity) {
        final LocalDateTime current;
        if (ObjectUtil.isNotNull(baseEntity.getCreatedAt())) {
            current = baseEntity.getCreatedAt();
        } else {
            current = LocalDateTime.now();
        }
        baseEntity.setCreatedAt(current);
        if (Objects.isNull(baseEntity.getCreatedBy())) {
            UUID userId = this.getUserId();
            userId = Objects.isNull(userId) ? DefaultConstantValue.BY_SYSTEM : userId;
            baseEntity.setCreatedBy(userId);
        }
    }

    private void setUpdated(final BaseEntity baseEntity) {
        baseEntity.setUpdatedAt(LocalDateTime.now());
        UUID userId = this.getUserId();
        userId = Objects.isNull(userId) ? DefaultConstantValue.BY_SYSTEM : userId;
        baseEntity.setUpdatedBy(userId);
    }

    /**
     * 更新填充方法，用于在更新数据时自动填充实体对象中的更新时间和更新人信息
     *
     * @param metaObject 元对象，用于获取原始对象并进行填充
     */
    @Override
    public void updateFill(final MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof final BaseEntity baseEntity) {
                // updatedAt / updatedBy
                this.setUpdated(baseEntity);
            } else {
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
            // throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户的信息，如果用户未登录则返回 null
     */
    private UUID getUserId() {
        return null;
    }
}
