package io.r2mo.dbe.mybatisplus.spi;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.common.DBAlias;
import io.r2mo.base.dbe.common.DBFor;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.common.DBResult;
import io.r2mo.base.dbe.operation.OpJoin;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.dbe.mybatisplus.JoinProxy;
import io.r2mo.spi.FactoryDBAction;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author lang : 2025-10-23
 */
@Slf4j
public class OpJoinImpl<T, M extends MPJBaseMapper<T>> implements OpJoin<T, MPJQueryWrapper<T>> {

    private final M executor;
    private final OpJoinAnalyzer<T> analyzer;
    private final DBRef ref;
    // 后置构造，等待执行完成后才启动的构造器，且构造时会传入 executor 之外的其他 executor
    private OpJoinWriter<T> writer;

    OpJoinImpl(final DBRef ref, final M executor) {
        this.ref = ref;
        this.executor = executor;
        this.analyzer = new OpJoinAnalyzer<>(ref);
    }

    /**
     * 此处的后期构造主要用于
     * <pre>
     *     抽象层   {@link FactoryDBAction} / {@link OpJoin}
     *
     *                                       {@link OpJoinImpl} -> {@link OpJoinWriter}
     *
     *                                       M = {@link MPJBaseMapper} 类型
     *     由于整个继承树上的类型是 MPJBaseMapper<T> 类型的执行器，所以无法直接将第二执行器或第三执行器注入到 {@link OpJoinImpl}
     *     中去完成写操作的支持，所以只能通过此处的后置构造来实现注入过程，确保{@link OpJoinWriter} 能够拿到完整的执行器集合，从而
     *     完成写操作，并且在执行写之前会执行相关检查来确保写操作是合法的。
     * </pre>
     *
     * @param joinProxy JoinProxy
     */
    public void afterConstruct(final JoinProxy<T> joinProxy) {
        // 此处设置是在构造阶段，所以不会出现先调用 this.writer() 的场景
        this.writer = new OpJoinWriter<>(this.ref, joinProxy);
    }

    private OpJoinWriter<T> writer() {
        return this.writer;
    }

    @Override
    public JArray findMany(final MPJQueryWrapper<T> queryWrapper) {
        this.postSelect(queryWrapper);
        final List<Map<String, Object>> rows = this.executor.selectJoinMaps(queryWrapper);
        return DBResult.of(this.ref).build(rows);
    }


    @Override
    public JObject findOne(final MPJQueryWrapper<T> queryWrapper) {
        this.postSelect(queryWrapper);
        final Map<String, Object> row = this.executor.selectJoinMap(queryWrapper);
        return DBResult.of(this.ref).build(row);
    }

    @Override
    public JObject findPage(final QQuery query) {
        // 构造 PAGE 条件，此处已包含了 projection 部分的过滤
        final MPJQueryWrapper<T> queryWrapper = this.analyzer.where(query);
        this.postSelect(queryWrapper);

        // 构造 分页 参数
        final IPage<Map<String, Object>> page = this.analyzer.page(query);

        final IPage<Map<String, Object>> result = this.executor.selectJoinMapsPage(page, queryWrapper);

        return this.toResponse(result);
    }

    @Override
    public JObject findById(final Serializable id) {
        // 构造 ID 条件
        final MPJQueryWrapper<T> queryWrapper = this.analyzer.whereId(id);
        // 构造查询结果
        this.postSelect(queryWrapper);
        // 执行查询
        final Map<String, Object> row = this.executor.selectJoinMap(queryWrapper);
        // 返回响应结果
        return DBResult.of(this.ref).build(row);
    }

    @Override
    public Optional<Long> count(final MPJQueryWrapper<T> queryWrapper) {
        return Optional.ofNullable(this.executor.selectJoinCount(queryWrapper));
    }

    @Override
    public JObject create(final JObject latest) {
        return this.writer().create(latest);
    }

    @Override
    public Boolean removeById(final Serializable id) {
        final JObject stored = this.findById(id);
        return this.writer().removeBy(stored);
    }

    @Override
    public Boolean removeBy(final MPJQueryWrapper<T> queryWrapper) {
        final JObject stored = this.findOne(queryWrapper);
        return this.writer().removeBy(stored);
    }

    @Override
    public JObject updateById(final Serializable id, final JObject latest) {
        // 原始数据
        final JObject stored = this.findById(id);
        // 数据压缩
        final JObject compressed = DBFor.ofFilter().exchange(latest, this.ref);
        // 合并数据
        compressed.fieldNames()
            .forEach(field -> stored.put(field, compressed.get(field)));

        return this.writer().update(stored);
    }

    @Override
    public JObject update(final MPJQueryWrapper<T> queryWrapper, final JObject latest) {
        // 原始数据
        final JObject stored = this.findOne(queryWrapper);
        // 数据压缩
        final JObject compressed = DBFor.ofFilter().exchange(latest, this.ref);
        // 合并数据
        compressed.fieldNames()
            .forEach(field -> stored.put(field, compressed.get(field)));

        return this.writer().update(stored);
    }

    // ----- 私有方法

    private JObject toResponse(final IPage<Map<String, Object>> page) {
        final JObject pageJ = SPI.J();
        pageJ.put("count", page.getTotal());
        final List<Map<String, Object>> rows = page.getRecords();
        pageJ.put("list", DBResult.of(this.ref).build(rows));
        return pageJ;
    }

    private void postSelect(final MPJQueryWrapper<T> queryWrapper) {
        final StringBuilder selectBuilder = new StringBuilder();
        if (StrUtil.isEmpty(queryWrapper.getSqlSelect())) {
            selectBuilder.append("*");
            // 未设置的时候执行此处的语句
        }
        final Set<String> aliasNames = this.ref.findAlias();
        if (!aliasNames.isEmpty()) {
            for (final String aliasName : aliasNames) {
                final DBAlias found = this.ref.findAlias(aliasName);


                // 提取 MyBatis-Plus 中的实体类，获取表级元数据
                final Class<?> entityCls = this.ref.seekType(found.table());


                // 提取表名别名
                final String tableAlias = this.ref.seekAlias(entityCls);


                // 根据属性查找列
                final DBNode meta = this.ref.findBy(entityCls);
                final String column = meta.vColumn(found.name());
                // SELECT *, TRX.{COLUMN} AS {aliasName} ( 大写 )
                selectBuilder.append(",").append(tableAlias).append(".").append(column)
                    .append(" AS ").append(aliasName);
            }
            log.info("[ R2MO ] SELECT 语句部分 = {}", selectBuilder);
        }
        queryWrapper.select(selectBuilder.toString());
    }
}
