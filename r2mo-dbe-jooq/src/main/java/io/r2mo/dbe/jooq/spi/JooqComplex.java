package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.syntax.QPager;
import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QSorter;
import io.r2mo.base.dbe.syntax.QTree;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import io.r2mo.typed.common.Pagination;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.jooq.SelectConditionStep;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectWhereStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-18
 */
class JooqComplex {
    private final JooqMeta meta;
    private final DSLContext context;
    private final QrAnalyzerJooq qr;

    JooqComplex(final Class<?> entityCls, final DSLContext context) {
        this.context = context;
        this.meta = LoadREF.of().loadMeta(entityCls);
        this.qr = new QrAnalyzerJooq(entityCls, context);
        Objects.requireNonNull(this.meta, "[ R2MO ] 无法从实体类中提取元数据：" + entityCls.getName());
    }

    private SelectWhereStep<?> selectFor() {
        return this.context.selectFrom(this.meta.table());
    }

    public SelectForUpdateStep<?> where(final Map<String, Object> condition) {
        return this.selectFor().where(this.qr.where(condition));
    }

    public SelectForUpdateStep<?> where(final String field, final Object value) {
        return this.selectFor().where(this.qr.where(field, value));
    }

    public SelectForUpdateStep<?> where(final QTree tree, final QSorter sorter) {
        final SelectConditionStep<?> stepQr = this.selectFor(tree);

        return this.selectFor(stepQr, sorter);
    }

    private SelectConditionStep<?> selectFor(final QTree tree) {

        final Condition condition = this.qr.where(tree, null /* 排序本来在此处就没使用，传 null 不影响 */);

        return this.selectFor().where(condition);
    }

    private SelectLimitStep<?> selectFor(final SelectConditionStep<?> stepQr, final QSorter sorter) {

        if (Objects.isNull(sorter)) {
            // 无排序
            return stepQr;
        }

        final List<OrderField<?>> orderBy = JooqHelper.forOrderBy(sorter, this.meta::findColumn, null);

        return stepQr.orderBy(orderBy);
    }

    private SelectForUpdateStep<?> selectFor(final SelectLimitStep<?> stepQr, final QPager pager) {
        if (Objects.isNull(pager)) {
            return stepQr;
        }

        return stepQr.offset(pager.getStart()).limit(pager.getSize());
    }

    public SelectForUpdateStep<?> where(final QQuery query) {
        final SelectConditionStep<?> stepQr = this.selectFor(query.criteria());

        final SelectLimitStep<?> stepOrders = this.selectFor(stepQr, query.sorter());

        return this.selectFor(stepOrders, query.pager());
    }

    @SuppressWarnings("unchecked")
    public <PAGE> PAGE page(final QQuery query) {
        final Pagination<?> pagination = new Pagination<>();

        final SelectConditionStep<?> stepQr = this.selectFor(query.criteria());
        /*
         * FIX-DBE: 分页数量问题
         * 注意，此处 stepQr 在后续计算过程中会引起条件本身的修改而导致副作用，这种模式下，会在
         * stepQr.fetchStream() 的时候追加 SelectLimitStep 和 SelectForUpdateStep，主要是此处不可以包含
         * Limit 参数，否则会导致 count 结果不正确，最终导致分页结果不正确
         */
        final SelectConditionStep<?> countQr = this.selectFor(query.criteria());

        final SelectLimitStep<?> stepOrders = this.selectFor(stepQr, query.sorter());

        final SelectForUpdateStep<?> stepFinal = this.selectFor(stepOrders, query.pager());

        final List<?> totalList = stepFinal.fetchInto(this.meta.entityCls());
        pagination.setList(this.toList(totalList));
        pagination.setCount(countQr.fetchStream().count());
        return (PAGE) pagination;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> toList(final List<?> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
            .map(item -> (T) item)
            .collect(Collectors.toList());
    }
}
