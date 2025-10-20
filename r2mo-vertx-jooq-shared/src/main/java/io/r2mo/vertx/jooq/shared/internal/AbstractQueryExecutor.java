package io.r2mo.vertx.jooq.shared.internal;

import org.jooq.Attachable;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;

import java.util.function.Function;

/**
 * @author jensklingsporn
 */
public class AbstractQueryExecutor implements Attachable {

    private Configuration configuration;

    public AbstractQueryExecutor(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void attach(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void detach() {
        this.attach(null);
    }

    @Override
    public Configuration configuration() {
        return this.configuration;
    }

    protected <T extends Query> T createQuery(final Function<DSLContext, T> queryFunction) {
        return queryFunction.apply(DSL.using(this.configuration()));
    }

}
