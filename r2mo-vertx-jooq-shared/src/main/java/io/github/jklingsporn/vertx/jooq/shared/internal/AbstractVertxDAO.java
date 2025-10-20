package io.github.jklingsporn.vertx.jooq.shared.internal;

import io.vertx.core.impl.Arguments;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.InsertValuesStepN;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.jooq.impl.DSL.row;

/**
 * Abstract base class to reduce duplicate code in the different VertxDAO implementations.
 *
 * @param <R>                the <code>Record</code> type.
 * @param <P>                the POJO-type
 * @param <T>                the Key-Type
 * @param <FIND_MANY>        the result type returned for all findManyXYZ-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<List<P>>}.
 * @param <FIND_ONE>         the result type returned for all findOneXYZ-operations. This varies on the VertxDAO-subtypes , e.g. {@code Future<P>}.
 * @param <EXECUTE>          the result type returned for all insert, update and delete-operations. This varies on the VertxDAO-subtypes, e.g. {@code Future<Integer>}.
 * @param <INSERT_RETURNING> the result type returned for the insertReturning-operation. This varies on the VertxDAO-subtypes, e.g. {@code Future<T>}.
 */
public abstract class AbstractVertxDAO<R extends UpdatableRecord<R>, P, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> implements GenericVertxDAO<R, P, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> {

    private final Class<P> type;
    private final Table<R> table;
    private final QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor;

    protected AbstractVertxDAO(final Table<R> table, final Class<P> type, final QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor) {
        this.type = type;
        this.table = table;
        this.queryExecutor = queryExecutor;
    }

    public Class<P> getType() {
        return this.type;
    }

    public Table<R> getTable() {
        return this.table;
    }

