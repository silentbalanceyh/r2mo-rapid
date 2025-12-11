package io.r2mo.vertx.function;

import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2023-06-07
 */
@SuppressWarnings("all")
class FnCombine {
    private FnCombine() {
    }

    static <T> Future<List<T>> combineT(final List<Future<T>> futures) {
        final List<Future<?>> futureList = new ArrayList<>(futures);
        return Future.join(futureList).compose(finished -> {
            final List<T> result = new ArrayList<>();
            finished.list().stream().filter(Objects::nonNull)
                .map(item -> (T) item).forEach(result::add);
            return Future.succeededFuture(result);
        }).otherwise(FnOut.otherwiseFn(ArrayList::new));
    }

    static <T> Future<Set<T>> combineT(final Set<Future<T>> futures) {
        final List<Future<?>> futureList = new ArrayList<>(futures);
        return Future.join(futureList).compose(finished -> {
            final Set<T> result = new HashSet<>();
            finished.list().stream().filter(Objects::nonNull)
                .map(item -> (T) item).forEach(result::add);
            return Future.succeededFuture(result);
        }).otherwise(FnOut.otherwiseFn(HashSet::new));
    }


    static <F, S, T> Future<T> combineT(final Supplier<Future<F>> futureF, final Supplier<Future<S>> futureS,
                                        final BiFunction<F, S, Future<T>> consumer) {
        return futureF.get().compose(f -> futureS.get().compose(s -> consumer.apply(f, s)));
    }

    static <F, S, T> Future<T> combineT(final Supplier<Future<F>> futureF, final Function<F, Future<S>> futureS,
                                        final BiFunction<F, S, Future<T>> consumer) {
        return futureF.get().compose(f -> futureS.apply(f).compose(s -> consumer.apply(f, s)));
    }
}
