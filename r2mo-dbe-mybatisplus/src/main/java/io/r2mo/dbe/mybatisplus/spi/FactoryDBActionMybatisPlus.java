package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.r2mo.base.dbe.operation.OpAggr;
import io.r2mo.base.dbe.operation.OpDb;
import io.r2mo.base.dbe.operation.OpVary;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.base.dbe.operation.QrMany;
import io.r2mo.base.dbe.operation.QrOne;
import io.r2mo.dbe.common.operation.FactoryDBActionBase;

/**
 * @author lang : 2025-08-28
 */
@SuppressWarnings({"unchecked"})
public class FactoryDBActionMybatisPlus extends FactoryDBActionBase {

    @Override
    protected <T, EXECUTOR> OpAggr ofAggr(final Class<T> entityCls, final EXECUTOR executor) {
        return new OpAggrImpl<>(entityCls, (BaseMapper<T>) executor);
    }

    @Override
    protected Class<?> ofAggr() {
        return OpAggrImpl.class;
    }

    @Override
    protected <T, EXECUTOR> OpDb<T> ofDb(final Class<T> entityCls, final EXECUTOR executor) {
        return new OpDbImpl<>(entityCls, (BaseMapper<T>) executor);
    }

    @Override
    protected Class<?> ofDb() {
        return OpDbImpl.class;
    }

    @Override
    protected <T, EXECUTOR> QrOne<T> ofOne(final Class<T> entityCls, final EXECUTOR executor) {
        return new QrOneImpl<>(entityCls, (BaseMapper<T>) executor);
    }

    @Override
    protected Class<?> ofOne() {
        return QrOneImpl.class;
    }

    @Override
    protected <T, EXECUTOR> QrMany<T> ofMany(final Class<T> entityCls, final EXECUTOR executor) {
        return new QrManyImpl<>(entityCls, (BaseMapper<T>) executor);
    }

    @Override
    protected Class<?> ofMany() {
        return QrManyImpl.class;
    }

    @Override
    protected <T, EXECUTOR, CONDITION> OpVary<T, CONDITION> ofVary(final Class<T> entityCls, final EXECUTOR executor) {
        return (OpVary<T, CONDITION>) new OpVaryImpl<>(entityCls, (BaseMapper<T>) executor);
    }

    @Override
    protected Class<?> ofVary() {
        return OpVaryImpl.class;
    }

    @Override
    protected <T, EXECUTOR, CONDITION> QrAnalyzer<CONDITION> ofAnalyzer(final Class<T> entityCls, final EXECUTOR executor) {
        return (QrAnalyzer<CONDITION>) new QrAnalyzerImpl<>(entityCls);
    }

    @Override
    protected Class<?> ofAnalyzer() {
        return QrAnalyzerImpl.class;
    }
}