    @Override
    public QueryExecutor<R, T, FIND_MANY, FIND_ONE, EXECUTE, INSERT_RETURNING> queryExecutor() {
        return this.queryExecutor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EXECUTE update(final P object) {
        Objects.requireNonNull(object);
        return this.queryExecutor().execute(dslContext -> {
            final R rec = dslContext.newRecord(this.getTable(), object);
            Condition where = DSL.trueCondition();
            final UniqueKey<R> pk = this.getTable().getPrimaryKey();
            for (final TableField<R, ?> tableField : pk.getFields()) {
                //exclude primary keys from update
                rec.changed(tableField, false);
                where = where.and(((TableField<R, Object>) tableField).eq(rec.get(tableField)));
            }
            final Map<String, Object> valuesToUpdate =
                Arrays.stream(rec.fields())
                    .collect(HashMap::new, (m, f) -> m.put(f.getName(), f.getValue(rec)), HashMap::putAll);
            return dslContext
                .update(this.getTable())
                .set(valuesToUpdate)
                .where(where);
        });
    }

    private Function<DSLContext, SelectConditionStep<R>> selectQuery(final Condition condition) {
        return dslContext -> dslContext.selectFrom(this.getTable()).where(condition);
    }

    @Override
    public FIND_MANY findManyByCondition(final Condition condition) {
        return this.queryExecutor().findMany(this.selectQuery(condition));
    }

    @Override
    public FIND_MANY findManyByCondition(final Condition condition, final int limit) {
        return this.queryExecutor().findMany(this.selectQuery(condition).andThen(sel -> sel.limit(limit)));
    }

    @Override
    public FIND_MANY findManyByCondition(final Condition condition, final OrderField<?>... orderField) {
        return this.queryExecutor().findMany(this.selectQuery(condition).andThen(sel -> sel.orderBy(orderField)));
    }

    @Override
    public FIND_MANY findManyByCondition(final Condition condition, final int limit, final OrderField<?>... orderField) {
        return this.queryExecutor().findMany(this.selectQuery(condition).andThen(sel -> sel.orderBy(orderField).limit(limit)));
    }

    @Override
    public FIND_MANY findManyByCondition(final Condition condition, final int limit, final int offset, final OrderField<?>... orderFields) {
        return this.queryExecutor().findMany(this.selectQuery(condition).andThen(sel -> sel.orderBy(orderFields).limit(offset, limit)));
    }

    @Override
    public FIND_MANY findManyByIds(final Collection<T> ids) {
        return this.findManyByCondition(this.equalKeys(ids));
    }

    @Override
    public FIND_MANY findAll() {
        return this.findManyByCondition(DSL.trueCondition());
    }

    @Override
    public FIND_ONE findOneById(final T id) {
        return this.findOneByCondition(this.equalKey(id));
    }

    @Override
    public FIND_ONE findOneByCondition(final Condition condition) {
        return this.queryExecutor().findOne(dslContext -> dslContext.selectFrom(this.getTable()).where(condition));
    }

    @Override
    public EXECUTE deleteByCondition(final Condition condition) {
        return this.queryExecutor().execute(dslContext -> dslContext.deleteFrom(this.getTable()).where(condition));
    }

    @Override
    public EXECUTE deleteById(final T id) {
        return this.deleteByCondition(this.equalKey(id));
    }

    @Override
    public EXECUTE deleteByIds(final Collection<T> ids) {
        return this.deleteByCondition(this.equalKeys(ids));
    }

    @Override
    public EXECUTE insert(final P pojo) {
        return this.insert(pojo, false);
    }

    @Override
    public EXECUTE insert(final P pojo, final boolean onDuplicateKeyIgnore) {
        Objects.requireNonNull(pojo);
        return this.queryExecutor().execute(dslContext -> {
            final InsertSetMoreStep<R> insertStep = dslContext.insertInto(this.getTable()).set(this.newRecord(dslContext, pojo));
            return onDuplicateKeyIgnore ? insertStep.onDuplicateKeyIgnore() : insertStep;
        });
    }

    @Override
    public EXECUTE insert(final Collection<P> pojos) {
        return this.insert(pojos, false);
    }

    @Override
    public EXECUTE insert(final Collection<P> pojos, final boolean onDuplicateKeyIgnore) {
        Arguments.require(!pojos.isEmpty(), "No elements");
        return this.queryExecutor().execute(dslContext -> {
            final InsertSetStep<R> insertSetStep = dslContext.insertInto(this.getTable());
            InsertValuesStepN<R> insertValuesStepN = null;
            for (final P pojo : pojos) {
                insertValuesStepN = insertSetStep.values(this.newRecord(dslContext, pojo).intoArray());
            }
            return onDuplicateKeyIgnore ? insertValuesStepN.onDuplicateKeyIgnore() : insertValuesStepN;
        });
    }

    @Override
    public INSERT_RETURNING insertReturningPrimary(final P object) {
        final UniqueKey<?> key = this.getTable().getPrimaryKey();
        //usually key shouldn't be null because DAO generation is omitted in such cases
        Objects.requireNonNull(key, () -> "No primary key");
        return this.queryExecutor().insertReturning(
            dslContext -> dslContext.insertInto(this.getTable()).set(this.newRecord(dslContext, object)).returning(key.getFields()),
            this.keyConverter());
    }

    @SuppressWarnings("unchecked")
    protected Function<Object, T> keyConverter() {
        return record -> {
            Objects.requireNonNull(record, () -> "Failed inserting record or no key");
            final Record key1 = ((R) record).key();
            if (key1.size() == 1) {
                return ((Record1<T>) key1).value1();
            }
            return (T) key1;
        };
    }

    @SuppressWarnings("unchecked")
    protected Condition equalKey(final T id) {
        final UniqueKey<?> uk = this.getTable().getPrimaryKey();
        Objects.requireNonNull(uk, () -> "No primary key");
        /**
         * Copied from jOOQs DAOImpl#equal-method
         */
        final TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        final Condition condition;
        if (pk.length == 1) {
            condition = ((Field<Object>) pk[0]).equal(pk[0].getDataType().convert(id));
        } else {
            condition = row(pk).equal((Record) id);
        }
        return condition;
    }

    @SuppressWarnings("unchecked")
    protected Condition equalKeys(final Collection<T> ids) {
        final UniqueKey<?> uk = this.getTable().getPrimaryKey();
        Objects.requireNonNull(uk, () -> "No primary key");
        /**
         * Copied from jOOQs DAOImpl#equal-method
         */
        final TableField<? extends Record, ?>[] pk = uk.getFieldsArray();
        final Condition condition;
        if (pk.length == 1) {
            if (ids.size() == 1) {
                condition = this.equalKey(ids.iterator().next());
            } else {
                condition = pk[0].in(pk[0].getDataType().convert(ids));
            }
        } else {
            condition = row(pk).in(ids.toArray(new Record[ids.size()]));
        }
        return condition;
    }

    @SuppressWarnings("unchecked")
    protected /* non-final */ T compositeKeyRecord(final Object... values) {
        final UniqueKey<R> key = this.table.getPrimaryKey();
        if (key == null) {
            return null;
        }

        final TableField<R, Object>[] fields = (TableField<R, Object>[]) key.getFieldsArray();
        final Record result = DSL.using(this.queryExecutor.configuration())
            .newRecord(fields);

        for (int i = 0; i < values.length; i++) {
            result.set(fields[i], fields[i].getDataType().convert(values[i]));
        }

        return (T) result;
    }

    /**
     * @param dslContext the {@link DSLContext}
     * @param pojo       the pojo
     *
     * @return a new {@code Record} based on the pojo.
     */
    protected Record newRecord(final DSLContext dslContext, final P pojo) {
        return this.setDefault(dslContext.newRecord(this.getTable(), pojo));
    }

    /**
     * Defaults fields that have a default value and are nullable.
     *
     * @param record the record
     *
     * @return the record
     */
    private Record setDefault(final Record record) {
        final int size = record.size();
        for (int i = 0; i < size; i++) {
            if (record.get(i) == null) {
                @SuppressWarnings("unchecked") final Field<Object> field = (Field<Object>) record.field(i);
                if (!field.getDataType().nullable() && !field.getDataType().identity()) {
                    record.set(field, DSL.defaultValue());
                }
            }
        }
        return record;
    }

    protected abstract T getId(P object);
}
