package io.r2mo.dbe.mybatisplus.spi;

import io.r2mo.base.dbe.constant.OpType;
import io.r2mo.dbe.mybatisplus.core.domain.BaseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-09-08
 */
class MetaObject {

    @SuppressWarnings("all")
    static <T> void insert(final T entity, final OpType type) {
        // Fix issue of insert
        if (OpType.CREATE != type) {
            return;
        }
        // 直接实体
        if (entity instanceof final BaseEntity baseEntity) {
            final UUID id = baseEntity.getId();
            if (Objects.isNull(id)) {
                baseEntity.setId(UUID.randomUUID());
            }
        }
        // 间接实体
        if (entity instanceof final LinkedHashMap baseMap) {
            final Object id = baseMap.get("id");
            if (Objects.isNull(id)) {
                baseMap.put("id", UUID.randomUUID());
            }
        }
    }

    static <T> void insert(final List<T> entities, final OpType type) {
        if (OpType.CREATE != type) {
            return;
        }
        entities.forEach(entity -> insert(entity, type));
    }
}
