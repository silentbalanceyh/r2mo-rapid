package io.r2mo.vertx.jooq.generate.rx;

import io.r2mo.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.r2mo.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class RXJDBCVertxGenerator extends DelegatingVertxGenerator {

    public RXJDBCVertxGenerator() {
        super(VertxGeneratorBuilder.init().withRXAPI().withJDBCDriver().build());
    }
}
