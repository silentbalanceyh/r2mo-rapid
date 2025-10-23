package io.r2mo.dbe.mybatisplus.spi;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.SourceReflect;
import io.r2mo.base.dbe.join.DBAlias;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.operation.OpJoin;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author lang : 2025-10-23
 */
@Slf4j
class OpJoinImpl<T, M extends MPJBaseMapper<T>> extends OpJoinPre<T> implements OpJoin<T, MPJQueryWrapper<T>> {

    private final M executor;
    private final OpJoinAnalyzer<T> joinAnalyzer;
    private final OpJoinWriter<T, M> writer;

    OpJoinImpl(final DBRef ref, final M executor) {
        super(ref);
        this.executor = executor;
        this.writer = new OpJoinWriter<>(ref, executor);
        this.joinAnalyzer = new OpJoinAnalyzer<>(ref);
    }

    @Override
    public JArray findMany(final MPJQueryWrapper<T> queryWrapper) {
        this.postSelect(queryWrapper);
        final List<Map<String, Object>> rows = this.executor.selectJoinMaps(queryWrapper);
        return this.toResponse(rows);
    }


    @Override
    public JObject findOne(final MPJQueryWrapper<T> queryWrapper) {
        this.postSelect(queryWrapper);
        final Map<String, Object> row = this.executor.selectJoinMap(queryWrapper);
        return this.toResponse(row);
    }

    @Override
    public JObject findPage(final QQuery query) {
        // 构造 PAGE 条件，此处已包含了 projection 部分的过滤
        final MPJQueryWrapper<T> queryWrapper = this.joinAnalyzer.where(query);
        this.postSelect(queryWrapper);

        // 构造 分页 参数
        final IPage<Map<String, Object>> page = this.joinAnalyzer.page(query);

        final IPage<Map<String, Object>> result = this.executor.selectJoinMapsPage(page, queryWrapper);

        return this.toResponse(result);
    }

    @Override
    public JObject findById(final Serializable id) {
        // 构造 ID 条件
        final MPJQueryWrapper<T> queryWrapper = this.whereId(id);
        // 构造查询结果
        this.postSelect(queryWrapper);
        // 执行查询
        final Map<String, Object> row = this.executor.selectJoinMap(queryWrapper);
        // 返回响应结果
        return this.toResponse(row);
    }

    @Override
    public Optional<Long> count(final MPJQueryWrapper<T> queryWrapper) {
        return Optional.ofNullable(this.executor.selectJoinCount(queryWrapper));
    }

    @Override
    public JObject create(final JObject latest) {
        return this.writer.create(latest);
    }

    @Override
    public Boolean removeById(final Serializable id) {
        return this.writer.removeById(id);
    }

    @Override
    public Boolean removeBy(final MPJQueryWrapper<T> queryWrapper) {
        return this.writer.removeBy(queryWrapper);
    }

    @Override
    public JObject updateById(final Serializable id, final JObject latest) {
        return this.writer.updateById(id, latest);
    }

    @Override
    public JObject update(final MPJQueryWrapper<T> queryWrapper, final JObject latest) {
        return this.writer.update(queryWrapper, latest);
    }

    // ----- 私有方法
    private JArray toResponse(final List<Map<String, Object>> rows) {
        final JArray array = SPI.A();
        rows.stream().map(this::toResponse)
            .filter(Objects::nonNull)
            // 过滤之后解开 JObject / JArray 的封装
            .map(JBase::data)
            .forEach(array::add);
        return array;
    }

    private JObject toResponse(final IPage<Map<String, Object>> page) {
        final JObject pageJ = SPI.J();
        pageJ.put("count", page.getTotal());
        final List<Map<String, Object>> rows = page.getRecords();
        pageJ.put("list", this.toResponse(rows));
        return pageJ;
    }

    private JObject toResponse(final Map<String, Object> row) {
        final JObject record = SPI.J();
        for (final String column : row.keySet()) {
            if (this.ref.isAlias(column)) {
                // 别名处理，直接启用别名完成所有的事
                record.put(column, row.get(column));
                continue;
            }
            final Object value = row.get(column);


            // 列转属性，但还需要处理 Class<?> 的提取
            final Class<?> metaCls = this.ref.seekTypeByColumn(column);


            // 只有实现类可以这样检索
            final MetaTable<?> metaTable = this.metaMap.get(metaCls);
            final String vProperty = metaTable.vProperty(column);


            // 此处由于做的是 Json 的序列化，所以还需要计算一次
            final Field field = SourceReflect.fieldN(metaCls, vProperty);
            Objects.requireNonNull(field, "[ R2MO ] 此处属性必然不为空！");
            final JsonProperty jProperty = field.getDeclaredAnnotation(JsonProperty.class);
            if (Objects.isNull(jProperty)) {
                record.put(vProperty, value);
            } else {
                record.put(jProperty.value(), value);
            }
        }
        return record;
    }

    private void postSelect(final MPJQueryWrapper<T> queryWrapper) {
        final StringBuilder selectBuilder = new StringBuilder();
        if (StrUtil.isEmpty(queryWrapper.getSqlSelect())) {
            selectBuilder.append("*");
            // 未设置的时候执行此处的语句
        }
        final Set<String> aliasNames = this.ref.alias();
        if (!aliasNames.isEmpty()) {
            for (final String aliasName : aliasNames) {
                final DBAlias found = this.ref.alias(aliasName);


                // 提取 MyBatis-Plus 中的实体类，获取表级元数据
                final Class<?> entityCls = this.ref.seekType(found.table());
                final MetaTable<?> meta = this.metaMap.get(entityCls);


                // 提取表名别名
                final String tableAlias = this.ref.seekAlias(entityCls);


                // 根据属性查找列
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
