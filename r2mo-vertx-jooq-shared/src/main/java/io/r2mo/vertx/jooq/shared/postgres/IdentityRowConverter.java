package io.r2mo.vertx.jooq.shared.postgres;

import org.jooq.Converter;
import org.jooq.impl.IdentityConverter;

/**
 * Mostly the type of the reactive client and jooq are the same. You can use this class for that purpose.
 *
 * @author jensklingsporn
 */
public final class IdentityRowConverter<T> implements RowConverter<T, T> {

    private final IdentityConverter<T> delegate;

    public IdentityRowConverter(final Class<T> clazz) {
        this.delegate = new IdentityConverter<>(clazz);
    }

    @Override
    public Class<T> fromType() {
        return this.delegate.fromType();
    }

    @Override
    public Class<T> toType() {
        return this.delegate.toType();
    }

    @Override
    public Converter<T, T> inverse() {
        return this.delegate.inverse();
    }

    @Override
    public T from(final T t) {
        return this.delegate.from(t);
    }

    @Override
    public T to(final T t) {
        return this.delegate.to(t);
    }


}
