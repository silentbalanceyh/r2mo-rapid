package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.r2mo.base.dbe.operation.QrMany;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.dbe.common.operation.AbstractDbOperation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
class QrManyImpl<T, M extends BaseMapper<T>> extends AbstractDbOperation<QueryWrapper<T>, T, M> implements QrMany<T> {
    QrManyImpl(final Class<T> entityCls, final M m) {
        super(entityCls, m);
    }

    @Override
    public List<T> execute(final QQuery query) {
        // Pre Condition
        if (Objects.isNull(query)) {
            return new ArrayList<>();
        }


        // Execute
        final QueryWrapper<T> condition = this.analyzer().where(query);
        return this.executor().selectList(condition);
    }

    @Override
    public List<T> execute(final QTree tree) {
        // Pre Condition
        if (Objects.isNull(tree)) {
            return new ArrayList<>();
        }


        // Execute
        final QueryWrapper<T> condition = this.analyzer().where(tree);
        return this.executor().selectList(condition);
    }

    @Override
    public List<T> execute(final Serializable... ids) {
        // Pre Condition
        if (0 == ids.length) {
            return new ArrayList<>();
        }


        // Execute
        return this.executor().selectByIds(Arrays.asList(ids));
    }

    @Override
    public List<T> execute(final String field, final Object value) {
        // Pre Condition
        if (StringUtils.isEmpty(field)) {
            return new ArrayList<>();
        }


        // Execute
        final QueryWrapper<T> query = this.analyzer().where(field, value);
        return this.executor().selectList(query);
    }
}
