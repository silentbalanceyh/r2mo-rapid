package io.r2mo.base.dbe.operation;

import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;

import java.util.Map;

/**
 * @author lang : 2025-08-28
 */
public interface QrAnalyzer<CONDITION> {

    CONDITION whereIn(String field, Object... values);

    CONDITION where(Map<String, Object> condition);

    CONDITION where(String field, Object value);

    CONDITION where(QTree tree, QSorter sorter);

    default CONDITION where(final QTree tree) {
        return this.where(tree, tree.sortBy());
    }

    CONDITION where(QQuery query);

    <PAGE> PAGE page(QQuery query);
}
