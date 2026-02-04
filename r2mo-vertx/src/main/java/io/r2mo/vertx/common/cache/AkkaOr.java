package io.r2mo.vertx.common.cache;

import io.r2mo.typed.webflow.Akka;
import io.vertx.core.Future;

import java.util.Objects;

public class AkkaOr<T> implements Akka<T> {

    private final Future<T> reference;

    public AkkaOr(final Future<T> reference) {
        if (Objects.isNull(reference)) {
            this.reference = Future.succeededFuture();
        } else {
            this.reference = reference;
        }
    }

    public static <T> Akka<T> of(final Future<T> reference) {
        return new AkkaOr<>(reference);
    }

    public static <T> Akka<T> of() {
        return new AkkaOr<>(Future.succeededFuture());
    }

    public static Akka<Boolean> of(final Boolean result) {
        return new AkkaOr<>(Future.succeededFuture(result));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V a() {
        return (V) this.reference;
    }

}
