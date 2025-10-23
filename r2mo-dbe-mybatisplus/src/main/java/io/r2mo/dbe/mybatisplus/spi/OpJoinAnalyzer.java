package io.r2mo.dbe.mybatisplus.spi;

import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.join.DBNode;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.web._400BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-23
 */
@Slf4j
public class OpJoinAnalyzer implements QrAnalyzer<MPJQueryWrapper<?>> {
    private final DBRef ref;
    private final ConcurrentMap<Class<?>, MetaTable<?>> metaMap = new ConcurrentHashMap<>();

    public OpJoinAnalyzer(final DBRef ref) {
        this.ref = ref;
        ref.findAll().forEach(node -> {
            final Class<?> entityCls = node.entity();
            this.metaMap.putIfAbsent(entityCls, MetaTable.of(entityCls));
        });
        log.info("[ R2MO ] 合计加载 JOIN 关联实体元信息：{}", this.metaMap.keySet());
    }

    @Override
    public MPJQueryWrapper<?> whereIn(final String field, final Object... values) {
        return null;
    }

    @Override
    public MPJQueryWrapper<?> where(final Map<String, Object> condition) {
        return null;
    }

    @Override
    public MPJQueryWrapper<?> where(final String field, final Object value) {
        final QValue qValue = QValue.of(field, value);
        final Kv<String, String> column = this.findColumn(qValue.field());
        return null;
    }

    @Override
    public MPJQueryWrapper<?> where(final QTree tree, final QSorter sorter) {
        return null;
    }

    @Override
    public MPJQueryWrapper<?> where(final QQuery query) {
        return null;
    }

    @Override
    public <PAGE> PAGE page(final QQuery query) {
        return null;
    }

    /**
     * 通过属性名查找列对应信息
     * <pre>
     *     1. 列中包含了表别名，别名会出现在 SQL 语句中
     *     2. 属性名 -> 列表名
     * </pre>
     *
     * @param field 属性名
     *
     * @return 表别名 + 列名
     */
    private Kv<String, String> findColumn(final String field) {
        // 1) 先用主实体
        final DBNode node = this.ref.find();
        final MetaTable<?> primary = this.metaMap.get(node.entity());
        final String c1 = primary.vColumn(field);
        if (c1 != null) {
            final String tableAlias = this.ref.findTableAlias(node.entity());
            return Kv.create(tableAlias, c1);
        }

        // 2) 其他实体（第一个匹配即返回）
        for (final Class<?> other : this.metaMap.keySet()) {
            final MetaTable<?> meta = this.metaMap.get(other);
            if (meta == primary) {
                continue;
            }
            final String c2 = meta.vColumn(field);
            if (c2 != null) {
                // 这里假定 MetaTable 提供表名访问（常见命名：table() / tableName()）
                final String tableAlias = this.ref.findTableAlias(other);
                return Kv.create(tableAlias, c2);
            }
        }

        // 3) 都找不到 -> 400
        throw new _400BadRequestException("[ R2MO ] 无法识别的查询字段: " + field);
    }
}
