package io.github.jklingsporn.vertx.jooq.generate;

/**
 * @author lang : 2025-10-20
 */
public class JooqSourceGenerator {

    private static final JooqSourceGenerator INSTANCE = new JooqSourceGenerator();

    private JooqSourceGenerator() {
    }

    public static JooqSourceGenerator of() {
        return INSTANCE;
    }

    public void generate(final JooqSourceConfiguration configuration) {
        
    }
}
