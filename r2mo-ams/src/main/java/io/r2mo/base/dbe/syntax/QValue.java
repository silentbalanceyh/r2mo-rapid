package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QOp;

/**
 * @author lang : 2025-08-28
 */
class QValue implements QLeaf {

    private final QOp op;
    private final String field;

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

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String field() {
        return this.field;
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
}
