package io.r2mo.vertx.jooq.generate.mutiny;

import io.r2mo.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.r2mo.vertx.jooq.generate.builder.PredefinedNamedInjectionStrategy;
import io.r2mo.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class MutinyJDBCGuiceVertxGenerator extends DelegatingVertxGenerator {

    public MutinyJDBCGuiceVertxGenerator() {
        super(VertxGeneratorBuilder.init().withMutinyAPI().withJDBCDriver().withGuice(true, PredefinedNamedInjectionStrategy.DISABLED).build());
    }
}
