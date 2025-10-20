package io.r2mo.vertx.jooq.shared.internal;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author jensklingsporn
 */
public abstract class AbstractQueryResult implements QueryResult {

    /**
     *
     * @param supplier the supplier to return they type
     * @param <T>      the return type
     *
     * @return the value supplied by supplier or throw a NoSuchElementException if the
     * underlying result has no results.
     */
    protected <T> T supplyOrThrow(final Supplier<T> supplier) {
        if (this.hasResults()) {
            return supplier.get();
        }
        throw new NoSuchElementException("QueryResult is empty");
    }

    @Override
    public List<QueryResult> asList() {
        return this.stream()
            .collect(Collectors.toList());
    }

}
