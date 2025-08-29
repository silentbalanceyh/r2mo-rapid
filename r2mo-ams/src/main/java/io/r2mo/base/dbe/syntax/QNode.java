package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QOp;

/**
 * 语法树基础节点
 * <pre>
 *     如果是分支节点类型为 {@link QBranch}
 *     如果是叶节点类型为 {@link QLeaf}
 * </pre>
 *
 * @author lang : 2025-08-28
 */
public interface QNode {

    QOp op();

    boolean isLeaf();

    QNode level(int level);

    // ---------- 下边方法是为了调试
    String dgInfo();
}
