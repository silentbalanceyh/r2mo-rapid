package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.operation.OpJoin;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author lang : 2025-10-23
 */
class OpJoinImpl<M extends BaseMapper<?>> implements OpJoin<QueryWrapper<?>> {

    private final DBRef ref;
    private final M executor;

    OpJoinImpl(final DBRef ref, final M executor) {
        this.executor = executor;
        this.ref = ref;
    }

    @Override
    public JArray findMany(final QueryWrapper<?> queryWrapper) {
        return null;
    }

    @Override
    public JObject findOne(final QueryWrapper<?> queryWrapper) {
        return null;
    }

    @Override
    public JObject findPage(final QQuery query) {
        return null;
    }

    @Override
    public JObject findById(final Serializable id) {
        return null;
    }

    @Override
    public Optional<Long> count(final QueryWrapper<?> queryWrapper) {
        return Optional.empty();
    }

    @Override
    public JObject create(final JObject latest) {
        return null;
    }

    @Override
    public Boolean removeById(final Serializable id) {
        return null;
    }

    @Override
    public Boolean removeBy(final QueryWrapper<?> queryWrapper) {
        return null;
    }

    @Override
    public JObject updateById(final Serializable id, final JObject latest) {
        return null;
    }

    @Override
    public JObject update(final QueryWrapper<?> queryWrapper, final JObject latest) {
        return null;
    }
}
