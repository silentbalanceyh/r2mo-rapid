package io.r2mo.base.dbe.syntax;

import java.util.Set;

/**
 * @author lang : 2025-08-28
 */
public interface QBranch extends QNode {
    Set<QNode> nodes();

    QBranch add(QNode node);
}
