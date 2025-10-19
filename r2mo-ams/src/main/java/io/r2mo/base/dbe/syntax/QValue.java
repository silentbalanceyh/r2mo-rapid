package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QOp;
import io.r2mo.base.util.R2MO;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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


    public static QValue of(final String field, final String op, final Object value) {
        return new QValue(field, QOp.toOp(op), value);
    }

    /**
     * 直接根据 key = value 解析构建 QValue
     *
     * @param field 字段名，支持 field,op,mark 三位构建
     * @param value 值
     *
     * @return QValue 实例
     */
    public static QValue of(final String field, final Object value) {
        if (field.contains(",")) {
            final String[] split = field.split(",");
            final String f = split[0].trim();
            final QOp op = QOp.toOp(split[1].trim());
            final QValue qValue = new QValue(f, op, value);
            if (2 < split.length) {
                // 标记位设置
                qValue.mark(split[2].trim());
            }
            return qValue;
        } else {
            // 默认 =
            return new QValue(field, QOp.EQ, value);
        }
    }

    /**
     * 克隆一个 QValue 对象，并替换值
     *
     * @param qValue      原始 QValue
     * @param valueLatest 最新值
     *
     * @return 新的 QValue 对象
     */
    public static QValue of(final QValue qValue, final Object valueLatest) {
        Objects.requireNonNull(qValue, "[ R2MO ] 此方法要求 qValue 不能为 null");
        final QValue cloned = new QValue(qValue.field, qValue.op, valueLatest);
        cloned.mark = qValue.mark;
        cloned.level = qValue.level;
        return cloned;
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
