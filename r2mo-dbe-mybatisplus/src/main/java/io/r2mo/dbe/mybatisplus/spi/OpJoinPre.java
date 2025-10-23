package io.r2mo.dbe.mybatisplus.spi;

import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.join.DBNode;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.typed.common.Kv;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-23
 */
abstract class OpJoinPre<T> {

    protected final DBRef ref;
    /**
     * 此处虽然有哈希表，但实际不会创建新的 {@link MetaTable} 实例，均为复用已有实例，在哈希表中保存了对应的
     * 引用信息，它内部使用了实例的类型 {@link Class} = {@link MetaTable} 做唯一缓存。
     */
    protected final ConcurrentMap<Class<?>, MetaTable<?>> metaMap;

    protected OpJoinPre(final DBRef ref) {
        this.ref = ref;
        this.metaMap = this.preConfigure();
    }

    private ConcurrentMap<Class<?>, MetaTable<?>> preConfigure() {
        final ConcurrentMap<Class<?>, MetaTable<?>> metaMap = MetaFix.toMetaMap(this.ref);
        // 先把主实体的内容做 configure 保证不会被覆盖
        final Class<?> entityCls = this.ref.find().entity();
        final MetaTable<?> metaMain = metaMap.get(entityCls);
        metaMain.vColumn().forEach(column -> this.ref.configure(column, metaMain.getTable()));

        // 从第二实体开始做 configure，但不能采取覆盖模式，已经存在的不再处理
        metaMap.keySet().stream()
            .filter(entityOf -> entityOf != entityCls)
            .map(metaMap::get)
            .forEach(metaTable -> metaTable.vColumn().forEach(column ->
                this.ref.configure(column, metaTable.getTable())
            ));
        return metaMap;
    }

    protected MPJQueryWrapper<T> whereId(final Serializable id) {
        // 提取主实体的主键值
        final String pkColumn = this.getColumnPrimaryKey();
        final MPJQueryWrapper<T> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.eq(pkColumn, id);
        return queryWrapper;
    }

    protected Kv<String, String> getFieldId(final DBNode found) {
        final MetaTable<?> table = this.metaMap.get(found.entity());
        final String pkColumn = table.getPrimaryKey();
        return Kv.create(table.getTable(), table.vProperty(pkColumn));
    }

    protected String getColumnPrimaryKey() {
        final DBNode found = this.ref.find();
        final MetaTable<?> table = this.metaMap.get(found.entity());
        return table.getPrimaryKey();
    }

    protected Set<Class<?>> getJoinedEntities() {
        final DBNode found = this.ref.find();
        return this.metaMap.keySet().stream()
            .filter(entityCls -> entityCls != found.entity())
            .collect(Collectors.toSet());
    }
}
