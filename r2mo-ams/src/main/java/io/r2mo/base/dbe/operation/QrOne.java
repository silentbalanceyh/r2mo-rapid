package io.r2mo.base.dbe.operation;

import io.r2mo.base.dbe.syntax.QTree;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author lang : 2025-08-28
 */
public interface QrOne<T> {

    Optional<T> execute(QTree syntax);

    Optional<T> execute(Serializable id);

    Optional<T> execute(String field, Object value);
}
