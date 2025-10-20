package io.r2mo.vertx.jooq.generate.mutiny;

import io.r2mo.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.r2mo.vertx.jooq.generate.builder.PredefinedNamedInjectionStrategy;
import io.r2mo.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class MutinyReactiveGuiceVertxGenerator extends DelegatingVertxGenerator {

    public MutinyReactiveGuiceVertxGenerator() {
        super(VertxGeneratorBuilder.init().withMutinyAPI().withPostgresReactiveDriver().withGuice(true, PredefinedNamedInjectionStrategy.DISABLED).build());
    }
}
