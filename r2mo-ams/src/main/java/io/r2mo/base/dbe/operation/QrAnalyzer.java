package io.r2mo.base.dbe.operation;

import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
public interface QrAnalyzer<CONDITION> {

    CONDITION whereIn(String field, Object... values);

    default CONDITION whereId(final Serializable id) {
        return null;
    }

    CONDITION where(Map<String, Object> condition);

    CONDITION where(String field, Object value);

    CONDITION where(QTree tree, QSorter sorter);

    default CONDITION where(final QTree tree) {
        // Fix issue: Cannot invoke "QTree.sortBy()" because "tree" is null
        return this.where(tree, Objects.isNull(tree) ? null : tree.sortBy());
    }

    CONDITION where(QQuery query);

    <PAGE> PAGE page(QQuery query);
}
