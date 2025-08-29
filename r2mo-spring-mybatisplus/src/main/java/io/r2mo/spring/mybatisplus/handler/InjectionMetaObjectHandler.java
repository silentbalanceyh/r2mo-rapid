package io.r2mo.spring.mybatisplus.handler;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.r2mo.dbe.mybatisplus.core.domain.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * MP注入处理器
 *
 */
@Slf4j
public class InjectionMetaObjectHandler implements MetaObjectHandler {

    /** 如果用户不存在默认注入-1代表无用户 */
    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    /**
     * 插入填充方法，用于在插入数据时自动填充实体对象中的创建时间、更新时间、创建人、更新人等信息
     *
     * @param metaObject 元对象，用于获取原始对象并进行填充
     */
    @Override
    public void insertFill(final MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof final BaseEntity baseEntity) {
                // 获取当前时间作为创建时间和更新时间，如果创建时间不为空，则使用创建时间，否则使用当前时间
                final LocalDateTime current;
                if (ObjectUtil.isNotNull(baseEntity.getCreatedAt())) {
                    current = baseEntity.getCreatedAt();
                } else {
                    current = LocalDateTime.now();
                }
                baseEntity.setCreatedAt(current);
                baseEntity.setUpdatedAt(current);

                // 如果创建人为空，则填充当前登录用户的信息
                if (ObjectUtil.isNull(baseEntity.getCreatedBy())) {
                    final UUID userId = this.getUserId();
                    if (ObjectUtil.isNotNull(userId)) {
                        // 填充创建人、更新人和创建部门信息
                        baseEntity.setCreatedBy(userId);
                        baseEntity.setUpdatedBy(userId);
                        // baseEntity.setCreateDept(ObjectUtils.notNull(baseEntity.getCreateDept(), loggedUser.getDeptId()));
                    } else {
                        // 填充创建人、更新人和创建部门信息
                        baseEntity.setCreatedBy(DEFAULT_USER_ID);
                        baseEntity.setUpdatedBy(DEFAULT_USER_ID);
                        // baseEntity.setCreateDept(ObjectUtils.notNull(baseEntity.getCreateDept(), DEFAULT_USER_ID));
                    }
                }
            } else {
                final Date date = new Date();
                this.strictInsertFill(metaObject, "createdAt", Date.class, date);
                this.strictInsertFill(metaObject, "updatedAt", Date.class, date);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
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
                // 获取当前时间作为更新时间，无论原始对象中的更新时间是否为空都填充
                baseEntity.setUpdatedAt(LocalDateTime.now());

                // 获取当前登录用户的ID，并填充更新人信息
                final UUID userId = this.getUserId();
                if (ObjectUtil.isNotNull(userId)) {
                    baseEntity.setUpdatedBy(userId);
                } else {
                    baseEntity.setUpdatedBy(DEFAULT_USER_ID);
                }
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
