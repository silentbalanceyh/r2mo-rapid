package io.r2mo.base.dbe.constant;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-08-28
 */
public enum QOp {

    AND(QCV.Connector.AND.name()),
    OR(QCV.Connector.OR.name()),

    EQ(QCV.Op.EQ),
    NEQ(QCV.Op.NEQ),
    GT(QCV.Op.GT),
    GTE(QCV.Op.GTE),
    LT(QCV.Op.LT),
    LTE(QCV.Op.LTE),
    NULL(QCV.Op.NULL),
    NOT_NULL(QCV.Op.NOT_NULL),
    IN(QCV.Op.IN),
    NOT_IN(QCV.Op.NOT_IN),
    START(QCV.Op.START),
    END(QCV.Op.END),
    CONTAIN(QCV.Op.CONTAIN);

    private static final ConcurrentMap<String, QOp> MAP = new ConcurrentHashMap<>() {
        {
            for (final QOp op : QOp.values()) {
                this.put(op.value(), op);
            }
        }
    };

    private final String value;

    QOp(final String value) {
        this.value = value;
    }

    public static QOp toOp(final String opStr) {
        if (!MAP.containsKey(opStr)) {
            throw new IllegalArgumentException("[ R2MO ] 不支持的枚举字面量: " + opStr);
        }
        return MAP.getOrDefault(opStr, null);
    }

    public String value() {
        return this.value;
    }
}
