package io.r2mo.base.dbe.operation;

import io.r2mo.base.dbe.syntax.QQuery;
import io.r2mo.base.dbe.syntax.QTree;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lang : 2025-08-28
 */
public interface QrMany<T> {

    List<T> execute(QQuery query);

    List<T> execute(QTree tree);

    List<T> execute(Serializable... ids);

    List<T> execute(String field, Object value);

    List<T> execute(Map<String, Object> condition);
}
