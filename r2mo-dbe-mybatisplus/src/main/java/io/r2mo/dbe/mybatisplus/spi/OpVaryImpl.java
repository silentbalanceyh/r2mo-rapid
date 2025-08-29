package io.r2mo.dbe.mybatisplus.spi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.r2mo.base.dbe.operation.OpVary;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.dbe.common.operation.AbstractDbOperation;
import io.r2mo.typed.process.Pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author lang : 2025-08-28
 */
class OpVaryImpl<T, M extends BaseMapper<T>> extends AbstractDbOperation<QueryWrapper<T>, T, M> implements OpVary<T, QueryWrapper<T>> {
    OpVaryImpl(final Class<T> entityCls, final M m) {
        super(entityCls, m);
    }

    @Override
    public Pagination<T> queryPage(final QQuery query) {
        if (Objects.isNull(query)) {
            return new Pagination<>();
        }
        // 分页基础
        final IPage<T> pager = this.analyzer().page(query);
        // 条件 / 排序
        final QueryWrapper<T> condition = this.analyzer().where(query);
        // 查询
        final IPage<T> result = this.executor().selectPage(pager, condition);
        // 构造分页结果
        final Pagination<T> pagination = new Pagination<>();
        pagination.setCount(result.getTotal());
        pagination.setList(result.getRecords());
        return pagination;
    }

    @Override
    public boolean removeBy(final QueryWrapper<T> condition) {
        if (Objects.isNull(condition)) {
            return false;
        }
        this.executor().delete(condition);
        return true;
    }

    @Override
    public List<T> queryMany(final QueryWrapper<T> condition) {
        if (Objects.isNull(condition)) {
            return new ArrayList<>();
        }
        return this.executor().selectList(condition);
    }

    @Override
    public Optional<T> queryOne(final QueryWrapper<T> condition) {
        if (Objects.isNull(condition)) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.executor().selectOne(condition));
    }

    @Override
    public boolean removeById(final Serializable id) {
        if (Objects.isNull(id)) {
            return false;
        }
        this.executor().deleteById(id);
        return true;
    }

    @Override
    public T save(final Optional<T> queried, final T latest) {
        if (queried.isPresent()) {
            // UPDATE
            return this.update(queried, latest);
        } else {
            // INSERT
            this.executor().insert(latest);
            return latest;
        }
    }

    @Override
    public T update(final Optional<T> queried, final T latest) {
        if (queried.isPresent()) {
            // UPDATE
            final T waiting = queried.get();
            BeanUtil.copyProperties(latest, waiting, CopyOptions.create().ignoreNullValue());
            this.executor().updateById(waiting);
            return waiting;
        }
        return null;
    }
}
