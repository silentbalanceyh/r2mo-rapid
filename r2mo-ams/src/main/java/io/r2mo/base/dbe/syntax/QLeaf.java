package io.r2mo.base.dbe.syntax;

/**
 * @author lang : 2025-08-28
 */
public interface QLeaf extends QNode {

    default Class<?> type() {
        return null;
    }

    default QLeaf type(final Class<?> type) {
        return this;
    }

    String field();

    Object value();

    String mark();
}
