package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.constant.QOp;
import io.r2mo.typed.json.JObject;

import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
public class QTree implements QRequest {

    private final QNode root;
    private final JObject syntaxJ;

    private QSorter sorter;

    private QTree(final JObject syntaxJ) {
        this.syntaxJ = syntaxJ;
        this.root = this.initialize(syntaxJ, 0);
    }

    public static QTree of(final JObject syntaxJ) {
        return new QTree(syntaxJ);
    }

    public QTree sortBy(final QSorter sorter) {
        this.sorter = sorter;
        return this;
    }

    public QSorter sortBy() {
        return this.sorter;
    }

    /**
     * {@link QBranch} / {@link QLeaf}
     */
    private QNode initialize(final String field, final Object value, final int level) {
        final QNode node;
        if (value instanceof final JObject branchJ) {
            node = this.initialize(branchJ, level);
        } else {
            node = this.initialize(field, value).level(level);
        }
        return node;
    }

    /**
     * {@link QBranch} / {@link QLeaf}
     */
    private QNode initialize(final JObject syntaxJ, final int level) {
        final QNode root = this.initialize(syntaxJ);
        syntaxJ.itKv((field, value) -> {
            final QNode branch = this.initialize(field, value, level + 1);
            if (!root.isLeaf()) {
                ((QBranch) root).add(branch);
            }
        });
        return root;
    }

    /**
     * {@link QLeaf}
     */
    private QNode initialize(final String field, final Object value) {
        if (field.contains(",")) {
            final String[] split = field.split(",");
            final String f = split[0].trim();
            final QOp op = QOp.toOp(split[1].trim());
            return QValue.of(f, op, value);
        } else {
            // 默认 =
            return QValue.of(field, QOp.EQ, value);
        }
    }

    /**
     * {@link QBranch}
     */
    private QNode initialize(final JObject syntaxJ) {
        final QOp connector;
        if (syntaxJ.containsKey("")) {
            final boolean isAnd = syntaxJ.getBool("", Boolean.TRUE);
            connector = isAnd ? QOp.AND : QOp.OR;
        } else {
            // 默认连接符
            connector = QOp.AND;
        }
        return QTier.of(connector);
    }

    @Override
    public String field() {
        return QCV.P_CRITERIA;
    }

    @Override
    @SuppressWarnings("all")
    public JObject data() {
        return this.syntaxJ;
    }

    @Override
    @SuppressWarnings("all")
    public QNode item() {
        return this.root;
    }

    @Override
    public boolean isOk() {
        if (this.root instanceof final QBranch branch) {
            return !branch.nodes().isEmpty();
        } else {
            return Objects.nonNull(this.root);
        }
    }
}
