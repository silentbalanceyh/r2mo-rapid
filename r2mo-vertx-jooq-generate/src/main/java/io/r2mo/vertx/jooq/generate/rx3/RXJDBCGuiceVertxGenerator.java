package io.r2mo.vertx.jooq.generate.rx3;

import io.r2mo.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.r2mo.vertx.jooq.generate.builder.PredefinedNamedInjectionStrategy;
import io.r2mo.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class RXJDBCGuiceVertxGenerator extends DelegatingVertxGenerator {

    public RXJDBCGuiceVertxGenerator() {
        super(VertxGeneratorBuilder.init().withRX3API().withJDBCDriver().withGuice(true, PredefinedNamedInjectionStrategy.DISABLED).build());
    }
}
