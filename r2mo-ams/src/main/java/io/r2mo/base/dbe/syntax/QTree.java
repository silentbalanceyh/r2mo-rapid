package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.constant.QOp;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;

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
     * 初始化入口
     * {@link QBranch} / {@link QLeaf}
     */
    private QNode initialize(final JObject syntaxJ, final int level) {
        final QNode root = this.initialize(syntaxJ);
        root.level(level);
        syntaxJ.itKv().filter(field -> !"".equals(field.getKey())).forEach(entry -> {
            final String field = entry.getKey();
            final Object value = entry.getValue();
            final QNode branch = this.initialize(field, value, level + 1);
            if (!root.isLeaf()) {
                ((QBranch) root).add(branch);
            }
        });
        return root;
    }

    /**
     * {@link QBranch} / {@link QLeaf}
     */
    private QNode initialize(final String field, final Object value, final int level) {
        final QNode node;
        // 此处判断要调整
        final JUtil ut = SPI.V_UTIL;
        if (ut.isJObject(value)) {
            final JObject branchJ = ut.toJObject(value);
            node = this.initialize(branchJ, level);
        } else {
            node = this.initialize(field, value).level(level);
        }
        return node;
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
        // 判断是否复杂逻辑
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

    @Override
    public String toString() {
        return "-----QTree-----\n" + this.root.dgInfo();
    }
}
