package io.r2mo.vertx.jooq.shared.internal.jdbc;

import io.r2mo.vertx.jooq.shared.internal.AbstractQueryResult;
import io.r2mo.vertx.jooq.shared.internal.QueryResult;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author jensklingsporn
 */
public class JDBCQueryResult extends AbstractQueryResult {

    private final Result<? extends Record> result;
    private final int index;

    public JDBCQueryResult(final Result<? extends Record> result) {
        this(result, 0);
    }

    private JDBCQueryResult(final Result<? extends Record> result, final int index) {
        this.result = result;
        this.index = index;
    }


    @Override
    public <T> T get(final Field<T> field) {
        return this.supplyOrThrow(() -> this.result.getValue(this.index, field));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(final int index, final Class<T> type) {
        return this.supplyOrThrow(() -> (T) this.result.getValue(this.index, index));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(final String columnName, final Class<T> type) {
        return this.supplyOrThrow(() -> (T) this.result.getValue(this.index, columnName));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap() {
        return (T) this.result;
    }

    @Override
    public boolean hasResults() {
        return this.result.size() > 0;
    }

    @Override
    public Stream<QueryResult> stream() {
        return IntStream
            .range(this.index, this.result.size())
            .mapToObj(i -> new JDBCQueryResult(this.result, i));
    }
}
