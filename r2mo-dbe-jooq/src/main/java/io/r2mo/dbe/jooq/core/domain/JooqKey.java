package io.r2mo.dbe.jooq.core.domain;

import io.r2mo.SourceReflect;
import io.r2mo.base.program.R2Vector;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/**
 * @author lang : 2025-10-18
 */
class JooqKey {

    private final Table<?> table;
    private R2Vector vector;

    JooqKey(final Table<?> table) {
        this.table = table;
    }

    void vector(final R2Vector vector) {
        this.vector = vector;
    }

    TreeSet<String> pkSet() {
        return this.keySet(this.table.getPrimaryKey());
    }

    String pkOne() {
        final TreeSet<String> keys = this.pkSet();
        if (keys.isEmpty()) {
            return null;
        }
        // 由于 jooq 必须是 21，所以此处无碍如此调用
        return keys.first();
    }

    <T> Object pkValue(final T input) {
        final String primaryField = this.pkOne();
        if (Objects.isNull(primaryField)) {
            return null;
        } else {
            return SourceReflect.value(input, primaryField);
        }
    }

    <T> List<Object> pkValue(final List<T> list) {
        final List<Object> values = new ArrayList<>();
        list.stream().map(this::pkValue).forEach(values::add);
        return values;
    }

    List<TreeSet<String>> ukList() {
        final List<TreeSet<String>> ukList = new ArrayList<>();
        this.table.getKeys().forEach(ukField -> {
            final TreeSet<String> ukSet = this.keySet(ukField);
            if (!ukSet.isEmpty()) {
                ukList.add(ukSet);
            }
        });
        return ukList;
    }

    private TreeSet<String> keySet(final UniqueKey<?> uniqueKey) {
        if (Objects.isNull(uniqueKey)) {
            return new TreeSet<>();
        }
        final TreeSet<String> keySet = new TreeSet<>();
        uniqueKey.getFields().stream().map(TableField::getName)
            .map(this.vector::mapByColumn)
            .filter(Objects::nonNull)
            .forEach(keySet::add);
        return keySet;
    }
}
