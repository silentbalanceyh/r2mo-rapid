package io.r2mo.typed.webflow;

public class AkkaOf<T> implements Akka<T> {

    private final T reference;

    private AkkaOf(final T reference) {
        this.reference = reference;
    }

    public static <T> Akka<T> of(final T reference) {
        return new AkkaOf<>(reference);
    }

    public static Akka<Void> of() {
        return new AkkaOf<>(null);
    }

    public static Akka<Boolean> of(final Boolean value) {
        return new AkkaOf<>(value);
    }

    @Override
    public T get() {
        return this.reference;
    }
}
