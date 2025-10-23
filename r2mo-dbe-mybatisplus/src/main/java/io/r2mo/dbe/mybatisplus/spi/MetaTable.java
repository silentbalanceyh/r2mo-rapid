package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.r2mo.typed.cc.Cc;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 核心缓存信息，存储了字段和列的双边对应关系，而缓存中的 key 则是实体类的全限定名，不会因为
 * 泛型的不同而重复缓存
 *
 * @author lang : 2025-09-08
 */
@Slf4j
public class MetaTable<T> {
    private static final Cc<String, MetaTable<?>> CCT_META = Cc.open();

    private final ConcurrentMap<String, String> f2c = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> c2f = new ConcurrentHashMap<>();
    private final Class<T> entityCls;
    @Getter
    private final String table;
    @Getter
    private String primaryKey;

    private MetaTable(final Class<T> entityCls) {
        this.entityCls = entityCls;
        final TableInfo tableInfo = TableInfoHelper.getTableInfo(entityCls);
        this.table = tableInfo.getTableName();
        if (tableInfo.havePK()) {
            // Field to Column
            this.f2c.put(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
            // Column to Field
            this.c2f.put(tableInfo.getKeyColumn(), tableInfo.getKeyProperty());
            // Primary Key
            this.primaryKey = tableInfo.getKeyColumn();
        }
        /*
         * FIX-DBE: 此处的迭代不包含主键，所以需要针对主键重新做一次操作
         */
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

    Set<String> vColumn() {
        return this.c2f.keySet();
    }

    String vColumn(final String field) {
        if (!this.f2c.containsKey(field)) {
            log.info("[ R2MO ] field -> column, 字段对应的列不存在: {} / {}", field, this.entityCls.getName());
            return null;
        }
        return this.f2c.getOrDefault(field, null);
    }

    String vProperty(final String column) {
        if (!this.c2f.containsKey(column)) {
            final TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityCls);
            log.info("[ R2MO ] column -> field, 列对应的字段不存在: {} / {}", column, tableInfo.getTableName());
            return null;
        }
        return this.c2f.getOrDefault(column, null);
    }

    List<String> vColumn(final List<String> properties) {
        return properties.stream().map(this::vColumn).toList();
    }
}
