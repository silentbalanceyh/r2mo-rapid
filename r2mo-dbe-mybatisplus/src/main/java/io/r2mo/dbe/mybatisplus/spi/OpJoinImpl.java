package io.r2mo.dbe.mybatisplus.spi;

import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
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
class OpJoinImpl<T, M extends MPJBaseMapper<T>> implements OpJoin<T, MPJQueryWrapper<T>> {

    private final DBRef ref;
    private final M executor;

    OpJoinImpl(final DBRef ref, final M executor) {
        this.executor = executor;
        this.ref = ref;
    }

    @Override
    public JArray findMany(final MPJQueryWrapper<T> queryWrapper) {
        return null;
    }

    @Override
    public JObject findOne(final MPJQueryWrapper<T> queryWrapper) {
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
    public Optional<Long> count(final MPJQueryWrapper<T> queryWrapper) {
        return Optional.ofNullable(this.executor.selectJoinCount(queryWrapper));
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
    public Boolean removeBy(final MPJQueryWrapper<T> queryWrapper) {
        return null;
    }

    @Override
    public JObject updateById(final Serializable id, final JObject latest) {
        return null;
    }

    @Override
    public JObject update(final MPJQueryWrapper<T> queryWrapper, final JObject latest) {
        return null;
    }
}
