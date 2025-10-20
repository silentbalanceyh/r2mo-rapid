package io.r2mo.vertx.jooq.generate.mutiny;

import io.r2mo.vertx.jooq.generate.builder.DelegatingVertxGenerator;
import io.r2mo.vertx.jooq.generate.builder.VertxGeneratorBuilder;

/**
 * Created by jensklingsporn on 06.02.18.
 */
public class MutinyJDBCVertxGenerator extends DelegatingVertxGenerator {

    public MutinyJDBCVertxGenerator() {
        super(VertxGeneratorBuilder.init().withMutinyAPI().withJDBCDriver().build());
    }
}
