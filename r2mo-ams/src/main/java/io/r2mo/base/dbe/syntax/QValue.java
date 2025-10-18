package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QOp;
import io.r2mo.base.util.R2MO;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author lang : 2025-08-28
 */
public class QValue implements QLeaf {

    private final QOp op;
    private final String field;
    private String mark;    // 第三位标记，出现了 startAt,<,day 中第三位会出现数据转换，主要针对时间格式

    private final Object value;
    private Integer level = 0;

    private QValue(final String field, final QOp op, final Object value) {
        this.field = field;
        this.op = null == op ? QOp.EQ : op;
        this.value = value;
    }

    static QValue of(final String field,
                     final QOp op,
                     final Object value) {
        return new QValue(field, op, value);
    }

    public QValue mark(final String mark) {
        this.mark = mark;
        return this;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String field() {
        return this.field;
    }

    @Override
    public String mark() {
        return this.mark;
    }

    @Override
    public QOp op() {
        return this.op;
    }

    @Override
    public QNode level(final int level) {
        this.level = level;
        return this;
    }

    @Override
    public Object value() {
        return this.value;
    }

    @Override
    public String dgInfo() {
        return "\t".repeat(Math.max(0, this.level)) +
            "Leaf" + "," +
            "( " + this.field + " , " +
            this.op + " , " +
            this.value + " ) ";
    }

    // -------------- 特殊场景一定会用到的方法
    public LocalDate toDate() {
        return this.toDateInternal(R2MO::toDate);
    }

    public LocalDateTime toDateTime() {
        return this.toDateInternal(R2MO::toDateTime);
    }

    public LocalTime toTime() {
        return this.toDateInternal(R2MO::toTime);
    }

    public Collection<?> toCollection() {
        return R2MO.toCollection(this.value);
    }

    private <T> T toDateInternal(final Function<Instant, T> convertFn) {
        if (Objects.isNull(this.value)) {
            return null;
        }
        final Date normalized = R2MO.parseFull(this.value.toString());
        return convertFn.apply(normalized.toInstant());
    }
}
