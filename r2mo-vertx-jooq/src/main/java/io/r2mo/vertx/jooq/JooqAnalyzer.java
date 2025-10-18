package io.r2mo.vertx.jooq;

import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;
import org.jooq.Condition;

import java.util.Map;

/**
 * @author lang : 2025-10-18
 */
public class JooqAnalyzer implements QrAnalyzer<Condition> {
    @Override
    public Condition whereIn(final String field, final Object... values) {
        return null;
    }

    @Override
    public Condition where(final Map<String, Object> condition) {
        return null;
    }

    @Override
    public Condition where(final String field, final Object value) {
        return null;
    }

    @Override
    public Condition where(final QTree tree, final QSorter sorter) {
        return null;
    }

    @Override
    public Condition where(final QQuery query) {
        return null;
    }

    @Override
    public <PAGE> PAGE page(final QQuery query) {
        return null;
    }
}
