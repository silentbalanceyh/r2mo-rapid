package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;

import java.util.Map;

/**
 * @author lang : 2025-10-23
 */
class OpJoinAnalyzer implements QrAnalyzer<QueryWrapper<?>> {
    private final DBRef ref;

    OpJoinAnalyzer(DBRef ref) {
        this.ref = ref;
    }

    @Override
    public QueryWrapper<?> whereIn(final String field, final Object... values) {
        return null;
    }

    @Override
    public QueryWrapper<?> where(final Map<String, Object> condition) {
        return null;
    }

    @Override
    public QueryWrapper<?> where(final String field, final Object value) {
        return null;
    }

    @Override
    public QueryWrapper<?> where(final QTree tree, final QSorter sorter) {
        return null;
    }

    @Override
    public QueryWrapper<?> where(final QQuery query) {
        return null;
    }

    @Override
    public <PAGE> PAGE page(final QQuery query) {
        return null;
    }
}
