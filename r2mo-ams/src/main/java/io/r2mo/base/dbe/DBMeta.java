package io.r2mo.base.dbe;

import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.typed.common.MultiKeyMap;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-24
 */
@Slf4j
public class DBMeta {
    /*
     * 双模式
     * - entityName = DBNode
     * - tableName  = DBNode
     */
    private static final MultiKeyMap<DBNode> STORED = new MultiKeyMap<>();
    /*
     * 快速定位
     * - entityCls  = DBNode
     * - daoCls     = DBNode
     */
    private static final ConcurrentMap<Class<?>, DBNode> SMART = new ConcurrentHashMap<>();
    private static DBMeta INSTANCE = new DBMeta();

    private DBMeta() {
    }

    public static DBMeta of() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DBMeta();
        }
        return INSTANCE;
    }

    public DBMeta registry(final Class<?> entity, final DBNode node) {
        if (Objects.isNull(node) || Objects.isNull(entity)) {
            log.warn("[ R2MO ] 注册元数据时，实体类或节点信息非法，直接忽略！");
            return this;
        }
        STORED.put(entity.getName(), node, node.table());
        SMART.put(entity, node);
        log.info("[ R2MO ] 注册实体类元数据：{} -> {}", entity.getName(), node);
        return this;
    }

    public DBNode findBy(final String tableOr) {
        return STORED.getOr(tableOr);
    }

    public DBNode findBy(final Class<?> entityCls) {
        DBNode found = SMART.getOrDefault(entityCls, null);
        if (Objects.isNull(found)) {
            found = this.findBy(entityCls.getName());
        }
        return found;
    }
}
