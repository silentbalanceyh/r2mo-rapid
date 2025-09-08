package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.r2mo.base.dbe.syntax.QLeaf;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 核心缓存信息，存储了字段和列的双边对应关系，而缓存中的 key 则是实体类的全限定名，不会因为
 * 泛型的不同而重复缓存
 *
 * @author lang : 2025-09-08
 */
class MetaTable<T> {
    private static final Cc<String, MetaTable<?>> CCT_META = Cc.open();

    private final ConcurrentMap<String, String> f2c = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> c2f = new ConcurrentHashMap<>();

    private MetaTable(final Class<T> entityCls) {
        final TableInfo tableInfo = TableInfoHelper.getTableInfo(entityCls);
        tableInfo.getFieldList().forEach(field -> {
            // Field to Column
            this.f2c.put(field.getProperty(), field.getColumn());
            // Column to Field
            this.c2f.put(field.getColumn(), field.getProperty());
        });
    }

    @SuppressWarnings("unchecked")
    static <T> MetaTable<T> of(final Class<T> clazz) {
        return (MetaTable<T>) CCT_META.pick(() -> new MetaTable<>(clazz), clazz.getName());
    }

    String vColumn(final String field) {
        if (!this.f2c.containsKey(field)) {
            throw new UnsupportedOperationException("[ R2MO ] field -> column, 字段对应的列不存在: " + field);
        }
        return this.f2c.getOrDefault(field, null);
    }

    String vProperty(final String column) {
        if (!this.c2f.containsKey(column)) {
            throw new UnsupportedOperationException("[ R2MO ] column -> field, 列对应的字段不存在: " + column);
        }
        return this.c2f.getOrDefault(column, null);
    }

    /*
     * 针对 Collection<?> 的单独处理流程
     */
    /*
     * Fix Issue: Cannot convert string '\xAC\xED\x00\x05sr...' from binary to utf8mb4
     */
    void in(final QLeaf leaf, final QueryWrapper<T> query) {
        final Object value = leaf.value();
        final String column = this.vColumn(leaf.field());
        if (value instanceof Collection<?>) {
            query.in(column, (Collection<?>) value);
        } else {
            /*
             * 有可能是实现部分，所以此处的核心转换要借用 UTIL 中的内容来完成
             */
            final Collection<?> values = SPI.V_UTIL.toCollection(value);
            query.in(column, values);
        }
    }

    void orderBy(final QueryWrapper<T> query, final QSorter sorter) {
        if (Objects.isNull(sorter)) {
            return;
        }
        sorter.item().forEach(kv -> {
            final boolean isAsc = kv.value();
            final String field = kv.key();
            final String column = this.vColumn(field);
            if (isAsc) {
                query.orderByAsc(column);
            } else {
                query.orderByDesc(column);
            }
        });
    }

    Map<String, Object> vColumn(final Map<String, Object> condition) {
        // new ConcurrentHashMap<>(); 此处不可以使用并发的 Map，因为空键和空值会引起问题
        final Map<String, Object> columnMap = new HashMap<>();
        condition.forEach((field, value) -> {
            final String column = this.vColumn(field);
            columnMap.put(column, value);
        });
        return columnMap;
    }

    List<String> vColumn(final List<String> properties) {
        return properties.stream().map(this::vColumn).toList();
    }
}
