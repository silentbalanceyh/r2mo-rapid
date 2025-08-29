package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QOp;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-08-28
 */
class QTier implements QBranch {
    private final QOp op;

    private final Set<QNode> nodes = new HashSet<>();

    private int level = 0;

    private QTier(final QOp op) {
        this.op = Objects.isNull(op) || QOp.AND == op ? QOp.AND : QOp.OR;
    }

    static QTier of(final QOp op) {
        return new QTier(op);
    }

    @Override
    public Set<QNode> nodes() {
        return this.nodes;
    }

    @Override
    public QBranch add(final QNode node) {
        this.nodes.add(node);
        return this;
    }

    @Override
    public QOp op() {
        return this.op;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public QNode level(final int level) {
        this.level = level;
        return this;
    }

    @Override
    public String dgInfo() {
        final StringBuilder source = new StringBuilder();
        source.append("\t".repeat(Math.max(0, this.level)));
        source.append("Branch").append(",");
        source.append("OP：").append(this.op).append(",");
        source.append("Node：").append("\n");
        this.nodes.forEach(node -> {
            source.append("\t".repeat(Math.max(0, this.level)));
            source.append(node).append("\n");
        });
        return source.toString();
    }
}
