package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.r2mo.base.dbe.operation.QrOne;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.dbe.common.operation.AbstractDbOperation;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lang : 2025-08-28
 */
class QrOneImpl<T, M extends BaseMapper<T>> extends AbstractDbOperation<QueryWrapper<T>, T, M> implements QrOne<T> {
    QrOneImpl(final Class<T> entityCls, final M m) {
        super(entityCls, m);
    }

    @Override
    public Optional<T> execute(final QTree syntax) {
        if (Objects.isNull(syntax) || syntax.isOk()) {
            return Optional.empty();
        }


        // Execute
        final QueryWrapper<T> query = this.analyzer().where(syntax);


        // Result
        return Optional.ofNullable(this.executor().selectOne(query));
    }

    @Override
    public Optional<T> execute(final Map<String, Object> condition) {
        if (Objects.isNull(condition) || condition.isEmpty()) {
            return Optional.empty();
        }
        final QueryWrapper<T> query = this.analyzer().where(condition);
        return Optional.ofNullable(this.executor().selectOne(query));
    }

    @Override
    public Optional<T> execute(final Serializable id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }


        return Optional.ofNullable(this.executor().selectById(id));
    }

    @Override
    public Optional<T> execute(final String field, final Object value) {
        // Pre Condition
        if (StringUtils.isEmpty(field)) {
            return Optional.empty();
        }


        // Execute
        final QueryWrapper<T> query = this.analyzer().where(field, value);


        // Result
        return Optional.ofNullable(this.executor().selectOne(query));
    }
}
